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
package com.ibm.portal.samples.mail.compose.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.StateAwareResponse;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.common.AbstractModel;

/**
 * Implementation of the state handling aspects of the composer portlet. We
 * maintain address and subject for the reply usecase.
 * 
 * @author cleue
 * 
 */
public class MailComposeModel extends AbstractModel implements Disposable,
		Cloneable {

	public interface Dependencies extends AbstractModel.Dependencies {

	}

	/**
	 * enumeration of the states we need to encode our view
	 */
	private enum STATE {
		/** the address */
		ADDRESS,
		/** the body */
		BODY,
		/** subject */
		SUBJECT
	}

	/**
	 * default address
	 */
	private static final String DEFAULT_ADDRESS = null;

	/**
	 * default body
	 */
	private static final String DEFAULT_BODY = null;

	/**
	 * default subject
	 */
	private static final String DEFAULT_SUBJECT = null;

	/** class name for the logger */
	private static final String LOG_CLASS = MailComposeModel.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * current address
	 */
	private String address;

	/**
	 * access to the services
	 */
	private final MailComposeBean bean;

	/**
	 * logging support, we can do this as a instance variable since the model
	 * bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * current body
	 */
	private String body;

	/**
	 * current subject
	 */
	private String subject;

	/**
	 * Decode the model from the request
	 * 
	 * @param aBean
	 *            access to services
	 * @param aReq
	 *            the request
	 */
	public MailComposeModel(final MailComposeBean aBean,
			final PortletRequest aReq, final Dependencies aDeps) {
		// default
		super(aReq, aDeps);
		// logging support
		final String LOG_METHOD = "MailComposeModel(aBean, aReq)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aBean, aReq });
		}
		// decode
		bean = aBean;
		// decode
		subject = decode(STATE.SUBJECT, DEFAULT_SUBJECT);
		address = decode(STATE.ADDRESS, DEFAULT_ADDRESS);
		body = decode(STATE.BODY, DEFAULT_BODY);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, this);
		}
	}

	/**
	 * Initializes the model as a copy of another model
	 * 
	 * @param aCopy
	 *            the model to copy
	 */
	protected MailComposeModel(final MailComposeModel aCopy) {
		super(aCopy);
		bean = aCopy.bean;
		subject = aCopy.subject;
		address = aCopy.address;
		body = aCopy.body;
	}

	/**
	 * Clears the message content
	 */
	public void clearMessage() {
		// reset the message
		subject = null;
		address = null;
		body = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MailComposeModel clone() {
		return new MailComposeModel(this);
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
	 * Encodes the stat of the model into the response. It is unfortunate that
	 * we need to write this method reduntantly to {@link #encode(PortletURL)}
	 * but there exists no common interface between {@link PortletURL} and
	 * {@link StateAwareResponse} that would allow to write parameters using
	 * common code.
	 * 
	 * @param resp
	 *            response
	 */
	public void encode(final PortletURL url) {
		// logging support
		final String LOG_METHOD = "encode(url)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
		}
		// encode the nav state into the action response
		encode(url, STATE.SUBJECT, subject, DEFAULT_SUBJECT);
		encode(url, STATE.ADDRESS, address, DEFAULT_ADDRESS);
		encode(url, STATE.BODY, body, DEFAULT_BODY);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the stat of the model into the response. It is unfortunate that
	 * we need to write this method reduntantly to {@link #encode(PortletURL)}
	 * but there exists no common interface between {@link PortletURL} and
	 * {@link StateAwareResponse} that would allow to write parameters using
	 * common code.
	 * 
	 * @param resp
	 *            response
	 */
	public void encode(final StateAwareResponse resp) {
		// logging support
		final String LOG_METHOD = "encode(resp)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
		}
		// encode the nav state into the action response
		encode(resp, STATE.SUBJECT, subject, DEFAULT_SUBJECT);
		encode(resp, STATE.ADDRESS, address, DEFAULT_ADDRESS);
		encode(resp, STATE.BODY, body, DEFAULT_BODY);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * The current address
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Current message body
	 * 
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Current message subject
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Assigns a new address
	 * 
	 * @param aAddress
	 *            the address
	 */
	public void setAddress(final String aAddress) {
		address = aAddress;
	}

	/**
	 * Assigns a new body
	 * 
	 * @param aBody
	 *            the body
	 */
	public void setBody(final String aBody) {
		body = aBody;
	}

	/**
	 * Assigns a new subject
	 * 
	 * @param aSubject
	 *            the subject
	 */
	public void setSubject(final String aSubject) {
		subject = aSubject;
	}
}
