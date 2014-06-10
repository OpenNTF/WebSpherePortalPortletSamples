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
package com.ibm.portal.samples.mvc.model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.StateAwareResponse;

import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.mvc.controller.TemplateController;

/**
 * Model implementation, includes all navigational state information. The key
 * features of this model are:
 * <ul>
 * <li>It provides bean access to the data model, so it can be used in a JSP
 * using JSTL.</li>
 * <li>The model state can be modified using bean syntax and the resulting
 * modification can be encoded into a URL or an action or event response.</li>
 * <li>It can be cloned efficiently, because for every URLs, we will clone the
 * model, update the clone so it represents the desired state represented by the
 * URL and then this state will be encoded into the URL.</li>
 * </ul>
 * 
 * @author cleue
 */
public class TemplateModel implements Cloneable {

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

	/**
	 * Private render parameters. The {@link TemplateModel} should provide
	 * getter and setter methods for each of the parameters. Optionally it can
	 * provide higher level modifying operations. The {@link TemplateController}
	 * contains methods to represent the modifiers by URLs.
	 * 
	 * @author cleue
	 * 
	 */
	private enum PARAMS {
		/**
		 * TODO For each render parameter define an enumeration value. This mock
		 * value is just a sample and can be removed. Also add getter and setter
		 * methods for custom render parameters.
		 * 
		 * @see TemplateModel#getSampleInt()
		 * @see TemplateModel#setSampleInt(int)
		 * @see TemplateModel#incSampleInt()
		 * @see TemplateModel#decSampleInt()
		 */

		SAMPLE_INT,

		/**
		 * Sample text that can be entered via a form input field.
		 * 
		 * @see TemplateModel#getSampleText()
		 * @see TemplateModel#setSampleText(String)
		 */
		SAMPLE_TEXT
	}

	/**
	 * default value for the sample integer
	 * 
	 * TODO replace by custom state
	 */
	private static final int DEFAULT_SAMPLE_INT = 1;

	/**
	 * default value for the sample text
	 * 
	 * TODO replace by custom state
	 */
	private static final String DEFAULT_SAMPLE_TEXT = "sample";

	/** class name for the logger */
	private static final String LOG_CLASS = TemplateModel.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging can be an instance variable, since the lifecycle of the model is
	 * the request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * Check if we have a sample int. We use a boolean object as a detector,
	 * <code>null</code> means that the int has not been decoded, yet.
	 */
	private Boolean bSampleInt;

	/**
	 * Check if we have a sample text. We use a boolean object as a detector,
	 * <code>null</code> means that the int has not been decoded, yet.
	 */
	private Boolean bSampleText;

	/**
	 * 
	 */
	private final PortletRequest request;

	/**
	 * sample navigational state
	 */
	private int sampleInt;

	/**
	 * sample navigational state
	 */
	private String sampleText;

	/**
	 * controls how private parameters are marshalled
	 */
	private final Marshaller privateMarshaller;

	/**
	 * Initializes the model from a portlet request
	 * 
	 * @param aRequest
	 *            the request
	 * @param aResponse
	 *            the response
	 * @param aDeps
	 *            the dependencies
	 */
	public TemplateModel(final PortletRequest aRequest,
			final PortletResponse aResponse, final PortletConfig aConfig,
			final Dependencies aDeps) {
		// sanity check
		assert aRequest != null;
		assert aResponse != null;
		assert aConfig != null;
		assert aDeps != null;
		// logging support
		final String LOG_METHOD = "TemplateModel(aRequest, aResponse, aConfig, aDeps)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// TODO copy dependencies from the interface into fields
		request = aRequest;
		privateMarshaller = aDeps.getPrivateParameterMarshaller();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Copy constructor, used to clone the model
	 * 
	 * @param aModel
	 *            the model
	 */
	protected TemplateModel(final TemplateModel aModel) {
		/**
		 * Copies the static portion of the data
		 */
		request = aModel.request;
		privateMarshaller = aModel.privateMarshaller;
		/**
		 * copies the resettable portion of the private data. Do not call the
		 * copy method because it might have been overridden by a subclass.
		 */
		internalCopy(aModel);
	}

	/**
	 * Reset all information
	 */
	public void clear() {
		// logging support
		final String LOG_METHOD = "clear()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		/**
		 * We dispatch to our internal implementation in case we need to clear
		 * our private data from somewhere else.
		 */
		internalClear();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TemplateModel clone() {
		/**
		 * generate a copy of the model. No need to change this implementation,
		 * rather update the copy constructor.
		 */
		return new TemplateModel(this);
	}

	/**
	 * Copies the state from the given model over to this model
	 * 
	 * @param aModel
	 *            model to copy
	 */
	public final void copy(final TemplateModel aModel) {
		// sanity check
		assert aModel != null;
		// logging support
		final String LOG_METHOD = "copy(aModel)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// copies the resettable portion of the private data
		internalCopy(aModel);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Decrement the sample integer
	 */
	public void decSampleInt() {
		// update the integer
		setSampleInt(getSampleInt() - 1);
	}

	/**
	 * Performs cleanup of the model resources at the end of the request
	 */
	public void dispose() {
		// logging support
		final String LOG_METHOD = "dispose()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// reset the data
		internalClear();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the state of the model into a URL. Note that typically the
	 * identical logic has to be implemented in the
	 * {@link TemplateModel#encode(StateAwareResponse)} method.
	 * 
	 * @param aURL
	 *            the URL
	 * 
	 * @throws PortletException
	 * @throws IOException
	 * 
	 * @see TemplateModel#encode(StateAwareResponse)
	 */
	public void encode(final PortletURL aURL) throws PortletException,
			IOException {
		// sanity check
		assert aURL != null;
		// logging support
		final String LOG_METHOD = "encode(aURL)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// encode the text
		aURL.setParameter(privateMarshaller.marshalEnum(PARAMS.SAMPLE_TEXT),
				privateMarshaller.marshalString(getSampleText()));
		aURL.setParameter(privateMarshaller.marshalEnum(PARAMS.SAMPLE_INT),
				privateMarshaller.marshalInt(getSampleInt()));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the state of the model into a response, typically after the
	 * action. Note that typically the identical logic has to be implemented in
	 * the {@link #encode(PortletURL))} method.
	 * 
	 * @param aResponse
	 *            the response
	 * @throws PortletException
	 * @throws IOException
	 * 
	 * @see TemplateModel#encode(PortletURL)
	 */
	public void encode(final StateAwareResponse aResponse)
			throws PortletException, IOException {
		// sanity check
		assert aResponse != null;
		// logging support
		final String LOG_METHOD = "encode(aResponse)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// encode the text
		aResponse.setRenderParameter(
				privateMarshaller.marshalEnum(PARAMS.SAMPLE_TEXT),
				privateMarshaller.marshalString(getSampleText()));
		aResponse.setRenderParameter(
				privateMarshaller.marshalEnum(PARAMS.SAMPLE_INT),
				privateMarshaller.marshalInt(getSampleInt()));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Getter for the sample integer
	 * 
	 * @return the sample integer
	 */
	public int getSampleInt() {
		// logging support
		final String LOG_METHOD = "getSampleInt()";
		// check if we have already decoded the parameter
		if (bSampleInt == null) {
			// decodes the int
			setSampleInt(privateMarshaller.unmarshalInt(request
					.getParameter(privateMarshaller
							.marshalEnum(PARAMS.SAMPLE_INT)),
					DEFAULT_SAMPLE_INT));
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Decoded the sample int ...");
			}
		}
		// ok
		return sampleInt;
	}

	/**
	 * Getter for the sample text
	 * 
	 * @return the sample text
	 */
	public String getSampleText() {
		// logging support
		final String LOG_METHOD = "getSampleText()";
		// check if we have already decoded the parameter
		if (bSampleText == null) {
			// decodes the text
			setSampleText(privateMarshaller.unmarshalString(request
					.getParameter(privateMarshaller
							.marshalEnum(PARAMS.SAMPLE_TEXT)),
					DEFAULT_SAMPLE_TEXT));
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Decoded the sample text ...");
			}
		}
		// ok
		return sampleText;
	}

	/**
	 * Increment the sample integer
	 */
	public void incSampleInt() {
		// update the integer
		setSampleInt(getSampleInt() + 1);
	}

	/**
	 * Clears only our private data
	 */
	private final void internalClear() {
		// TODO reset your custom data here
		setSampleInt(DEFAULT_SAMPLE_INT);
		setSampleText(DEFAULT_SAMPLE_TEXT);
	}

	/**
	 * Copies only our private data
	 * 
	 * @param aModel
	 *            model to copy
	 */
	private final void internalCopy(final TemplateModel aModel) {
		// sanity check
		assert aModel != null;
		// TODO copy the data here
		bSampleInt = aModel.bSampleInt;
		sampleInt = aModel.sampleInt;
		bSampleText = aModel.bSampleText;
		sampleText = aModel.sampleText;
	}

	/**
	 * Assigns a new sample integer
	 * 
	 * @param aSampleInt
	 *            the new sample
	 */
	public void setSampleInt(final int aSampleInt) {
		// logging support
		final String LOG_METHOD = "setSampleInt(aSampleInt)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aSampleInt);
		}
		// set the int
		sampleInt = aSampleInt;
		// update the flag
		bSampleInt = (sampleInt != 0);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Assigns a new sample text
	 * 
	 * @param aSampleText
	 *            new text
	 */
	public void setSampleText(final String aSampleText) {
		// logging support
		final String LOG_METHOD = "setSampleText(aSampleText)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aSampleText);
		}
		// set the text
		sampleText = aSampleText;
		// update the flag
		bSampleText = (sampleText != null);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}
}
