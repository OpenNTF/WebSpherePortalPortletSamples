package com.ibm.portal.samples.mail.view;

import static com.ibm.portal.samples.mail.common.Constants.CREDENTIAL_VAULT_JNDI_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;

import org.w3c.dom.Element;

import com.ibm.portal.portlet.service.PortletServiceHome;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultService;
import com.ibm.portal.resolver.service.CorPocServiceHome;
import com.ibm.portal.resolver.xml.PooledTemplates;
import com.ibm.portal.samples.common.ErrorBean;
import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.common.PrivateParameterMarshaller;
import com.ibm.portal.samples.common.PublicParameterMarshaller;
import com.ibm.portal.samples.mail.common.AbstractPortlet;
import com.ibm.portal.samples.mail.common.MessageBean;
import com.ibm.portal.samples.mail.view.controller.MailViewController;
import com.ibm.portal.samples.mail.view.model.MailViewActions;
import com.ibm.portal.samples.mail.view.model.MailViewBean;
import com.ibm.portal.samples.mail.view.model.MailViewModel;
import com.ibm.portal.samples.mail.view.view.MailViewView;
import com.ibm.portal.um.PumaHome;

/**
 * Implementation of the portlet that displays a customer list. The portlet's
 * view is realized as a JSP. Access to the data model and URLs is made
 * available via beans, the {@link MailViewModel} bean and the
 * {@link MailViewController} bean.
 * 
 * @author cleue
 */
public class MailViewPortlet extends AbstractPortlet {

	/**
	 * Depenendency resolution of the models
	 * 
	 * @author cleue
	 */
	private final class Dependencies implements MailViewBean.Dependencies,
			MailViewView.Dependencies, MailViewModel.Dependencies,
			MailViewActions.Dependencies, MailViewController.Dependencies {

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
		 * @see
		 * com.ibm.portal.sample.portlet.mail.view.MessageParser.Dependencies#
		 * getIdentityTemplates()
		 */
		@Override
		public PooledTemplates getIdentityTemplates() {
			// convenient access to the templates
			return pocHome.getIdentityTemplates();
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
	private static final String LOG_CLASS = MailViewPortlet.class.getName();

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
	private static final String PRIVATE_PATH_PREFIX = "/WEB-INF/"
			+ MailViewPortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * prefix for public resources
	 */
	private static final String PUBLIC_PATH_PREFIX = "/"
			+ MailViewPortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * path to the AJAX JSP
	 */
	private static final String AJAX_JSP = PRIVATE_PATH_PREFIX + "ajax"
			+ PATH_SUFFIX;

	/**
	 * access to the credential vault
	 */
	private CredentialVaultService credentialVaultService;

	/**
	 * dependency injection
	 */
	private final Dependencies dependencies = new Dependencies();

	/**
	 * access to the templates
	 */
	private CorPocServiceHome pocHome;

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
	private final MailViewActions createActions(final MailViewModel aModel,
			final ActionRequest aRequest, final ActionResponse aResponse)
			throws PortletException, IOException {
		// sanity check
		assert aModel != null;
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Decodes the action.This method normally does not have to be changed.
		 * Rather change the implementation of the action.
		 */
		return new MailViewActions(aModel, aRequest, aResponse, dependencies);
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
		pocHome = null;
		credentialVaultService = null;
		// shutdown
		super.destroy();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

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

		// init the beans in the request
		initBeans(request, response);
		try {
			// init the response
			response.setContentType(request.getResponseContentType());
			// check if the system is authenticated
			final MailViewModel model = getModel(request);
			if (model.isAuthenticated()) {
				// lazy load of the view
				getPortletContext().getRequestDispatcher(AJAX_JSP).include(
						request, response);
			} else {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Skip rendering of the view because the backend is not authenticated.");
				}
			}
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
	 * Accesses the current model from the request
	 * 
	 * @param aRequest
	 *            the request
	 * @return the current model
	 */
	private final MailViewModel getModel(final PortletRequest aRequest) {
		return (MailViewModel) aRequest.getAttribute(KEY_MODEL);
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
			pocHome = (CorPocServiceHome) ctx
					.lookup(CorPocServiceHome.JNDI_NAME);

			final PortletServiceHome psh = (PortletServiceHome) ctx
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
		final MailViewBean bean = new MailViewBean(aRequest, dependencies);
		final MailViewModel model = new MailViewModel(bean, aRequest,
				dependencies);
		final MailViewController controller = new MailViewController(model,
				aResponse, dependencies);
		final MailViewView view = new MailViewView(getPortletConfig(),
				aRequest, aResponse, model, controller, dependencies);
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
		final MailViewBean bean = new MailViewBean(aRequest, dependencies);
		final MailViewModel model = new MailViewModel(bean, aRequest,
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
		final MailViewModel model = getModel(request);
		// construct the action handler
		final MailViewActions actions = createActions(model, request, response);
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

	/**
	 * Copies the bytes for an embedded resource onto the output stream.
	 * 
	 * @param messageResource
	 * @param message
	 * @param request
	 * @param response
	 * 
	 * @throws MessagingException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private final void serveMessageResource(final String messageResource,
			final MessageBean message, final ResourceRequest request,
			final ResourceResponse response) throws MessagingException,
			IOException, URISyntaxException {
		// logging support
		final String LOG_METHOD = "serveMessageResource(messageResource, message, request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] {
					messageResource, message, request, response });
		}
		// locate the part
		final Part part = message.getPartByID(messageResource);
		if (part != null) {
			// serve the part
			response.setContentType(part.getContentType());
			// copy
			final byte[] buffer = new byte[256];
			final InputStream is = part.getInputStream();
			final OutputStream os = response.getPortletOutputStream();
			for (int i = is.read(buffer); i >= 0; i = is.read(buffer)) {
				os.write(buffer, 0, i);
			}
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
	 * javax.portlet.GenericPortlet#serveResource(javax.portlet.ResourceRequest,
	 * javax.portlet.ResourceResponse)
	 */
	@Override
	public void serveResource(final ResourceRequest request,
			final ResourceResponse response) throws PortletException,
			IOException {
		// logging support
		final String LOG_METHOD = "serveResource(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// init the beans in the request
		initBeans(request, response);
		try {
			// check if we server an image or the message
			final MailViewModel model = getModel(request);
			// check if the model addresses content
			final String messageResource = model.getMessageResource();
			if (messageResource != null) {
				// access the currently selected message
				final MessageBean msgBean = model.getSelectedMessage();
				if (msgBean != null) {
					// log this
					if (bIsLogging) {
						LOGGER.logp(
								LOG_LEVEL,
								LOG_CLASS,
								LOG_METHOD,
								"Addressing resource [{0}] for message [{1}].",
								new Object[] { messageResource, msgBean.getId() });
					}
					// serve this
					serveMessageResource(messageResource, msgBean, request,
							response);
				}
			} else {
				// subsequent links should again be AJAX
				model.setRenderMessage(false);
				request.setAttribute("renderMessage", true);
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Rendering message content.");
				}
				// locate the JSP
				final String jspPath = PRIVATE_PATH_PREFIX
						+ request.getPortletMode() + PATH_SUFFIX;
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Including JSP [{0}].", jspPath);
				}
				getPortletContext().getRequestDispatcher(jspPath).include(
						request, response);
			}
		} catch (final Exception ex) {
			// bail out
			throw new PortletException(ex);
		} finally {
			// done
			destroyBeans(request);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

}
