package com.ibm.portal.samples.mail.common;

import static com.ibm.portal.portlet.service.credentialvault.CredentialTypes.USER_PASSWORD_PASSIVE;

import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.portlet.PortletRequest;

import com.ibm.portal.Disposable;
import com.ibm.portal.ObjectID;
import com.ibm.portal.portlet.service.credentialvault.CredentialSlotConfig;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultException;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultService;
import com.ibm.portal.portlet.service.credentialvault.credentials.UserPasswordPassiveCredential;
import com.ibm.portal.samples.mail.helper.Pair;
import com.ibm.portal.um.PumaHome;
import com.ibm.portal.um.PumaProfile;
import com.ibm.portal.um.User;
import com.ibm.websphere.cache.DistributedMap;
import com.ibm.wsspi.cache.DistributedObjectCacheFactory;

/**
 * Base class for the beans used for the different mail portlets. Contains some
 * general purpose functions to access mail.
 * 
 * @author cleue
 * 
 */
public class AbstractBean implements Disposable, Cloneable {

	public interface Dependencies {

		CredentialVaultService getCredentialVaultService();

		PumaHome getPumaHome();
	}

	/**
	 * the mail session
	 */
	private Session mailSession;

	/**
	 * the mail store
	 */
	private Store mailStore;

	/**
	 * the mail authenticator
	 */
	private Authenticator authenticator;

	/** class name for the logger */
	private static final String LOG_CLASS = AbstractBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * the central cache instance
	 */
	private static final DistributedMap CACHE = DistributedObjectCacheFactory
			.getMap(AbstractBean.class.getName());

	/**
	 * access to the puma services
	 */
	private final PumaHome pumaHome;

	/**
	 * the current user
	 */
	private ObjectID userID;

	/**
	 * key into the cache for the session
	 */
	private static final Object KEY_SESSION = new Object();

	/**
	 * key into the cache for the store
	 */
	private static final Object KEY_STORE = new Object();

	/**
	 * key into the message cache
	 */
	private static final Object KEY_MESSAGE = new Object();

	/**
	 * cache key part for the folder
	 */
	private static final Object KEY_FOLDER = new Object();

	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * access to the credential vault
	 */
	private final CredentialVaultService credentialVaultService;

	/**
	 * we use this slot identifier
	 */
	protected static final String CREDENTIAL_SLOT_DESCRIPTION = "com.ibm.portal.sample.portlet.mail.common.AbstractBean";

	/**
	 * our credential slot configuration
	 */
	private CredentialSlotConfig slotConfig;

	/**
	 * the portlet request
	 */
	private final PortletRequest request;

	/**
	 * the default locale
	 */
	protected static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	/**
	 * our credential
	 */
	private UserPasswordPassiveCredential credential;

	protected AbstractBean(final PortletRequest aRequest,
			final Dependencies aDeps) {
		pumaHome = aDeps.getPumaHome();
		credentialVaultService = aDeps.getCredentialVaultService();
		request = aRequest;
	}

	/**
	 * Returns a new message that we can send
	 * 
	 * @return the message
	 * 
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public MimeMessage createMessage() throws AddressException,
			MessagingException {
		try {
			// construct the new message
			final MimeMessage message = new MimeMessage(getSession());
			message.setFrom(new InternetAddress(getUserMail()));
			// ok
			return message;
		} catch (CredentialVaultException ex) {
			// bail out
			throw new MessagingException(ex.getLocalizedMessage(), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// cleanup if required
	}

	/**
	 * Returns the authentication mechanism
	 * 
	 * @return the authenticator
	 */
	private final Authenticator getAuthenticator() {
		// logging support
		final String LOG_METHOD = "getAuthenticator()";
		// lazily create the authenticator
		if (authenticator == null) {
			authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// log this
					if (bIsLogging) {
						LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
								"Authenticating ...");
					}
					try {
						// password authentication
						return new PasswordAuthentication(getUserMail(),
								getPassword());
					} catch (CredentialVaultException ex) {
						// just log
						logException(ex);
						// no password provided
						return null;
					}
				}
			};
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Constructed the authenticator ...");
			}
		}
		// ok
		return authenticator;
	}

	/**
	 * Returns the slot for this mail application or <code>null</code> if no
	 * slot exists
	 * 
	 * @return the slot or <code>null</code>
	 * 
	 * @throws CredentialVaultException
	 */
	private final UserPasswordPassiveCredential getCredential()
			throws CredentialVaultException {
		// logging support
		final String LOG_METHOD = "getCredential()";
		// lazily load the mail credential
		if (credential == null) {
			// find the slot
			final CredentialSlotConfig slot = getCredentialSlot();
			if (slot != null) {
				// load the credential from the configuration
				credential = (UserPasswordPassiveCredential) credentialVaultService
						.getCredential(slot.getSlotId(), USER_PASSWORD_PASSIVE,
								null, request);
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Found the credential [{0}].", credential);
				}
			}
		}
		// returns the credential
		return credential;
	}

	/**
	 * Locates the credential slot for our application
	 * 
	 * @param portletRequest
	 *            the request
	 * @return the credential slot or <code>null</code>
	 * @throws CredentialVaultException
	 */
	protected final CredentialSlotConfig getCredentialSlot()
			throws CredentialVaultException {
		// sanity check
		if (slotConfig == null) {
			// use vault service to get all accessible slots
			@SuppressWarnings("unchecked")
			final Iterator<? extends CredentialSlotConfig> slotIter = credentialVaultService
					.getAccessibleSlots(request);
			while (slotIter.hasNext()) {
				// try the slot
				slotConfig = slotIter.next();
				// use vault service to get the slot's description;
				// alternatively use:
				// slot.getVaultSlotDescription(Locale.ENGLISH)
				final String slotDescription = credentialVaultService
						.getCredentialSlotDescription(slotConfig.getSlotId(),
								DEFAULT_LOCALE);
				// very simple search "strategy"
				if (CREDENTIAL_SLOT_DESCRIPTION.equals(slotDescription)) {
					return slotConfig;
				}
				// reset
				slotConfig = null;
			}
		}
		// no slot found
		return slotConfig;
	}

	/**
	 * Returns the ID of the current user
	 * 
	 * @return the current user
	 */
	public ObjectID getCurrentUser() {
		// logging support
		final String LOG_METHOD = "getCurrentUser()";
		// lazily access the current user
		if (userID == null) {
			try {
				// access the profile
				final PumaProfile profile = pumaHome.getProfile();
				if (profile != null) {
					// puma user
					final User currentUser = profile.getCurrentUser();
					if (currentUser != null) {
						// access the current user
						userID = currentUser.getObjectID();
					}
				}
			} catch (final Exception ex) {
				// just log
				logException(ex);
				// no user available
				userID = null;
			}
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Current user [{0}].", userID);
			}
		}
		// returns the user ID
		return userID;
	}

	/**
	 * Access the folder in question
	 * 
	 * @param aName
	 *            name of the folder
	 * @return the folder
	 * 
	 * @throws MessagingException
	 */
	public Folder getFolder(final String aName) throws MessagingException {
		// logging support
		final String LOG_METHOD = "getFolder(aName)";
		// cache key
		final Object key = Pair.pair(getCurrentUser(),
				Pair.pair(aName, KEY_FOLDER));
		// check if we already know the folder
		Folder folder = (Folder) CACHE.get(key);
		if (folder == null) {
			// access the store
			final Store store = getStore();
			assert store != null;
			// access the folder if connected
			if (store.isConnected()) {
				// resolve the folder
				folder = store.getFolder(aName);
				// update the cache
				CACHE.put(key, folder);
			}
		} else {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Accessing folder from cache ...");
			}
		}
		// ok
		return folder;
	}

	/**
	 * Constructs the cache key for a message identifier that is user dependent
	 * 
	 * @param aID
	 *            the ID of the message
	 * @return the key
	 */
	private final Object getKey(final long aID) {
		return Pair.pair(getCurrentUser(), Pair.pair(aID, KEY_MESSAGE));
	}

	/**
	 * Accesses the message from the cache
	 * 
	 * @param aID
	 *            unique identifier
	 * @return the message
	 */
	public MessageBean getMessage(final long aID) {
		return (MessageBean) CACHE.get(getKey(aID));
	}

	/**
	 * Computes a unique message identifier
	 * 
	 * @param aMessage
	 *            the message
	 * @param aFolder
	 *            folder
	 * @return the unique identifier
	 * 
	 * @throws MessagingException
	 */
	public long getMessageID(final Message aMessage, final UIDFolder aFolder)
			throws MessagingException {
		// resolve the UID
		return aFolder.getUID(aMessage);
	}

	/**
	 * Returns the password from the credential
	 * 
	 * @return the password
	 * @throws CredentialVaultException
	 */
	private final String getPassword() throws CredentialVaultException {
		// check the credential
		final UserPasswordPassiveCredential credential = getCredential();
		return (credential != null) ? String.valueOf(credential.getPassword())
				: null;

	}

	/**
	 * returns the mail session
	 * 
	 * @return the session or <code>null</code>
	 */
	public Session getSession() {
		// logging support
		final String LOG_METHOD = "getSession()";
		if (mailSession == null) {
			// cache key
			final Object key = Pair.pair(getCurrentUser(), KEY_SESSION);
			// look for the cached value
			mailSession = (Session) CACHE.get(key);
			if (mailSession == null) {
				// returns the session
				final Properties props = new Properties(System.getProperties());
				props.setProperty("mail.store.protocol", "imaps");
				props.setProperty("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.auth", "true");
				props.setProperty("mail.smtp.host", "smtp.gmail.com");
				props.setProperty("mail.imaps.host", "imap.gmail.com");
				props.setProperty("mail.imaps.messagecache.debug", "true");
				props.setProperty("mail.smtp.port", "587");
				// construct the session
				mailSession = Session.getInstance(props, getAuthenticator());
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Accessing mail session [{0}].", mailSession);
				}
				// update the cache
				CACHE.put(key, mailSession);
			} else {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Accessing session from cache ...");
				}
			}
		}
		// returns the mail session
		return mailSession;
	}

	/**
	 * Access the mail store
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public Store getStore() throws MessagingException {
		// logging support
		final String LOG_METHOD = "getStore()";
		if (mailStore == null) {
			// cache key
			final Object key = Pair.pair(getCurrentUser(), KEY_STORE);
			// check for the store
			mailStore = (Store) CACHE.get(key);
			if ((mailStore == null) || !mailStore.isConnected()) {
				// lazily access the mail store
				mailStore = getSession().getStore("imaps");
				mailStore.connect();
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Accessed store [{0}].", mailStore);
				}
				// update the cache
				CACHE.put(key, mailStore);
			} else {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Accessing store from cache ...");
				}
			}
		}
		// returns the store
		return mailStore;
	}

	/**
	 * Returns the username from the credential
	 * 
	 * @return the username
	 * @throws CredentialVaultException
	 */
	private final String getUserMail() throws CredentialVaultException {
		// check the credential
		final UserPasswordPassiveCredential credential = getCredential();
		return (credential != null) ? credential.getUserId() : null;
	}

	/**
	 * Just some logging in case we do not rethrow the exception
	 * 
	 * @param ex
	 */
	protected void logException(final Throwable ex) {
		ex.printStackTrace();
	}

	/**
	 * Returns a message bean that represents the message, potentially from the
	 * cache.
	 * 
	 * @param aID
	 *            the message identity
	 * @param aMessage
	 *            the message
	 * @return the bean
	 */
	public MessageBean resolveMessage(final long aID, final Message aMessage) {
		// logging support
		final String LOG_METHOD = "resolveMessage(aID, aMessage)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aID });
		}
		// construct the cache key
		final Object key = getKey(aID);
		MessageBean resolved = (MessageBean) CACHE.get(key);
		if (resolved == null) {
			resolved = new MessageBean(aID, aMessage);
			CACHE.put(key, resolved);
		} else {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Using cached message ...");
			}
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// ok
		return resolved;
	}

	/**
	 * Sends the message
	 * 
	 * @param aMessage
	 *            the message
	 * 
	 * @throws MessagingException
	 */
	public void sendMessage(final Message aMessage) throws MessagingException {
		// deliver the message
		Transport.send(aMessage);
	}
}