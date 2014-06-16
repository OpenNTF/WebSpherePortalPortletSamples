package com.ibm.portal.samples.mail.view.model;

import javax.portlet.PortletRequest;

import com.ibm.portal.samples.mail.common.AbstractBean;

/**
 * Helper bean for mail view
 * 
 * @author cleue
 */
public class MailViewBean extends AbstractBean {

  public interface Dependencies extends AbstractBean.Dependencies {
  }

  public MailViewBean(final PortletRequest aRequest, final Dependencies aDeps) {
    super(aRequest, aDeps);
  }
}
