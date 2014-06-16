package com.ibm.portal.samples.mail.list;

import static com.ibm.portal.resolver.data.CharDataSource.CONTENT_TYPE_TEXT;
import static com.ibm.portal.samples.mail.common.Constants.CREDENTIAL_VAULT_JNDI_NAME;
import static javax.mail.Message.RecipientType.TO;
import static javax.portlet.PortletMode.VIEW;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;

import org.w3c.dom.Element;

import com.ibm.portal.portlet.service.PortletServiceHome;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultService;
import com.ibm.portal.samples.common.ErrorBean;
import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.common.PrivateParameterMarshaller;
import com.ibm.portal.samples.common.PublicParameterMarshaller;
import com.ibm.portal.samples.mail.common.AbstractPortlet;
import com.ibm.portal.samples.mail.common.SendEventBean;
import com.ibm.portal.samples.mail.list.controller.MailListController;
import com.ibm.portal.samples.mail.list.model.MailListActions;
import com.ibm.portal.samples.mail.list.model.MailListBean;
import com.ibm.portal.samples.mail.list.model.MailListModel;
import com.ibm.portal.samples.mail.list.view.MailListView;
import com.ibm.portal.um.PumaHome;

/**
 * Implementation of the portlet that displays a customer list. The portlet's
 * view is realized as a JSP. Access to the data model and URLs is made
 * available via beans, the {@link MailListModel} bean and the
 * {@link MailListController} bean.
 * 
 * @author cleue
 */
public class MailListPortlet extends AbstractPortlet {

	/**
	 * Depenendency resolution of the models
	 * 
	 * @author cleue
	 */
	private final class Dependencies implements MailListBean.Dependencies,
			MailListModel.Dependencies, MailListController.Dependencies,
			MailListActions.Dependencies {

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
	private static final String LOG_CLASS = MailListPortlet.class.getName();

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
			+ MailListPortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * prefix for public resources
	 */
	private static final String PUBLIC_PATH_PREFIX = "/"
			+ MailListPortlet.class.getPackage().getName().replace('.', '/')
			+ "/";

	/**
	 * path to the unauthenticated JSP
	 */
	private static final String UNAUTHENTICATED_JSP = PRIVATE_PATH_PREFIX
			+ "unauthenticated" + PATH_SUFFIX;

	/**
	 * path to the AJAX JSP
	 */
	private static final String AJAX_JSP = PRIVATE_PATH_PREFIX + "ajax"
			+ PATH_SUFFIX;

	/**
	 * path to the edit JSP
	 */
	private static final String EDIT_JSP = PRIVATE_PATH_PREFIX + "edit"
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
	private final MailListActions createActions(final MailListModel aModel,
			final MailListBean aBean, final ActionRequest aRequest,
			final ActionResponse aResponse) throws PortletException,
			IOException {
		// sanity check
		assert aModel != null;
		assert aBean != null;
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Decodes the action.This method normally does not have to be changed.
		 * Rather change the implementation of the action.
		 */
		return new MailListActions(aModel, aBean, aRequest, aResponse,
				dependencies);
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
	 * Initialize the beans and make them available as request parameters
	 * 
	 * @param aRequest
	 *            request
	 * @param aResponse
	 *            response
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
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}

		// start time
		final long t1 = System.currentTimeMillis();

		// initialize the beans
		initBeans(request, response);
		try {
			// init the response
			response.setContentType(request.getResponseContentType());
			// check which JSP to render
			final MailListModel model = getModel(request);
			// the JSP
			final String jspPath;
			// check for the unauthenticated case
			if (model.isLoggedIn()) {
				// dispatch based on the mode
				final PortletMode mode = request.getPortletMode();
				if (model.isAuthenticated() && (mode == VIEW)) {
					// use the ajax path
					jspPath = AJAX_JSP;
				} else {
					// use the mode based path
					jspPath = EDIT_JSP;
				}
			} else {
				// use the mode based path
				jspPath = UNAUTHENTICATED_JSP;
			}
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Including JSP [{0}].", jspPath);
			}
			// dispatch
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
	 * Accesses the current bean from the request
	 * 
	 * @param aRequest
	 *            the request
	 * @return the current bean
	 */
	private final MailListBean getBean(final PortletRequest aRequest) {
		return (MailListBean) aRequest.getAttribute(KEY_BEAN);
	}

	/**
	 * Accesses the current model from the request
	 * 
	 * @param aRequest
	 *            the request
	 * @return the current model
	 */
	private final MailListModel getModel(final PortletRequest aRequest) {
		return (MailListModel) aRequest.getAttribute(KEY_MODEL);
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
		final MailListBean bean = new MailListBean(aRequest, dependencies);
		final MailListModel model = new MailListModel(bean, aRequest,
				dependencies);
		final MailListView view = new MailListView(getPortletConfig(),
				aRequest, aResponse, model);
		final MailListController controller = new MailListController(model,
				aResponse, dependencies);
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
		final MailListBean bean = new MailListBean(aRequest, dependencies);
		final MailListModel model = new MailListModel(bean, aRequest,
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
		final MailListModel model = getModel(request);
		// construct the action handler
		final MailListActions actions = createActions(model, getBean(request),
				request, response);
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
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// clear previous errors
		ErrorBean.clear(request);
		// init the beans
		initBeans(request, response);
		// access the model
		final MailListModel model = getModel(request);
		try {
			// interpret the event
			final Event event = request.getEvent();
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Received event [{0}].", event.getQName());
			}
			// interpret the event
			if (SendEventBean.EVENT_NAME.equals(event.getQName())) {
				// access the payload
				final SendEventBean sendBean = (SendEventBean) event.getValue();
				// access the helper bean
				final MailListBean bean = getBean(request);
				// construct the message
				final Message message = bean.createMessage();
				message.setRecipients(TO,
						InternetAddress.parse(sendBean.getAddress()));
				message.setSubject(sendBean.getSubject());
				message.setText(sendBean.getText());
				// send
				bean.sendMessage(message);
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Sent message to [{0}].", sendBean.getAddress());
				}
			}
		} catch (final Throwable ex) {
			// adds the error
			ErrorBean.setThrowable(ex, request);
		} finally {
			// in any case encode the model
			model.encode(response);
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
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// decode the model
		initBeans(request, response);
		try {
			// access the model
			final MailListModel model = getModel(request);
			// check the cases
			if (model.isCheckMail()) {
				/**
				 * We simply print out the number of messages. This is not
				 * always correct, but for a demo ...
				 */
				response.setContentType(CONTENT_TYPE_TEXT);
				response.setProperty("Cache-Control", "must-revalidate");
				response.getWriter().println(model.getItemCount());

			} else if (model.isRefresh()) {
				// produce a result
				response.setContentType("text/html");
				response.setProperty("Cache-Control", "must-revalidate");
				// dispatch to the view JSP
				final String jspPath = PRIVATE_PATH_PREFIX
						+ request.getPortletMode() + PATH_SUFFIX;
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Including JSP [{0}].", jspPath);
				}
				// dispatch
				getPortletContext().getRequestDispatcher(jspPath).include(
						request, response);
			}
		} finally {
			// cleanup
			destroyBeans(request);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

}
