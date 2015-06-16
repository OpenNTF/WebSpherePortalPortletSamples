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
package com.ibm.portal.samples.mail.compose.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import com.ibm.portal.samples.mail.common.AbstractView;
import com.ibm.portal.samples.mail.compose.model.MailComposeModel;

/**
 * Bean that represents formatting related aspects. We prefer to use a bean over
 * the JSTL fmt tags, because the tags do not directly support escaping and it
 * is tedious and error prone to copy every string into a temporary variable in
 * the JSP just to escape it later. The JSP becomes more readable using the bean
 * approach.
 * 
 * @author cleue
 */
public class MailComposeView extends AbstractView {

	/** class name for the logger */
	private static final String LOG_CLASS = MailComposeView.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support, we can do this as a instance variable since the model
	 * bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * Initialize this view bean
	 * 
	 * @param aConfig
	 *            portlet config, used to access the resource bundle
	 * @param aResponse
	 *            the response, used to access the desired locale
	 * @param aModel
	 *            model
	 */
	public MailComposeView(final PortletConfig aConfig,
			final PortletRequest aRequest, final MimeResponse aResponse,
			final MailComposeModel aModel) {
		// default init
		super(aConfig, aRequest, aResponse);
		// logging support
		final String LOG_METHOD = "MailComposeView(aConfig, aRequest, aResponse, aModel)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aConfig,
					aRequest, aResponse, aModel });
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Send...
	 */
	private String sendMail;

	/**
	 * @return "Send..."
	 */
	public String getSendMail() {
		if (sendMail == null) {
			sendMail = getMessage("compose.sendMail");
		}
		return sendMail;
	}
}
