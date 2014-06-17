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
package com.ibm.portal.samples.common;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class AbstractPortlet extends GenericPortlet {

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
	private static final String LOG_CLASS = AbstractPortlet.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * suffix for the JSP path
	 */
	private static final String PATH_SUFFIX = ".jsp";

	/**
	 * Resource prefix for private resource
	 */
	private String privatePathPrefix;

	/**
	 * Resource prefix for public resource
	 */
	private String publicPathPrefix;

	protected AbstractController createController(PortletRequest request, MimeResponse response) {
		return null;
	}

	protected AbstractController createController(RenderRequest request, RenderResponse response) {
		return createController((PortletRequest) request, (MimeResponse) response);
	}

	protected AbstractModel createModel(PortletRequest request, MimeResponse response) {
		return null;
	}

	protected AbstractModel createModel(RenderRequest request, RenderResponse response) {
		return createModel((PortletRequest) request, (MimeResponse) response);
	}

	protected AbstractView createView(PortletRequest request, MimeResponse response) {
		return null;
	}

	protected AbstractView createView(RenderRequest request, RenderResponse response) {
		return createView((PortletRequest) request, (MimeResponse) response);
	}

	protected void dispose(final Object aObject) {
	}

	protected void disposeRequest(PortletRequest request, MimeResponse response) {
		// remove the beans from the request
		removeBean(KEY_MODEL, request);
		removeBean(KEY_VIEW, request);
		removeBean(KEY_CONTROLLER, request);
	}

	protected void disposeRequest(RenderRequest request, RenderResponse response) {
		disposeRequest((PortletRequest) request, (MimeResponse) response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	protected void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// dispatch
		doInclude(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doHelp(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	protected void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// dispatch
		doInclude(request, response);
	}

	protected void doInclude(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "doInclude(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// init the response
		response.setContentType(request.getResponseContentType());
		// include the JSP
		getPortletContext().getRequestDispatcher(
				getIncludePath(request, response)).include(request, response);
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
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// dispatch
		doInclude(request, response);
	}

	protected String getIncludePath(RenderRequest request,
			RenderResponse response) {
		return getPrivatePathPrefix() + request.getPortletMode().toString()
				+ getPathSuffix();
	}
	
	protected AbstractModel getModel(PortletRequest request) {
		return (AbstractModel) request.getAttribute(KEY_MODEL);
	}

	protected AbstractModel getModel(RenderRequest request) {
		return getModel((PortletRequest) request);
	}
	
	protected String getPathSuffix() {
		return PATH_SUFFIX;
	}

	protected String getPrivatePathPrefix() {
		return privatePathPrefix;
	}

	protected String getPublicPathPrefix() {
		return publicPathPrefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#init(javax.portlet.PortletConfig)
	 */
	@Override
	public void init(PortletConfig config) throws PortletException {
		// logging support
		final String LOG_METHOD = "init(config)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { config });
		}
		// default initialization
		super.init(config);
		// mail class
		final Class<?> clz = getClass();
		final String prefix = clz.getPackage().getName().replace('.', '/')
				+ "/";
		// init the prefixes
		publicPathPrefix = "/" + prefix;
		privatePathPrefix = "/WEB-INF/" + prefix;
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	protected void initRequest(PortletRequest request, MimeResponse response) {
	}

	protected void initRequest(RenderRequest request, RenderResponse response) {
		// fallback
		initRequest((PortletRequest) request, (MimeResponse) response);
		// init the beans
		final AbstractModel model = createModel(request, response);
		final AbstractView view = createView(request, response);
		final AbstractController controller = createController(request, response);
		// add the beans to the request
		setBean(KEY_MODEL, model, request);
		setBean(KEY_VIEW, view, request);
		setBean(KEY_CONTROLLER, controller, request);		
	}

	protected void removeBean(final String aKey, final PortletRequest aRequest) {
		dispose(aRequest.getAttribute(aKey));
		aRequest.removeAttribute(aKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#render(javax.portlet.RenderRequest,
	 * javax.portlet.RenderResponse)
	 */
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// logging support
		final String LOG_METHOD = "render(request, response)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { request,
					response });
		}
		// initialize the beans
		initRequest(request, response);
		try {
			// default
			super.render(request, response);
		} finally {
			// done with the initialization
			disposeRequest(request, response);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	protected void setBean(final String aKey, final Object aBean,
			final PortletRequest aRequest) {
		aRequest.setAttribute(aKey, aBean);
	}
}
