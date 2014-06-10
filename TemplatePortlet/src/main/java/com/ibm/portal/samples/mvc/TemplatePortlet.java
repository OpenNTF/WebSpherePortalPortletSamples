/*
 * (C) Copyright IBM Corp. 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.portal.samples.mvc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.common.PrivateParameterMarshaller;
import com.ibm.portal.samples.mvc.controller.TemplateController;
import com.ibm.portal.samples.mvc.model.TemplateActions;
import com.ibm.portal.samples.mvc.model.TemplateModel;
import com.ibm.portal.samples.mvc.view.TemplateView;

/**
 * Implementation of the actual portlet class. The portlet initializes and
 * manages the lifecycle of the model, view and controller beans. During
 * rendering it dispatches to a JSP that can then make use of the beans via
 * request attributes. The JSP is located inside a subdirectory of the "WEB-INF"
 * directory. The subdirectory follows the package structure of the portlet
 * class. The name of the JSP is the name of the portlet mode, followed by
 * ".jsp".
 * 
 * The JSP is located inside the "WEB-INF" directory, because it is not supposed
 * to be accessed directly via a servlet entry point. Using the package name as
 * the directory structure makes it easier to maintain multiple portlets in one
 * project.
 * 
 * @author cleue
 */
public class TemplatePortlet extends GenericPortlet {

	/**
	 * Representation to dependencies on external services. Note that this
	 * interface inherits all dependencies from the components it aggregates.
	 * You should use a naming convention such that common dependencies use the
	 * same getters.
	 */
	public interface Dependencies extends TemplateModel.Dependencies,
			TemplateActions.Dependencies, TemplateController.Dependencies,
			TemplateView.Dependencies {

		/**
		 * TODO add dependencies via parameterless getter methods
		 */
	}

	/**
	 * Name of the request attribute for the controller
	 */
	private static final String KEY_CONTROLLER = "controller";

	/**
	 * Name of the request attribute for the model
	 */
	private static final String KEY_MODEL = "model";

	/**
	 * Name of the request attribute for the view
	 */
	private static final String KEY_VIEW = "view";

	/** class name for the logger */
	private static final String LOG_CLASS = TemplatePortlet.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * reference to external services
	 */
	private Dependencies dependencies;

	/**
	 * root path for JSPs. Per default we use the package name of the portlet
	 * inside the WEB-INF directory.
	 */
	private String jspRoot;

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
	private final TemplateActions createActions(final TemplateModel aModel,
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
		return new TemplateActions(aModel, aRequest, aResponse, dependencies);
	}

	/**
	 * Constructs a new controller
	 * 
	 * @param aModel
	 *            the model
	 * @param aRequest
	 *            the request
	 * @param aResponse
	 *            the response
	 * @return the controller
	 */
	private final TemplateController createController(
			final TemplateModel aModel, final PortletRequest aRequest,
			final MimeResponse aResponse) {
		// sanity check
		assert aModel != null;
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Constructs the controller. This method normally does not have to be
		 * changed. Rather change the implementation of the controller.
		 */
		return new TemplateController(aModel, aRequest, aResponse, dependencies);
	}

	/**
	 * Decodes the model used during rendering
	 * 
	 * @param aRequest
	 *            the portlet request
	 * @param aResponse
	 *            the portlet response
	 * @return the model
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	private final TemplateModel decodeModel(final PortletRequest aRequest,
			final PortletResponse aResponse) throws PortletException,
			IOException {
		// sanity check
		assert aRequest != null;
		assert aResponse != null;
		/**
		 * Decodes the model.This method normally does not have to be changed.
		 * Rather change the implementation of the model.
		 */
		return new TemplateModel(aRequest, aResponse, getPortletConfig(),
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
		// TODO reset all other instance fields to null
		// reset the dependencies
		dependencies = null;
		jspRoot = null;
		// default
		super.destroy();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doDispatch(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	protected void doDispatch(final RenderRequest request,
			final RenderResponse response) throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "doDispatch(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// initialize the content type
		response.setContentType(request.getResponseContentType());
		// render the JSP
		final String jspName = jspRoot + request.getPortletMode() + ".jsp";
		getPortletContext().getRequestDispatcher(jspName).include(request,
				response);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.portlet.base.portal.WebSpherePortalPortlet#init()
	 */
	@Override
	public void init() throws PortletException {
		// logging support
		final String LOG_METHOD = "init()";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// default
		super.init();
		// initialize the JSP root path
		jspRoot = "/WEB-INF/"
				+ TemplatePortlet.class.getPackage().getName()
						.replace('.', '/') + "/";
		// log this
		if (bIsLogging) {
			LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Root path [{0}].",
					jspRoot);
		}
		/**
		 * Note that our dependency interface extends the dependency interfaces
		 * of multiple other components. Thanks to a naming convention however
		 * the methods all collapse into one single method that we have to
		 * implement.
		 */
		dependencies = new Dependencies() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.ibm.portal.samples.portlettemplate.model.TemplateModel.
			 * Dependencies#getPrivateParameterMarshaller()
			 */
			@Override
			public Marshaller getPrivateParameterMarshaller() {
				// decide how to marshal private parameters
				return PrivateParameterMarshaller.SINGLETON;
			}

			// implement your getters here
		};
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
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
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// decode the model
		final TemplateModel model = decodeModel(request, response);
		// construct the action handler
		final TemplateActions actions = createActions(model, request, response);
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
			// handle the exception
		} finally {
			/**
			 * Encodes the model. This is an important step, without it the
			 * navigational state would be lost after the action.
			 */
			model.encode(response);
			// dispose
			actions.dispose();
			model.dispose();
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#render(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	public void render(final RenderRequest request,
			final RenderResponse response) throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "doDispatch(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// construct the view bean
		final TemplateView view = new TemplateView(getPortletConfig(), request,
				response, dependencies);
		// decode the model
		final TemplateModel model = decodeModel(request, response);
		// get the controller
		final TemplateController controller = createController(model, request,
				response);
		// set the beans, so we can access them in the JSP
		request.setAttribute(KEY_VIEW, view);
		request.setAttribute(KEY_MODEL, model);
		request.setAttribute(KEY_CONTROLLER, controller);
		try {
			// default handling
			super.render(request, response);
		} finally {
			// release model and controller
			request.removeAttribute(KEY_CONTROLLER);
			request.removeAttribute(KEY_MODEL);
			request.removeAttribute(KEY_VIEW);
			// dispose
			controller.dispose();
			model.dispose();
			view.dispose();
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
		/**
		 * Method not required at the moment. Make sure to override the default
		 * implementation, because it exposes a security issue.
		 */
	}
}
