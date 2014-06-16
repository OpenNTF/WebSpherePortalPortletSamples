package com.ibm.portal.samples.mail.compose.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.xml.namespace.QName;

import com.ibm.portal.Committable;
import com.ibm.portal.Disposable;
import com.ibm.portal.model.controller.exceptions.CannotCommitException;
import com.ibm.portal.samples.mail.common.SendEventBean;

public class MailComposeEvents implements Committable, Disposable {

	public enum EVENT {

		/**
		 * Event that initializes the portlet
		 */
		SEND_MAIL(SendEventBean.EVENT_NAME) {

			@Override
			protected boolean processEvent(Event aEvent, final MailComposeEvents aHandler) throws Exception {
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
		protected abstract boolean processEvent(final Event aEvent, final MailComposeEvents aHandler)
				throws Exception;
	}

	/**
	 * maps from QName to event handler
	 */
	private static final Map<QName, EVENT> EVENT_MAP = createEventMap(EVENT.values());

	/** class name for the logger */
	private static final String LOG_CLASS = MailComposeEvents.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;
	
	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);
	
	/**
	 * Maps from QName to event
	 * 
	 * @param aValues	the available events
	 * @return	the mapping
	 */
	private static final Map<QName, EVENT> createEventMap(final EVENT[] aValues) {		
		final Map<QName, EVENT> tmp = new HashMap<QName, EVENT>();
		for (EVENT event : aValues) {
			tmp.put(event.qName, event);
		}
		return tmp;
	}
	
	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL); 

	/**
	 * current model
	 */
	private final MailComposeModel model;

	/**
	 * the event request
	 */
	private final EventRequest request;

	public MailComposeEvents(final MailComposeModel aModel,
			final EventRequest aRequest) {
		// sanity check
		assert aModel != null;
		// maintain a model reference
		model = aModel;
		request = aRequest;
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
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Unable to locate a handler for event [{0}].", event.getQName());
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
	 */
	private final boolean processSendMail(final Event aEvent) {
		// logging support
		final String LOG_METHOD = "processSendMail(aEvent)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aEvent });
		}
		// access the payload
		final SendEventBean sendBean = (SendEventBean) aEvent.getValue();
		// copy over the state
		model.setAddress(sendBean.getAddress());
		model.setSubject(sendBean.getSubject());
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// nothing persistent changed
		return false;
	}

	@Override
	public void commit() throws CannotCommitException {
		// TODO Auto-generated method stub
		
	}
}
