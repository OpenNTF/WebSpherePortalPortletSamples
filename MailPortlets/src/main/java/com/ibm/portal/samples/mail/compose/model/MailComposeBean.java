package com.ibm.portal.samples.mail.compose.model;

import javax.portlet.PortletRequest;

import com.ibm.portal.samples.mail.common.AbstractBean;

/**
 * Helper bean to access mail aspects specific to the composer portlet
 * 
 * @author cleue
 */
public class MailComposeBean extends AbstractBean {

  public interface Dependencies extends AbstractBean.Dependencies {

  }

  public MailComposeBean(final PortletRequest aRequest, final Dependencies aDeps) {
    super(aRequest, aDeps);
  }
}
