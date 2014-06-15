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
}
