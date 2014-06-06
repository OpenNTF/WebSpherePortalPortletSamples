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
package com.ibm.portal.samples.mvc.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.ibm.portal.samples.common.AbstractView;

/**
 * Implementation of the view bean. The bean has accessors that can be used by
 * the JSP to access contextual information. Note in particular the getter
 * methods that provide access to the resource bundle. The actual resource
 * bundle keys are considered an implementation detail that should not be used
 * directly by the JSP. Instead the getters offer a stable APIs to the logically
 * available texts, that can remain stable even across changes of the resource
 * bundle keys or structure.
 * 
 * @author cleue
 */
public class TemplateView extends AbstractView {

	/**
	 * Dependencies of the bean
	 * 
	 */
	public interface Dependencies extends AbstractView.Dependencies {
		/**
		 * TODO add dependencies via parameterless getter methods
		 */
	}

	/** class name for the logger */
	private static final String LOG_CLASS = TemplateView.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * Initialize the view bean
	 * 
	 * @param aConfig
	 *            configuration of the portlet
	 * @param aRequest
	 *            the request
	 * @param aResponse
	 *            the response
	 * @param aDeps
	 *            the dependencies
	 */
	public TemplateView(final PortletConfig aConfig,
			final RenderRequest aRequest, final RenderResponse aResponse,
			final Dependencies aDeps) {
		// default
		super(aConfig, aRequest, aResponse, aDeps);
		// sanity check
		assert aConfig != null;
		assert aRequest != null;
		assert aResponse != null;
		assert aDeps != null;
	}

	/**
	 * Performs cleanup of the view resources at the end of the request
	 */
	public void dispose() {
		// logging support
		final String LOG_METHOD = "dispose()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// TODO add cleanup here

		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Resource bundle access for the cancel button.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getCancelButtonTitle() {
		return getMessage("form.button.cancel");
	}

	/**
	 * Resource bundle access for the tooltip on the decrement link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getDecSampleIntHint() {
		return getMessage("link.decrement.hint");
	}

	/**
	 * Resource bundle access for the text of the decrement link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getDecSampleIntTitle() {
		return getMessage("link.decrement.title");
	}

	/**
	 * Resource bundle access for the form title.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getFormTitle() {
		return getMessage("form.title");
	}

	/**
	 * Resource bundle access for the tooltip on the increment link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getIncSampleIntHint() {
		return getMessage("link.increment.hint");
	}

	/**
	 * Resource bundle access for the text of the increment link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getIncSampleIntTitle() {
		return getMessage("link.increment.title");
	}

	/**
	 * Resource bundle access for the tooltip of the info icon.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getInfoIconHint() {
		return getMessage("icon.information.hint");
	}

	/**
	 * Resource bundle access for the title of the info icon.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getInfoIconTitle() {
		return getMessage("icon.information.title");
	}

	/**
	 * Resource bundle access for tooltip for a required input field.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getRequiredFieldHint() {
		return getMessage("form.requiredField");
	}

	/**
	 * Resource bundle access for the tooltip on the reset link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getResetHint() {
		return getMessage("link.reset.hint");
	}

	/**
	 * Resource bundle access for the text of the reset link.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getResetTitle() {
		return getMessage("link.reset.title");
	}

	/**
	 * Resource bundle access for the label of the sample text input field.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getSampleTextTitle() {
		return getMessage("form.sampleText.title");
	}

	/**
	 * Resource bundle access for the save button.
	 * 
	 * TODO replace by custom getters
	 * 
	 * @return the resource bundle entry
	 */
	public String getSaveButtonTitle() {
		return getMessage("form.button.save");
	}
}
