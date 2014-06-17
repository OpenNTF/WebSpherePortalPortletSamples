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

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;

/**
 * Some base class for all of our models
 * 
 * @author cleue
 */
public class AbstractController {

	/**
	 * Representation to dependencies on external services
	 */
	public interface Dependencies {
		/**
		 * Marshaller for private render parameters
		 * 
		 * @return the marshaller
		 */
		Marshaller getPrivateParameterMarshaller();

		/**
		 * TODO add dependencies via parameterless getter methods
		 */
	}

	/** class name for the logger */
	private static final String LOG_CLASS = AbstractController.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * the model
	 */
	private final AbstractModel model;

	/**
	 * controls how private parameters are marshalled
	 */
	private final Marshaller privateMarshaller;

	/**
	 * the response
	 */
	private final MimeResponse response;

	/**
	 * Initialize the controller
	 * 
	 * @param aModel
	 *            the base model
	 * @param aResponse
	 *            the portlet response
	 * @param aDeps
	 *            the dependencies
	 */
	protected AbstractController(final AbstractModel aModel,
			final MimeResponse aResponse, final Dependencies aDeps) {
		model = aModel;
		response = aResponse;
		privateMarshaller = aDeps.getPrivateParameterMarshaller();
	}

	protected final PortletURL createRenderURL(final AbstractModel aModel)
			throws PortletException, IOException {
		// sanity check
		assert aModel != null;
		// construct a new render URL
		final PortletURL url = response.createRenderURL();
		aModel.encode(url);
		// ok
		return url;
	}

	protected final ResourceURL createResourceURL(final AbstractModel aModel)
			throws PortletException, IOException {
		// sanity check
		assert aModel != null;
		// construct a new resource URL
		final ResourceURL url = response.createResourceURL();
		aModel.encode(url);
		// ok
		return url;
	}

	/**
	 * Returns a generic action URL. It is not necessary to set an action ID,
	 * since we rather transport the action ID via the multipart input stream.
	 * 
	 * @return the action URL
	 * @throws IOException
	 * @throws PortletException
	 */
	public final PortletURL getActionURL() throws IOException, PortletException {
		// logging support
		final String LOG_METHOD = "getActionURL()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// construct the URL
		final PortletURL url = response.createActionURL();
		model.encode(url);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
		}
		// return the result
		return url;
	}

	protected String getEncoded(final Enum<?> aKey) {
		return privateMarshaller.marshalEnum(aKey);
	}
}
