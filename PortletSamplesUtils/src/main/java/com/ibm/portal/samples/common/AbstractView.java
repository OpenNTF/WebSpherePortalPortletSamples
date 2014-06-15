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
 */package com.ibm.portal.samples.common;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

/**
 * Base class for view beans. This class encapsulates functionality that is
 * basically required by any portlet.
 * 
 * @author cleue
 */
public abstract class AbstractView {

	/**
	 * Dependencies of the bean
	 * 
	 */
	public interface Dependencies {
		/**
		 * TODO add dependencies via parameterless getter methods
		 */
	}

	/** class name for the logger */
	private static final String LOG_CLASS = AbstractView.class.getName();

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
	 * URL to the blank image
	 */
	private String blankURL;

	/**
	 * default resource bundle for the portlet
	 */
	private final ResourceBundle bundle;

	/**
	 * selected locale
	 */
	private final Locale locale;

	/**
	 * namespace identifier
	 */
	private String namespace;

	/**
	 * the request
	 */
	private final PortletRequest request;

	/**
	 * the response
	 */
	private final MimeResponse response;

	/**
	 * Initialize this view bean
	 * 
	 * @param aConfig
	 *            portlet config, used to access the resource bundle
	 * @param aRequest
	 *            the request
	 * @param aResponse
	 *            the response, used to access the desired locale
	 * @param aDeps
	 *            the dependencies
	 */
	protected AbstractView(final PortletConfig aConfig,
			final PortletRequest aRequest, final MimeResponse aResponse,
			final Dependencies aDeps) {
		// sanity check
		assert aConfig != null;
		assert aRequest != null;
		assert aResponse != null;
		assert aDeps != null;
		// logging support
		final String LOG_METHOD = "AbstractView(aConfig, aRequest, aResponse)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// init
		request = aRequest;
		response = aResponse;
		locale = aResponse.getLocale();
		bundle = aConfig.getResourceBundle(locale);
		// log this
		if (bIsLogging) {
			LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
					"Resource bundle locale is [{0}].", bundle.getLocale());
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns the URL to the blank image. This is typically required to realize
	 * image sprites, when the sprite is provided by CSS classes, but the img
	 * element still has to point to a valid image.
	 * 
	 * @return blank image URL
	 */
	public String getBlankImageURL() {
		// logging support
		final String LOG_METHOD = "getBlankImageURL()";
		// lazily compute the URL
		if (blankURL == null) {
			// create the URL
			blankURL = response.encodeURL(request.getContextPath()
					+ "/images/blank.png");
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"URL to the blank image [{0}].", blankURL);
			}
		}
		// returns the URL
		return blankURL;
	}

	/**
	 * Returns the character encoding of the response. This is typically
	 * required to provision the "_charset_" field of action forms with an
	 * initial value.
	 * 
	 * @return the encoding
	 */
	public final String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}

	/**
	 * Returns the rendering locale for the portlet. Note that this is the
	 * locale of the portlet response, not the locale of the request. The
	 * response locale is computed individually for each portlets, based on the
	 * locales the portlet has declared, the locale that the aggregator has
	 * chosen for the page and the locales requested by the client.
	 * 
	 * @return the locale
	 */
	public final Locale getLocale() {
		return locale;
	}

	/**
	 * Returns the message from the bundle
	 * 
	 * @param aKey
	 *            the bundle key
	 * @return the formatted string
	 */
	protected final String getMessage(final String aKey) {
		// logging support
		final String LOG_METHOD = "getMessage(aKey)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aKey });
		}
		// bundle results
		final String result = bundle.getObject(aKey).toString();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Returns the portlet namespace
	 * 
	 * @return the namespace
	 */
	public final String getNamespace() {
		// logging support
		final String LOG_METHOD = "getNamespace()";
		// lazily access the namespace
		if (namespace == null) {
			// get the namespace
			namespace = response.getNamespace();
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Decoded the namespace as [{0}].", namespace);
			}
		}
		// the namespace
		return namespace;
	}
}
