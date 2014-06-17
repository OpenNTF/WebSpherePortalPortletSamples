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
package com.ibm.portal.samples.mail.view.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.StateAwareResponse;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.common.AbstractModel;
import com.ibm.portal.samples.mail.common.MessageBean;

/**
 * Representation of the navigational state of the view
 * 
 * @author cleue
 * 
 */
public class MailViewModel extends AbstractModel implements Disposable, Cloneable {

  public interface Dependencies extends AbstractModel.Dependencies {
	  
  }

  /**
   * enumeration of the states we need to encode our view
   */
  private enum STATE {
    /** request to render the message asynchroneously */
    MESSAGE_RENDER,
    /** resource */
    MESSAGE_RESOURCE
  }

  /**
   * default resource identifier
   */
  private static final String DEFAULT_MESSAGE_RESOURCE = null;

  /**
   * default selection
   */
  private static final long DEFAULT_SELECTION_ID = Long.MIN_VALUE;

  /**
   * local name of the public render parameter that identifies the selection
   */
  private static final String KEY_SELECTED = "sel";

  /** class name for the logger */
  private static final String LOG_CLASS = MailViewModel.class.getName();

  /** logging level */
  private static final Level LOG_LEVEL = Level.FINER;

  /** class logger */
  private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

  /**
   * async rendering
   */
  private static final boolean RENDER_ASNYC = true;

  /**
   * checks if the application is authenticated
   */
  private Boolean bAuthenticated;

  /**
   * access to the services
   */
  private final MailViewBean bean;

  /**
   * logging support, we can do this as a instance variable since the model bean
   * is instantiated for every request
   */
  private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

  /**
   * checks if the request is one to render the message
   */
  private boolean bIsRenderMessage;

  private Folder folder;

  /**
   * the resource
   */
  private String messageResource;
  
  /**
   * ID of the currently selected item
   */
  private final long selectedId;

  /**
   * The currently selected message
   */
  private MessageBean selectedMessage;

  /**
   * Decode the model from the request
   * 
   * @param aBean
   *          access to services
   * @param aReq
   *          the request
   */
  public MailViewModel(final MailViewBean aBean, final PortletRequest aReq, final Dependencies aDeps) {
    // default handling
    super(aReq, aDeps);
    // logging support
    final String LOG_METHOD = "MailViewModel(aBean, aReq)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aBean, aReq });
    }
    // decode
    bean = aBean;
    selectedId = decode(KEY_SELECTED, DEFAULT_SELECTION_ID);
    messageResource = decode(STATE.MESSAGE_RESOURCE,
        DEFAULT_MESSAGE_RESOURCE);
    bIsRenderMessage = decode(STATE.MESSAGE_RENDER, !RENDER_ASNYC);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, this);
    }
  }

  /**
   * Initializes the model as a copy of another model
   * 
   * @param aCopy
   *          the model to copy
   */
  protected MailViewModel(final MailViewModel aCopy) {
    super(aCopy);
    bean = aCopy.bean;
    selectedId = aCopy.selectedId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public MailViewModel clone() {
    // generate a new copy
    return new MailViewModel(this);
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
   * Encodes the stat of the model into a URL
   * 
   * @param url
   *          target URL
   */
  public void encode(final BaseURL url) {
    // logging support
    final String LOG_METHOD = "encode(url)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
    }
    // encode the resource identifier
    encode(url, STATE.MESSAGE_RESOURCE, messageResource,
        DEFAULT_MESSAGE_RESOURCE);
    encode(url, STATE.MESSAGE_RENDER, bIsRenderMessage, !RENDER_ASNYC);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /**
   * Encodes the stat of the model into the response. It is unfortunate that we
   * need to write this method reduntantly to {@link #encode(PortletURL)} but
   * there exists no common interface between {@link PortletURL} and
   * {@link StateAwareResponse} that would allow to write parameters using
   * common code.
   * 
   * @param resp
   *          response
   */
  public void encode(final StateAwareResponse resp) {
    // logging support
    final String LOG_METHOD = "encode(resp)";
    final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
    }
    // encode the resource identifier
    encode(resp, STATE.MESSAGE_RESOURCE, messageResource,
        DEFAULT_MESSAGE_RESOURCE);
    encode(resp, STATE.MESSAGE_RENDER, bIsRenderMessage, !RENDER_ASNYC);

    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /**
   * Access the folder
   * 
   * @return current folder
   * @throws MessagingException
   */
  private final Folder getFolder() throws MessagingException {
    if (folder == null) {
      folder = bean.getFolder("Inbox");
    }
    return folder;
  }

  /**
   * Returns the ID of the resource inside the message
   * 
   * @return the resource identifier
   */
  public String getMessageResource() {
    return messageResource;
  }

  /**
   * Returns the currently selected ID
   * 
   * @return the selected ID
   */
  public long getSelectedId() {
    return selectedId;
  }

  /**
   * Returns the currently selected message
   * 
   * @return the current message
   */
  public MessageBean getSelectedMessage() {
    // try to resolve the selected message
    if ((selectedMessage == null) && hasSelectedMessage()) {
      // check if we have the message cached
      selectedMessage = bean.getMessage(getSelectedId());
      if (selectedMessage == null) {
        try {
          // access the folder
          final Folder folder = getFolder();
          assert folder != null;
          // make sure to open the folder
          if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
          }
          // resolve the message
          final UIDFolder uidFolder = (UIDFolder) folder;
          // resolve this beast
          final long selId = getSelectedId();
          final Message msg = uidFolder.getMessageByUID(selId);
          // resolve to the bean
          selectedMessage = bean.resolveMessage(getSelectedId(), msg);
        } catch (final MessagingException ex) {
          // just log
          logException(ex);
        }
      }
    }
    // returns the message
    return selectedMessage;
  }

  /**
   * Checks if a selected message exists
   * 
   * @return <code>true</code> if a selected page exists, else
   *         <code>false</code>
   */
  private final boolean hasSelectedMessage() {
    return (selectedId != Long.MIN_VALUE);
  }

  /**
   * Checks if the application is authenticated
   * 
   * @return <code>true</code> if authenticated, else <code>false</code>
   */
  public boolean isAuthenticated() {
    // logging support
    final String LOG_METHOD = "isAuthenticated()";
    // lazily check if the application is authenticated
    if (bAuthenticated == null) {
      try {
        // try to access the session
        bAuthenticated = getFolder() != null;
      } catch (MessagingException ex) {
        // not authenticated
        bAuthenticated = false;
      }
      // log this
      if (bIsLogging) {
        LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Authentication [{0}].",
            bAuthenticated);
      }
    }
    // true for an authenticated session
    return bAuthenticated;
  }

  /**
   * Checks if the model targets message rendering
   * 
   * @return <code>true</code> for message rendering, else <code>false</code>
   */
  public boolean isRenderMessage() {
    return bIsRenderMessage;
  }

  /**
   * Selectes a particular resource from the message
   * 
   * @param aMessageResource
   *          the resource
   */
  public void setMessageResource(final String aMessageResource) {
    // logging support
    final String LOG_METHOD = "setMessageResource(aMessageResource)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aMessageResource });
    }
    // get the resource
    messageResource = aMessageResource;
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /**
   * Enables message rendering
   * 
   * @param aRenderMessage
   *          the desired flag
   */
  public void setRenderMessage(boolean aRenderMessage) {
    bIsRenderMessage = aRenderMessage;
  }
}
