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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

/**
 * Data transfer object that represents a simple email message.
 * 
 * @author cleue
 * 
 */
@XmlRootElement
public class SendEventBean implements Serializable {

	/**
	 * QName of the event
	 */
	public static final QName EVENT_NAME = new QName(
			"http://www.ibm.com/xmlns/prod/websphere/portal/publicparams/sample/mail",
			"send");

	/**
	 * serialization support
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * email of the receiver
	 */
	private String address;

	/**
	 * mail subject
	 */
	private String subject;

	/**
	 * body text
	 */
	private String text;

	public String getAddress() {
		return address;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public void setReceiver(String aReceiver) {
		address = aReceiver;
	}

	public void setSubject(String aSubject) {
		subject = aSubject;
	}

	public void setText(String aText) {
		text = aText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// construct some debug string
		return "[address: " + address + ", subject: " + subject + ", body: "
				+ text + "]";
	}
}
