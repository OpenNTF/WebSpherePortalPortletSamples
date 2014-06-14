package com.ibm.portal.samples.mail.common;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletRequest;

import com.ibm.portal.Disposable;

public class AbstractPortlet extends GenericPortlet {

	protected void removeBean(final String aKey, final PortletRequest aRequest) {
		final Object obj = aRequest.getAttribute(aKey);
		if (obj instanceof Disposable) {
			((Disposable) obj).dispose();
		}
		aRequest.removeAttribute(aKey);
	}

	protected void setBean(final String aKey, final Object aBean,
			final PortletRequest aRequest) {
		aRequest.setAttribute(aKey, aBean);
	}

}
