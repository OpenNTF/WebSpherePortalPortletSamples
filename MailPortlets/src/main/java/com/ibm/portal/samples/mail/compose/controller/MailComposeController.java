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
package com.ibm.portal.samples.mail.compose.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.common.AbstractController;
import com.ibm.portal.samples.mail.compose.model.MailComposeModel;
import com.ibm.portal.samples.mail.compose.model.MailComposeActions.ACTION;
import com.ibm.portal.samples.mail.compose.model.MailComposeActions.KEY;

/**
 * Provides access to the aspects of the application that change the application
 * state, i.e. URLs, forms, form fields, etc
 * 
 * All control URLs used by the portlet (via the JSP) should be generated via
 * this controller bean, since the business logic behind the URLs is then
 * captured in the controller layer and moved out of the view layer (the JSP).
 * 
 * The pattern for generating a URL is
 * <ul>
 * <li>create the URL object via the portlet response</li>
 * <li>create a copy of the model. This copy represents the new model state for
 * the URL</li>
 * <li>modify the model such that it represents the desired result state of the
 * URL</li>
 * <li>encode the model into the URL</li>
 * </ul>
 * 
 * The controller bean returns the API objects for the URLs. These objects will
 * be converted to a string by the JSP. Note, that the URLs represent "raw"
 * URLs, they are not escaped, so this escaping needs to be applied by the JSP
 * explicitly.
 * 
 * @author cleue
 */
public class MailComposeController extends AbstractController implements
		Disposable {

	public interface Dependencies extends AbstractController.Dependencies {

	}

	/** class name for the logger */
	private static final String LOG_CLASS = MailComposeController.class
			.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support, we can do this as a instance variable since the
	 * controller bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * base model that we operate on
	 */
	private final MailComposeModel model;

	/**
	 * the portlet response, used to construct the URLs
	 */
	private final MimeResponse response;

	/**
	 * Initialize teh controller
	 * 
	 * @param aModel
	 *            the base model
	 * @param aResponse
	 *            the portlet response
	 */
	public MailComposeController(final MailComposeModel aModel,
			final MimeResponse aResponse, Dependencies aDeps) {
		super(aModel, aResponse, aDeps);
		model = aModel;
		response = aResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// logging support
		final String LOG_METHOD = "dispose()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// noop for now
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns the name of the form field that encodes the action
	 * 
	 * @return form field name
	 */
	public String getKeyAction() {
		return getEncoded(KEY.ACTION);
	}

	/**
	 * Returns the name of the form field that encodes the body
	 * 
	 * @return form field name
	 */
	public String getKeyBody() {
		return getEncoded(KEY.BODY);
	}

	/**
	 * Returns the name of the form field that encodes the receiver
	 * 
	 * @return form field name
	 */
	public String getKeyReceiver() {
		return getEncoded(KEY.RECEIVER);
	}

	/**
	 * Returns the name of the form field that encodes the subject
	 * 
	 * @return form field name
	 */
	public String getKeySubject() {
		return getEncoded(KEY.SUBJECT);
	}

	/**
	 * Returns the value of the form field that encodes the send mail action
	 * 
	 * @return form field value
	 */
	public String getValueActionSendMail() {
		return getEncoded(ACTION.SEND_MAIL);
	}
}
