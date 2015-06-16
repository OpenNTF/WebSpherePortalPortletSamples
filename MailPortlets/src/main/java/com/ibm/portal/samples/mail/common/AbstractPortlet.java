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
