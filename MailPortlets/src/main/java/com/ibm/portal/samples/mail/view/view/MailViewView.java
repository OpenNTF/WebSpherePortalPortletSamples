package com.ibm.portal.samples.mail.view.view;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import com.ibm.portal.samples.mail.common.AbstractView;
import com.ibm.portal.samples.mail.common.MessageBean;
import com.ibm.portal.samples.mail.helper.AbstractJstlMap;
import com.ibm.portal.samples.mail.view.controller.MailViewController;
import com.ibm.portal.samples.mail.view.model.MailViewModel;

/**
 * Bean that represents formatting related aspects. We prefer to use a bean over
 * the JSTL fmt tags, because the tags do not directly support escaping and it
 * is tedious and error prone to copy every string into a temporary variable in
 * the JSP just to escape it later. The JSP becomes more readable using the bean
 * approach.
 * 
 * @author cleue
 */
public class MailViewView extends AbstractView {

	public interface Dependencies extends MessageParser.Dependencies {

	}

	/** class name for the logger */
	private static final String LOG_CLASS = MailViewView.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support, we can do this as a instance variable since the model
	 * bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * access to the controller
	 */
	private final MailViewController controller;

	/**
	 * date formatting
	 */
	private Map<Object, Object> dateMap;

	private final Dependencies dependencies;

	/**
	 * URL to the loading image
	 */
	private String loadingURL;

	/**
	 * Reply
	 */
	private String mailReply;

	/**
	 * out parser
	 */
	private MessageParser messageParser;

	/**
	 * the request
	 */
	private final PortletRequest request;

	private final MimeResponse response;

	/**
	 * Jump to page {0}
	 */
	private Map<Object, Object> rewrite;

	/**
	 * Initialize this view bean
	 * 
	 * @param aConfig
	 *            portlet config, used to access the resource bundle
	 * @param aResponse
	 *            the response, used to access the desired locale
	 * @param aModel
	 *            model
	 */
	public MailViewView(final PortletConfig aConfig,
			final PortletRequest aRequest, final MimeResponse aResponse,
			final MailViewModel aModel, final MailViewController aController,
			final Dependencies aDeps) {
		// default init
		super(aConfig, aRequest, aResponse);
		// logging support
		final String LOG_METHOD = "MailViewView(aConfig, aRequest, aResponse, aModel, aController, aDeps)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aConfig,
					aRequest, aResponse, aModel });
		}
		controller = aController;
		dependencies = aDeps;
		request = aRequest;
		response = aResponse;
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns indexed access to a formatted date
	 * 
	 * @return map view to the date
	 */
	@Override
	public Map<Object, Object> getDate() {
		if (dateMap == null) {
			dateMap = new AbstractJstlMap<Object, Object>() {

				@Override
				protected Object getValue(final Object key) throws Exception {
					// dispatch
					return getDate((Date) key);
				}
			};

		}
		return dateMap;
	}

	/**
	 * Returns the URL to the blank image
	 * 
	 * @return
	 */
	public String getLoadingImageURL() {
		// logging support
		final String LOG_METHOD = "getLoadingImageURL()";
		// lazily compute the URL
		if (loadingURL == null) {
			// create the URL
			loadingURL = response.encodeURL(request.getContextPath()
					+ "/images/loading.gif");
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"URL to the loading image [{0}].", loadingURL);
			}
		}
		// returns the URL
		return loadingURL;
	}

	/**
	 * @return "Reply"
	 */
	public String getMailReply() {
		if (mailReply == null) {
			mailReply = getMessage("mail.reply");
		}
		return mailReply;
	}

	private final MessageParser getMessageParser() {
		if (messageParser == null) {
			messageParser = new MessageParser(dependencies);
		}
		// ok
		return messageParser;
	}

	/**
	 * Returns indexed access to the translated string
	 * 
	 * @return map view to the translation
	 */
	public Map<Object, Object> getRewrite() {
		if (rewrite == null) {
			rewrite = new AbstractJstlMap<Object, Object>() {
				@Override
				protected Object getValue(final Object key) throws Exception {
					// dispatch
					return getRewrite((MessageBean) key);
				}
			};

		}
		return rewrite;
	}

	private final Source getRewrite(final MessageBean aMessage) {
		return new SAXSource(getMessageParser(), MessageParser.getInputSource(
				response, aMessage, controller));
	}
}
