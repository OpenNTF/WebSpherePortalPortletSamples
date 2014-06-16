package com.ibm.portal.samples.mail.compose;

import static com.ibm.portal.samples.mail.common.Constants.CREDENTIAL_VAULT_JNDI_NAME;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.StateAwareResponse;

import org.w3c.dom.Element;

import com.ibm.portal.portlet.service.PortletServiceHome;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultService;
import com.ibm.portal.samples.common.ErrorBean;
import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.common.PrivateParameterMarshaller;
import com.ibm.portal.samples.common.PublicParameterMarshaller;
import com.ibm.portal.samples.mail.common.AbstractPortlet;
import com.ibm.portal.samples.mail.compose.controller.MailComposeController;
import com.ibm.portal.samples.mail.compose.model.MailComposeActions;
import com.ibm.portal.samples.mail.compose.model.MailComposeBean;
import com.ibm.portal.samples.mail.compose.model.MailComposeEvents;
import com.ibm.portal.samples.mail.compose.model.MailComposeModel;
import com.ibm.portal.samples.mail.compose.view.MailComposeView;
import com.ibm.portal.um.PumaHome;

/**
 * Implementation of the portlet that displays a customer list. The portlet's
 * view is realized as a JSP. Access to the data model and URLs is made
 * available via beans, the {@link MailComposeModel} bean and the
 * {@link MailComposeController} bean.
 * 
 * @author cleue
 */
public class MailComposePortlet extends AbstractPortlet {

	/**
	 * Depenendency resolution of the models
	 * 
	 * @author cleue
	 */
	private final class Dependencies implements MailComposeBean.Dependencies,
			MailComposeModel.Dependencies, MailComposeController.Dependencies,
			MailComposeActions.Dependencies {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.ibm.portal.sample.portlet.mail.common.AbstractBean.Dependencies#
		 * getCredentialVaultService()
		 */
		@Override
		public CredentialVaultService getCredentialVaultService() {
			// access to the credential vault
			return credentialVaultService;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.portal.samples.common.AbstractModel.Dependencies#
		 * getPrivateParameterMarshaller()
		 */
		@Override
		public Marshaller getPrivateParameterMarshaller() {
			// return the standard implementation
			return PrivateParameterMarshaller.SINGLETON;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.portal.samples.common.AbstractModel.Dependencies#
		 * getPublicParameterMarshaller()
		 */
		@Override
		public Marshaller getPublicParameterMarshaller() {
			// return the standard implementation
			return PublicParameterMarshaller.SINGLETON;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.ibm.portal.sample.portlet.mail.common.AbstractBean.Dependencies#
		 * getPumaHome()
		 */
		@Override
		public PumaHome getPumaHome() {
			// access to the puma APIs
			return pumaHome;
		}

	}

	/**
	 * our portlet bean
	 */
	private static final String KEY_BEAN = "bean";

	/**
	 * our controller bean
	 */
	private static final String KEY_CONTROLLER = "controller";

	/**
	 * our model bean
	 */
	private static final String KEY_MODEL = "model";

	/**
	 * our view bean
	 */
	private static final String KEY_VIEW = "view";

	/** class name for the logger */
	private static final String LOG_CLASS = MailComposePortlet.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * suffix for the JSP path
	 */
	private static final String PATH_SUFFIX = ".jsp";

	/**
	 * prefix for the JSP path
	 */
	private static final String PRIVATE_PATH_PREFIX = "/WEB-INF/resources/"
			+ MailComposePortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * prefix for public resources
	 */
	private static final String PUBLIC_PATH_PREFIX = "/resources/"
			+ MailComposePortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * access to the credential vault
	 */
	private CredentialVaultService credentialVaultService;

	/**
	 * dependency injection
	 */
	private final Dependencies dependencies = new Dependencies();

	/**
	 * access to the puma APIs
	 */
	private PumaHome pumaHome;

	/**
	 * Constructs the action handler
	 * 
	 * @param aModel
	 *            model the actions will work on
	 * @param aRequest
	 *            the action request
	 * @param aResponse
	 *            the action response
	 * @return the model
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	private final MailComposeActions createActions(
			final MailComposeModel aModel, final ActionRequest aRequest,
			final ActionResponse aResponse) throws PortletException,
			IOException {
		// sanity check
		assert aModel != null;
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Decodes the action.This method normally does not have to be changed.
		 * Rather change the implementation of the action.
		 */
		return new MailComposeActions(aModel, aRequest, aResponse, dependencies);
	}

	/**
	 * Constructs the action handler
	 * 
	 * @param aModel
	 *            model the actions will work on
	 * @param aRequest
	 *            the action request
	 * @param aResponse
	 *            the action response
	 * @return the model
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	private final MailComposeEvents createEvents(final MailComposeModel aModel,
			final EventRequest aRequest, final EventResponse aResponse)
			throws PortletException, IOException {
		// sanity check
		assert aModel != null;
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Decodes the action.This method normally does not have to be changed.
		 * Rather change the implementation of the action.
		 */
		return new MailComposeEvents(aModel, aRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#destroy()
	 */
	@Override
	public void destroy() {
		// logging support
		final String LOG_METHOD = "destroy()";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// reset
		pumaHome = null;
		credentialVaultService = null;
		// shutdown
		super.destroy();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Cleanup of the beans
	 * 
	 * @param aRequest
	 *            request
	 */
	private final void destroyBeans(final PortletRequest aRequest) {
		// add the beans to the request
		removeBean(KEY_BEAN, aRequest);
		removeBean(KEY_MODEL, aRequest);
		removeBean(KEY_VIEW, aRequest);
		removeBean(KEY_CONTROLLER, aRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doHeaders(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	protected void doHeaders(final RenderRequest request,
			final RenderResponse response) {
		// logging support
		final String LOG_METHOD = "doHeaders(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// default
		super.doHeaders(request, response);
		// compute the URL to the stylesheet
		final String cssRef = response.encodeURL(request.getContextPath()
				+ PUBLIC_PATH_PREFIX + "styles.css");
		// log this
		if (bIsLogging) {
			LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
					"Adding stylesheet [{0}].", cssRef);
		}
		// add the stylesheet
		final Element element = response.createElement("link");
		element.setAttribute("rel", "stylesheet");
		element.setAttribute("type", "text/css");
		element.setAttribute("href", cssRef);
		response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(final RenderRequest request,
			final RenderResponse response) throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "doView(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}

		// start time
		final long t1 = System.currentTimeMillis();

		// initialize the beans
		initBeans(request, response);
		try {
			// init the response
			response.setContentType(request.getResponseContentType());

			// locate the JSP
			final String jspPath = PRIVATE_PATH_PREFIX
					+ getJsp(request, response);

			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Including JSP [{0}].", jspPath);
			}
			getPortletContext().getRequestDispatcher(jspPath).include(request,
					response);
		} finally {
			// cleanup
			destroyBeans(request);
		}

		// end time
		final long t2 = System.currentTimeMillis();

		System.out.println(getClass() + ": " + (t2 - t1) + "[ms]");

		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Decodes the name of the JSP from the request. This name does NOT contain
	 * the path up to the resource folder for the portlet. This will be added
	 * later
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the respone
	 * @return relative path to the JSP
	 */
	protected String getJsp(final RenderRequest request,
			final RenderResponse response) {
		// logging support
		final String LOG_METHOD = "getJsp(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// guess the JSP name
		final String key = request.getPortletMode() + PATH_SUFFIX;
		// try to find an override, fallback to our guess
		final PortletPreferences prefs = request.getPreferences();
		final String jspName = prefs.getValue(key, key);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, jspName);
		}
		// ok
		return jspName;
	}

	/**
	 * Accesses the current model from the request
	 * 
	 * @param aRequest
	 *            the request
	 * @return the current model
	 */
	private final MailComposeModel getModel(final PortletRequest aRequest) {
		return (MailComposeModel) aRequest.getAttribute(KEY_MODEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#init(javax.portlet.PortletConfig)
	 */
	@Override
	public void init(final PortletConfig config) throws PortletException {
		// logging support
		final String LOG_METHOD = "init(config)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { config });
		}
		// default
		super.init(config);
		try {
			// service lookup via JNDI
			final InitialContext ctx = new InitialContext();
			pumaHome = (PumaHome) ctx.lookup(PumaHome.JNDI_NAME);

			PortletServiceHome psh = (PortletServiceHome) ctx
					.lookup(CREDENTIAL_VAULT_JNDI_NAME);
			credentialVaultService = psh
					.getPortletService(CredentialVaultService.class);
		} catch (final NamingException ex) {
			// bail out
			throw new PortletException(ex);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Initialize the beans and make them available as request parameters
	 * 
	 * @param aRequest
	 *            request
	 * @param aResponse
	 *            response
	 */
	private final void initBeans(final PortletRequest aRequest,
			final MimeResponse aResponse) {
		// initialize the beans
		final MailComposeBean bean = new MailComposeBean(aRequest, dependencies);
		final MailComposeModel model = new MailComposeModel(bean, aRequest,
				dependencies);
		final MailComposeView view = new MailComposeView(getPortletConfig(),
				aRequest, aResponse, model);
		final MailComposeController controller = new MailComposeController(
				model, aResponse, dependencies);
		// add the beans to the request
		setBean(KEY_BEAN, bean, aRequest);
		setBean(KEY_MODEL, model, aRequest);
		setBean(KEY_VIEW, view, aRequest);
		setBean(KEY_CONTROLLER, controller, aRequest);
	}

	/**
	 * Initialize the beans and make them available as request parameters
	 * 
	 * @param aRequest
	 *            request
	 * @param aResponse
	 *            response
	 */
	private final void initBeans(final PortletRequest aRequest,
			final StateAwareResponse aResponse) {
		// initialize the beans
		final MailComposeBean bean = new MailComposeBean(aRequest, dependencies);
		final MailComposeModel model = new MailComposeModel(bean, aRequest,
				dependencies);
		// add the beans to the request
		setBean(KEY_BEAN, bean, aRequest);
		setBean(KEY_MODEL, model, aRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest,
	 * javax.portlet.ActionResponse)
	 */
	@Override
	public void processAction(final ActionRequest request,
			final ActionResponse response) throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "processAction(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// clear previous errors
		ErrorBean.clear(request);
		// initialize the beans
		initBeans(request, response);
		// decode the model
		final MailComposeModel model = getModel(request);
		// construct the action handler
		final MailComposeActions actions = createActions(model, request,
				response);
		try {
			// process the model
			if (actions.processActions()) {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Committing the model ...");
				}
				// commit persistent modifications
				actions.commit();
			}
		} catch (final Throwable ex) {
			// adds the exception to the session
			ErrorBean.setThrowable(ex, request);
		} finally {
			/**
			 * Encodes the model. This is an important step, without it the
			 * navigational state would be lost after the action.
			 */
			model.encode(response);
			// dispose
			actions.dispose();
			// cleanup
			destroyBeans(request);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.portlet.GenericPortlet#processEvent(javax.portlet.EventRequest,
	 * javax.portlet.EventResponse)
	 */
	@Override
	public void processEvent(final EventRequest request,
			final EventResponse response) throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "processEvent(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// clear previous errors
		ErrorBean.clear(request);
		// initialize the beans
		initBeans(request, response);
		// decode the model
		final MailComposeModel model = getModel(request);
		// construct the action handler
		final MailComposeEvents events = createEvents(model, request, response);
		try {
			// process the model
			if (events.processEvents()) {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Committing the model ...");
				}
				// commit persistent modifications
				events.commit();
			}
		} catch (final Throwable ex) {
			// adds the exception to the session
			ErrorBean.setThrowable(ex, request);
		} finally {
			/**
			 * Encodes the model. This is an important step, without it the
			 * navigational state would be lost after the action.
			 */
			model.encode(response);
			// dispose
			events.dispose();
			// cleanup
			destroyBeans(request);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}
}
