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
package com.ibm.portal.samples.mail.common;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import com.ibm.portal.samples.mail.helper.AbstractJstlMap;

/**
 * Bean that represents formatting related aspects. We prefer to use a bean over
 * the JSTL fmt tags, because the tags do not directly support escaping and it
 * is tedious and error prone to copy every string into a temporary variable in
 * the JSP just to escape it later. The JSP becomes more readable using the bean
 * approach.
 * 
 * @author cleue
 */
public class AbstractView {

  /** class name for the logger */
  private static final String LOG_CLASS = AbstractView.class.getName();

  /** logging level */
  private static final Level LOG_LEVEL = Level.FINER;

  /** class logger */
  private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

  /**
   * default resource bundle for the portlet
   */
  private final ResourceBundle bundle;

  /**
   * logging support, we can do this as a instance variable since the model bean
   * is instantiated for every request
   */
  private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

  /**
   * the message format
   */
  private MessageFormat msgFormat;

  /**
   * the date format
   */
  private DateFormat dateFormat;

  /**
   * selected locale
   */
  private final Locale locale;

  /**
   * the string buffer
   */
  private StringBuffer buffer;

  /**
   * field position used for formatting
   */
  private FieldPosition fieldPosition;

  /**
   * message access
   */
  private Map<Object, Object> messageMap;

  /**
   * date access
   */
  private Map<Object, Object> dateMap;

  /**
   * the response
   */
  private final MimeResponse response;

  /**
   * the request
   */
  private final PortletRequest request;

  /**
   * URL to the blank image
   */
  private String blankURL;

  private String namespace;

  /**
   * Person image URLs
   */
  private Map<Object, Object> personImageURL;

  /**
   * Initialize this view bean
   * 
   * @param aConfig
   *          portlet config, used to access the resource bundle
   * @param aResponse
   *          the response, used to access the desired locale
   * @param aModel
   *          model
   */
  protected AbstractView(final PortletConfig aConfig,
      final PortletRequest aRequest, final MimeResponse aResponse) {
    // logging support
    final String LOG_METHOD = "CustomerRecordView(aConfig, aRequest, aResponse)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD,
          new Object[] { aConfig, aResponse });
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
   * Possibility to clean up
   */
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
   * Formats the date
   * 
   * @param aDate
   *          the date
   * @return the string
   */
  private final String formatDate(final Date aDate) {
    // logging support
    final String LOG_METHOD = "formatDate(aDate)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aDate });
    }
    // format
    final DateFormat fmt = getDateFormat();
    final StringBuffer result = fmt.format(aDate, getBuffer(),
        getFieldPosition());
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
    }
    // ok
    return result.toString();
  }

  /**
   * Formats the message from the bundle with the given parameters
   * 
   * @param aKey
   *          the bundle key
   * @param aParams
   *          the parameters
   * @return the string
   */
  protected final String formatMessage(final String aKey,
      final Object... aParams) {
    // logging support
    final String LOG_METHOD = "formatMessage(aKey, aParams)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD,
          new Object[] { aKey, Arrays.toString(aParams) });
    }
    // format
    final MessageFormat fmt = getMessageFormat();
    fmt.applyPattern(bundle.getString(aKey));
    final StringBuffer result = fmt.format(aParams, getBuffer(),
        getFieldPosition());
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
    }
    // ok
    return result.toString();
  }

  /**
   * Returns the URL to the blank image
   * 
   * @return
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
   * Returns an initialized string buffer
   * 
   * @return the buffer
   */
  private final StringBuffer getBuffer() {
    // lazily construct the buffer
    if (buffer == null) {
      buffer = new StringBuffer();
    } else {
      buffer.setLength(0);
    }
    return buffer;
  }

  /**
   * Returns indexed access to a date
   * 
   * @return map view to the entry
   */
  public Map<Object, Object> getDate() {
    if (dateMap == null) {
      dateMap = new AbstractJstlMap<Object, Object>() {
        @Override
        protected Object getValue(final Object key) throws Exception {
          // dispatch
          return getDate((Date) key);
        }
      };

    }
    return messageMap;
  }

  /**
   * Actually formats the date
   * 
   * @param aDate
   *          the date to format
   * @return the formatted date
   */
  protected final String getDate(final Date aDate) {
    // dispatch
    return formatDate(aDate);
  }

  /**
   * Returns a date format instance for the locale
   * 
   * @return the date format instance
   */
  private final DateFormat getDateFormat() {
    // lazily initialize the date format
    if (dateFormat == null) {
      dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    }
    // returns the format
    return dateFormat;
  }

  /**
   * Returns an initialized field position
   * 
   * @return the position
   */
  private FieldPosition getFieldPosition() {
    // lazily construct the position
    if (fieldPosition == null) {
      fieldPosition = new FieldPosition(0);
    } else {
      fieldPosition.setBeginIndex(0);
      fieldPosition.setEndIndex(0);
    }
    return fieldPosition;
  }

  public Locale getLocale() {
    return response.getLocale();
  }

  /**
   * Returns indexed access to a simple resource bundle entry
   * 
   * @return map view to the entry
   */
  public Map<Object, Object> getMessage() {
    if (messageMap == null) {
      messageMap = new AbstractJstlMap<Object, Object>() {
        @Override
        protected Object getValue(final Object key) throws Exception {
          // dispatch
          return getMessage(key.toString());
        }
      };

    }
    return messageMap;
  }

  /**
   * Returns the message from the bundle
   * 
   * @param aKey
   *          the bundle key
   * @return the formatted string
   */
  protected final String getMessage(final String aKey) {
    // logging support
    final String LOG_METHOD = "getMessage(aKey)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aKey });
    }
    // bundle results
    final String result = bundle.getString(aKey);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
    }
    // ok
    return result;
  }

  /**
   * Returns a message format instance for the locale
   * 
   * @return the message format instance
   */
  private final MessageFormat getMessageFormat() {
    // lazily initialize the message format
    if (msgFormat == null) {
      msgFormat = new MessageFormat("", locale);
    }
    // returns the format
    return msgFormat;
  }

  /**
   * Decodes the namespace identifier
   * 
   * @return namespace identifier
   */
  public String getNamespace() {
    // logging support
    final String LOG_METHOD = "getNamespace()";
    if (namespace == null) {
      // decode the namespace
      namespace = response.getNamespace();
      // log this
      if (bIsLogging) {
        LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Namespace [{0}].",
            namespace);
      }
    }
    return namespace;
  }

  /**
   * Returns indexed access to a formatted date
   * 
   * @return map view to the date
   */
  public Map<Object, Object> getPersonImageURL() {
    if (personImageURL == null) {
      personImageURL = new AbstractJstlMap<Object, Object>() {
        @Override
        protected Object getValue(final Object key) throws Exception {
          // dispatch
          return getPersonImageURL((MessageBean) key);
        }
      };

    }
    return personImageURL;
  }

  /**
   * Produces a URL to the person image
   * 
   * @param aBean
   *          the message bean
   * @return the URL
   */
  private final String getPersonImageURL(final MessageBean aBean) {
    String path = null;
    try {
      path = getMessage(aBean.getSenderMail());
    } catch (final Exception ex) {
      path = getMessage("person.unknown");
    }
    // produce a direct URL to the image
    return getResourceURL(path);
  }

  /**
   * Generates a URL to a static resource
   * 
   * @param aPath
   *          the resource path
   * @return the URL
   */
  public String getResourceURL(final String aPath) {
    // produces and encoded URL
    return response.encodeURL(request.getContextPath() + aPath);
  }
}