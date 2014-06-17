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
package com.ibm.portal.samples.mail.list.model;

import static javax.mail.Message.RecipientType.TO;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.xml.namespace.QName;

import com.ibm.portal.Committable;
import com.ibm.portal.Disposable;
import com.ibm.portal.model.controller.exceptions.CannotCommitException;
import com.ibm.portal.samples.mail.common.SendEventBean;

/**
 * Event implementation that handles an incoming event. The {@link QName} of the
 * event is mapped to an {@link EVENT} enumeration that couples the event to its
 * implementation.
 * 
 * Event implementations modify the underlying {@link MailListModel} which will
 * be encoded into the navigational state after the end of the action phase. If
 * an action resulted in a persistent change of data, the action implementation
 * should return <code>true</code> to indicate this and the framework will call
 * the {@link MailListActions#commit()} method.
 * 
 * @author cleue
 */
public class MailListEvents implements Committable, Disposable {

	public enum EVENT {

		/**
		 * Event that initializes the portlet
		 */
		SEND_MAIL(SendEventBean.EVENT_NAME) {

			@Override
			protected boolean processEvent(Event aEvent,
					final MailListEvents aHandler) throws Exception {
				// dispatch
				return aHandler.processSendMail(aEvent);
			}
		};

		/**
		 * QNAme of the event
		 */
		private final QName qName;

		private EVENT(QName aName) {
			qName = aName;
		}

		/**
		 * Executes the action on the model
		 * 
		 * @param aEvent
		 *            the event to process
		 * @return <code>true</code> if the action modified persistent state,
		 *         else <code>false</code>
		 * 
		 * @throws Exception
		 */
		protected abstract boolean processEvent(final Event aEvent,
				final MailListEvents aHandler) throws Exception;
	}

	/**
	 * maps from QName to event handler
	 */
	private static final Map<QName, EVENT> EVENT_MAP = createEventMap(EVENT
			.values());

	/** class name for the logger */
	private static final String LOG_CLASS = MailListEvents.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * Maps from QName to event
	 * 
	 * @param aValues
	 *            the available events
	 * @return the mapping
	 */
	private static final Map<QName, EVENT> createEventMap(final EVENT[] aValues) {
		final Map<QName, EVENT> tmp = new HashMap<QName, EVENT>();
		for (EVENT event : aValues) {
			tmp.put(event.qName, event);
		}
		return tmp;
	}

	/**
	 * access to the mail APIs
	 */
	private final MailListBean bean;

	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * the event request
	 */
	private final EventRequest request;

	public MailListEvents(final MailListModel aModel, final MailListBean aBean,
			final EventRequest aRequest) {
		// sanity check
		assert aModel != null;
		assert aBean != null;
		assert aRequest != null;
		// maintain a model reference
		bean = aBean;
		request = aRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.Committable#commit()
	 */
	@Override
	public void commit() throws CannotCommitException {
		// TODO Auto-generated method stub

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
		// nothing special to do

		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	public boolean processEvents() throws Exception {
		// logging support
		final String LOG_METHOD = "processEvents()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the result
		final boolean bResult;
		// try to decode the event
		final Event event = request.getEvent();
		assert event != null;
		// find the handler
		final EVENT handler = EVENT_MAP.get(event.getQName());
		if (handler != null) {
			// process
			bResult = handler.processEvent(event, this);
		} else {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Unable to locate a handler for event [{0}].",
						event.getQName());
			}
			// nothing done
			bResult = false;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}

	/**
	 * Process the {@link SendEventBean#EVENT_NAME} event
	 * 
	 * @param aEvent
	 *            event object
	 * @return <code>true</code> if the processing resulted in a persistent
	 *         modification, else <code>false</code>
	 * @throws MessagingException
	 * @throws AddressException
	 */
	private final boolean processSendMail(final Event aEvent)
			throws AddressException, MessagingException {
		// logging support
		final String LOG_METHOD = "processSendMail(aEvent)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aEvent });
		}
		// access the payload
		final SendEventBean sendBean = (SendEventBean) aEvent.getValue();
		assert sendBean != null;
		// log this
		if (bIsLogging) {
			LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
					"SendEventBean: [{0}].", sendBean);
		}
		// construct the message
		final Message message = bean.createMessage();
		message.setRecipients(TO, InternetAddress.parse(sendBean.getAddress()));
		message.setSubject(sendBean.getSubject());
		message.setText(sendBean.getText());
		// send
		bean.sendMessage(message);
		// log this
		if (bIsLogging) {
			LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
					"Sent message to [{0}].", sendBean.getAddress());
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// nothing persistent changed
		return false;
	}
}
