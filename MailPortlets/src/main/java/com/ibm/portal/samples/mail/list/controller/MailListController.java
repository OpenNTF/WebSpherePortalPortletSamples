package com.ibm.portal.samples.mail.list.controller;

import static com.ibm.portal.samples.mail.list.MailListPortlet.ACTIONS.AUTHENTICATE;
import static javax.portlet.ActionRequest.ACTION_NAME;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.mail.helper.AbstractJstlMap;
import com.ibm.portal.samples.mail.list.model.MailListModel;

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
public class MailListController implements Disposable {

  /**
   * Use an enum to manage our input fields. This allows us to assign really
   * small identifiers, while still generating readable code.
   * 
   * @author cleue
   */
  public enum CONTROLS {

    CTRL_PASSWORD, CTRL_SAVE, CTRL_USERNAME;

    /**
     * access the enumeration values without making a copy every time
     */
    private static final CONTROLS[] VALUES = values();

    /**
     * Converts from ID to action
     * 
     * @param aID
     *          the ID
     * @return the matching action identifier
     */
    public static final CONTROLS fromId(final String aID) {
      return VALUES[Integer.parseInt(aID)];
    }

    // use some short, optimized encoding
    private final String id = String.valueOf(ordinal());

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      // returns a short identifier
      return id;
    }

  }

  /** class name for the logger */
  private static final String LOG_CLASS = MailListController.class.getName();

  /** logging level */
  private static final Level LOG_LEVEL = Level.FINER;

  /** class logger */
  private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

  /**
   * logging support, we can do this as a instance variable since the controller
   * bean is instantiated for every request
   */
  private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

  /**
   * base model that we operate on
   */
  private final MailListModel model;

  /**
   * helper map to expose indexed access to the sort orders to JSTL
   */
  private Map<Object, Object> nextSortOrderURL;

  /**
   * helper map to expose indexed access to the page size URLs to JSTL
   */
  private Map<Object, Object> pageSizeURL;

  /**
   * helper map to expose indexed access to the quick page URLs to JSTL
   */
  private Map<Object, Object> quickPageURL;

  /**
   * the portlet response, used to construct the URLs
   */
  private final MimeResponse response;

  /**
   * helper map to expose indexed access to the selection URLs to JSTL
   */
  private Map<Object, Object> selectionURL;

  /**
   * Initialize teh controller
   * 
   * @param aModel
   *          the base model
   * @param aResponse
   *          the portlet response
   */
  public MailListController(final MailListModel aModel,
      final MimeResponse aResponse) {
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
   * Returns a generic action URL. It is not necessary to set an action ID,
   * since we rather transport the action ID via the multipart input stream.
   * 
   * @return the action URL
   * @throws IOException
   */
  public PortletURL getActionURL() throws IOException {
    // logging support
    final String LOG_METHOD = "getActionURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the URL
    final PortletURL url = response.createActionURL();
    model.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Constructs the action URL that targets the send mail action
   * 
   * @return the URL
   * 
   * @throws IOException
   */
  public PortletURL getAuthenticateURL() throws IOException {
    // logging support
    final String LOG_METHOD = "getAuthenticateURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the URL
    final PortletURL actionURL = response.createActionURL();
    actionURL.setParameter(ACTION_NAME, AUTHENTICATE.toString());
    // encode our state
    model.encode(actionURL);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, actionURL);
    }
    // ok
    return actionURL;
  }

  /**
   * Returns the resource serving URL for the portlet resource
   * 
   * @return the resource URL
   */
  public ResourceURL getMailCheckURL() {
    // logging support
    final String LOG_METHOD = "getMailCheckURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the resource URL
    final ResourceURL url = response.createResourceURL();
    url.setCacheability(ResourceURL.PORTLET);
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setCheckMail();
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // ok
    return url;
  }

  /**
   * Returns the URL to the next page
   * 
   * @return the URL
   * 
   * @throws IOException
   */
  public PortletURL getNextPageURL() throws IOException {
    // logging support
    final String LOG_METHOD = "getNextPageURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setNextPage();
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns a helper map that provides indexed access to the sort order URLs
   * 
   * @return the map
   */
  public Map<Object, Object> getNextSortOrderURL() {
    if (nextSortOrderURL == null) {
      nextSortOrderURL = new AbstractJstlMap<Object, Object>() {
        @Override
        public Object getValue(final Object key) throws Exception {
          // returns the URL
          return getNextSortOrderURL((Integer) key);
        }
      };
    }
    return nextSortOrderURL;
  }

  /**
   * Returns a URL that changes the sort order to the given column
   * 
   * @param aColumn
   *          the column
   * @return the URL
   * 
   * @throws IOException
   */
  private final PortletURL getNextSortOrderURL(final int aColumn)
      throws IOException {
    // logging support
    final String LOG_METHOD = "getNextSortOrderURL(aColumn)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aColumn });
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setNextSortOrder(aColumn);
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns a helper map that provides indexed access to the page size URLs
   * 
   * @return the map
   */
  public Map<Object, Object> getPageSizeURL() {
    if (pageSizeURL == null) {
      pageSizeURL = new AbstractJstlMap<Object, Object>() {
        @Override
        public Object getValue(final Object key) throws Exception {
          // returns the URL
          return getPageSizeURL(((Number) key).intValue());
        }
      };
    }
    return pageSizeURL;
  }

  /**
   * Returns a URL that sets the page size
   * 
   * @param aPageSize
   *          the page size
   * @return the URL
   * 
   * @throws IOException
   */
  private final PortletURL getPageSizeURL(final int aPageSize)
      throws IOException {
    // logging support
    final String LOG_METHOD = "getPageSizeURL(aPageSize)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aPageSize });
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setPageSize(aPageSize);
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns the short ID for the control
   * 
   * @return the short identifier
   */
  public String getPasswordId() {
    return CONTROLS.CTRL_PASSWORD.toString();
  }

  /**
   * Returns the URL to the previous page
   * 
   * @return the URL
   * 
   * @throws IOException
   */
  public PortletURL getPreviousPageURL() throws IOException {
    // logging support
    final String LOG_METHOD = "getPreviousPageURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setPreviousPage();
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns a helper map that provides indexed access to the quick page URLs
   * 
   * @return the map
   */
  public Map<Object, Object> getQuickPageURL() {
    if (quickPageURL == null) {
      quickPageURL = new AbstractJstlMap<Object, Object>() {
        @Override
        public Object getValue(final Object key) throws Exception {
          // returns the URL
          return getQuickPageURL(((Number) key).intValue());
        }
      };
    }
    return quickPageURL;
  }

  /**
   * Returns a URL that selects a particular page
   * 
   * @param aPageIdx
   *          the page index
   * @return the URL
   * 
   * @throws IOException
   */
  private final PortletURL getQuickPageURL(final int aPageIdx)
      throws IOException {
    // logging support
    final String LOG_METHOD = "getQuickPageURL(aPageIdx)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aPageIdx });
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setCurrentPage(aPageIdx);
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns the resource serving URL for the portlet resource
   * 
   * @return the resource URL
   */
  public ResourceURL getRefreshURL() {
    // logging support
    final String LOG_METHOD = "getRefreshURL()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // construct the resource URL
    final ResourceURL url = response.createResourceURL();
    url.setCacheability(ResourceURL.PAGE);
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setRefresh();
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // ok
    return url;
  }

  /**
   * Returns the short ID for the control
   * 
   * @return the short identifier
   */
  public String getSaveId() {
    return CONTROLS.CTRL_SAVE.toString();
  }

  /**
   * Returns a helper map that provides indexed access to the selection URLs
   * 
   * @return the map
   */
  public Map<Object, Object> getSelectionURL() {
    if (selectionURL == null) {
      selectionURL = new AbstractJstlMap<Object, Object>() {
        @Override
        public Object getValue(final Object key) throws Exception {
          // returns the URL
          return getSelectionURL(((Number) key).longValue());
        }
      };
    }
    return selectionURL;
  }

  /**
   * Returns a URL that selects a particular item
   * 
   * @param aSelectionId
   *          the ID of the selected item
   * @return the URL
   * 
   * @throws IOException
   */
  private final PortletURL getSelectionURL(final long aSelectionId)
      throws IOException {
    // logging support
    final String LOG_METHOD = "getSelectionURL(aSelectionId)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aSelectionId });
    }
    // construct the URL
    final PortletURL url = response.createRenderURL();
    // important, work on a copy of the current model
    final MailListModel copy = model.clone();
    copy.setSelectedId(aSelectionId);
    copy.encode(url);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, url);
    }
    // return the result
    return url;
  }

  /**
   * Returns the short ID for the control
   * 
   * @return the short identifier
   */
  public String getUserNameId() {
    return CONTROLS.CTRL_USERNAME.toString();
  }
}
