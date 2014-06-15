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
package com.ibm.portal.samples.mvc.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import com.ibm.portal.samples.common.Marshaller;
import com.ibm.portal.samples.mvc.model.TemplateActions.ACTION;
import com.ibm.portal.samples.mvc.model.TemplateActions.KEY;
import com.ibm.portal.samples.mvc.model.TemplateModel;

/**
 * Implementation of the controller that generates URLs to modify the model.
 * Each URL should work on a clone of the {@link TemplateModel} and modify the
 * cloned model.
 * 
 * @author cleue
 */
public class TemplateController {

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
	private static final String LOG_CLASS = TemplateController.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging can be an instance variable, since the lifecycle of the
	 * controller is the request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * base model
	 */
	private final TemplateModel model;

	private final MimeResponse response;

	/**
	 * controls how private parameters are marshalled
	 */
	private final Marshaller privateMarshaller;

	/**
	 * Initializes the controller on top of a model
	 * 
	 * @param aModel
	 *            the model
	 * @param aRequest
	 *            the request
	 * @param aResponse
	 *            the response
	 * @param aDeps
	 *            the dependencies
	 */
	public TemplateController(final TemplateModel aModel,
			final PortletRequest aRequest, final MimeResponse aResponse,
			final Dependencies aDeps) {
		// sanity check
		assert aModel != null;
		assert aRequest != null;
		assert aResponse != null;
		assert aDeps != null;
		// logging support
		final String LOG_METHOD = "TemplateController(aModel, aBean, aDeps)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// TODO copy dependencies from the interface into fields
		response = aResponse;
		model = aModel;
		privateMarshaller = aDeps.getPrivateParameterMarshaller();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns a clone of the current model
	 * 
	 * @return the clone
	 */
	private final TemplateModel cloneModel() {
		// clones the current model
		return model.clone();
	}

	/**
	 * Constructs a render URL that encodes the given model
	 * 
	 * @param aModel
	 *            the model
	 * @return the render URL
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	private final PortletURL createRenderURL(final TemplateModel aModel)
			throws PortletException, IOException {
		// sanity check
		assert aModel != null;
		// construct a new render URL
		final PortletURL url = response.createRenderURL();
		aModel.encode(url);
		// ok
		return url;
	}

	/**
	 * Performs a cleanup of resources held by the controller
	 */
	public void dispose() {
		// TODO cleanup here
	}

	/**
	 * Returns the action URL that encodes the current model. We use the same
	 * action URL for all actions, since the distinction of the individual
	 * action is encoded as a hidden form data value.
	 * 
	 * @return the action URL.
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public PortletURL getActionURL() throws PortletException, IOException {
		// construct a new render URL
		final PortletURL url = response.createActionURL();
		model.encode(url);
		// ok
		return url;
	}

	/**
	 * Creates a render URL that clears the model
	 * 
	 * @return the render URL
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public PortletURL getClearURL() throws PortletException, IOException {
		// clone the model
		final TemplateModel clone = cloneModel();
		// modify the cloned model
		clone.clear();
		// represent the cloned model via a URL
		return createRenderURL(clone);
	}

	/**
	 * TODO remove and replace by some more meaningful methods
	 * 
	 * Creates a render URL that decrements the sample integer
	 * 
	 * @return the render URL
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public PortletURL getDecSampleIntURL() throws PortletException, IOException {
		// clone the model
		final TemplateModel clone = cloneModel();
		// modify the cloned model
		clone.decSampleInt();
		// represent the cloned model via a URL
		return createRenderURL(clone);
	}

	/**
	 * TODO remove and replace by some more meaningful methods
	 * 
	 * Creates a render URL that increments the sample integer
	 * 
	 * @return the render URL
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public PortletURL getIncSampleIntURL() throws PortletException, IOException {
		// clone the model
		final TemplateModel clone = cloneModel();
		// modify the cloned model
		clone.incSampleInt();
		// represent the cloned model via a URL
		return createRenderURL(clone);
	}

	/**
	 * Returns the value of the form field that encodes the cancel action
	 * 
	 * @return form field value
	 */
	public String getValueActionCancel() {
		return privateMarshaller.marshalEnum(ACTION.SAMPLE_FORM_CANCEL);
	}

	/**
	 * Returns the value of the form field that encodes the save action
	 * 
	 * @return form field value
	 */
	public String getValueActionSave() {
		return privateMarshaller.marshalEnum(ACTION.SAMPLE_FORM_SAVE);
	}

	/**
	 * Returns the name of the form field that encodes the action
	 * 
	 * @return form field name
	 */
	public String getKeyAction() {
		return privateMarshaller.marshalEnum(KEY.ACTION);
	}

	/**
	 * Returns the name of the form field that encodes the sample text
	 * 
	 * @return form field name
	 */
	public String getKeySampleText() {
		return privateMarshaller.marshalEnum(KEY.SAMPLE_TEXT);
	}
}
