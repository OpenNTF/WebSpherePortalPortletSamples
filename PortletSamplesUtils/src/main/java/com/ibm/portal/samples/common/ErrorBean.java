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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

/**
 * Information about an exception in the session
 * 
 * @author cleue
 */
public class ErrorBean implements Serializable {

	/**
	 * key into the session for this bean
	 */
	public static final String KEY_BEAN = ErrorBean.class.getName();

	/** class name for the logger */
	private static final String LOG_CLASS = ErrorBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * the serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Clears the error message
	 * 
	 * @param aRequest
	 *            the request
	 */
	public static final void clear(final PortletRequest aRequest) {
		final PortletSession session = aRequest.getPortletSession(false);
		if (session != null) {
			session.removeAttribute(KEY_BEAN);
		}
	}

	/**
	 * Returns the error from the session if it exists
	 * 
	 * @param aRequest
	 *            the request
	 * @return the error
	 */
	public static final ErrorBean getErrorBean(final PortletRequest aRequest) {
		final PortletSession session = aRequest.getPortletSession(false);
		return (session != null) ? (ErrorBean) session.getAttribute(KEY_BEAN)
				: null;
	}

	/**
	 * Sets this error message
	 * 
	 * @param th
	 *            the throwable
	 * @param aRequest
	 */
	public static final void setThrowable(final Throwable th,
			final PortletRequest aRequest) {
		// logging support
		final String LOG_METHOD = "setThrowable(th, aRequest)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, th);
		}
		// store the exception in the session
		final PortletSession session = aRequest.getPortletSession(true);
		if (session != null) {
			session.setAttribute(KEY_BEAN, new ErrorBean(th));
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * the exception message
	 */
	private final Throwable exception;

	/**
	 * Initialize the error message
	 * 
	 * @param aError
	 *            the message
	 */
	public ErrorBean(final Throwable aError) {
		exception = aError;
	}

	/**
	 * Returns the error message
	 * 
	 * @return the error message
	 */
	public final String getErrorMessage() {
		// returns the message
		return getException().getLocalizedMessage();
	}

	/**
	 * Returns the exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
}
