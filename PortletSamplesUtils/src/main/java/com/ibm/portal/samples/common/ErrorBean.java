package com.ibm.portal.samples.common;

import java.io.Serializable;

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
		final PortletSession session = aRequest.getPortletSession(true);
		if (session != null) {
			session.setAttribute(KEY_BEAN, new ErrorBean(th));
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
