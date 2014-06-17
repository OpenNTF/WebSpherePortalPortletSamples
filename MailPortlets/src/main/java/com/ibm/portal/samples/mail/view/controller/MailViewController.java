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
package com.ibm.portal.samples.mail.view.controller;

import static javax.portlet.ResourceURL.PAGE;
import static javax.portlet.ResourceURL.PORTLET;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.common.AbstractController;
import com.ibm.portal.samples.mail.view.model.MailViewActions.ACTION;
import com.ibm.portal.samples.mail.view.model.MailViewActions.KEY;
import com.ibm.portal.samples.mail.view.model.MailViewModel;

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
public class MailViewController extends AbstractController implements
		Disposable {

	public interface Dependencies extends AbstractController.Dependencies {

	}

	/** class name for the logger */
	private static final String LOG_CLASS = MailViewController.class.getName();

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
	private final MailViewModel model;

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
	public MailViewController(final MailViewModel aModel,
			final MimeResponse aResponse, final Dependencies aDeps) {
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
	 * Returns a resource URL that serves an image from the currently selected
	 * message. This served image does not contain further links, so we only
	 * need to know about the identity of the selected message. The cacheability
	 * can be PORTLET.
	 * 
	 * @param aResourceID
	 *            the resource ID
	 * @return the URL to that resource
	 */
	public final ResourceURL getMessageResourceURL(final String aResourceID) {
		// logging support
		final String LOG_METHOD = "getMessageResourceURL(aResourceID)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aResourceID });
		}
		// construct the resource
		final ResourceURL url = response.createResourceURL();
		url.setCacheability(PORTLET);
		// important, work on a copy of the current model
		final MailViewModel copy = model.clone();
		copy.setMessageResource(aResourceID);
		copy.encode(url);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
		}
		// return the result
		return url;
	}

	/**
	 * Constructs a URL that only renders the message content via a server
	 * resource request. Note, that the message rendering might contain portlet
	 * URLs, e.g. the reply URL. So the full navigational state must be
	 * available and the cacheability is of type PAGE.
	 * 
	 * @return the resource URL to the message content
	 */
	public final ResourceURL getRenderMessageURL() {
		// logging support
		final String LOG_METHOD = "getRenderMessageURL()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// construct the resource
		final ResourceURL url = response.createResourceURL();
		url.setCacheability(PAGE);
		// important, work on a copy of the current model
		final MailViewModel copy = model.clone();
		copy.setRenderMessage(true);
		copy.encode(url);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
		}
		// return the result
		return url;
	}

	/**
	 * Returns the value of the form field that encodes the enter credentials
	 * action
	 * 
	 * @return form field value
	 */
	public String getValueActionReply() {
		return getEncoded(ACTION.REPLY);
	}
}
