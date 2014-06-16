package com.ibm.portal.samples.mail.view.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.xml.transform.sax.SAXResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.ibm.portal.resolver.xml.DisposableTransformer;
import com.ibm.portal.resolver.xml.PooledTemplates;
import com.ibm.portal.samples.mail.common.MessageBean;
import com.ibm.portal.samples.mail.view.controller.MailViewController;

/**
 * Filter around the document that represents the email. The filter rewrites
 * URLs to static resources such as embedded images.
 * 
 * @author cleue
 */
public class MessageParser extends XMLFilterImpl {

  public interface Dependencies {

    PooledTemplates getIdentityTemplates();
  }

  /**
   * Transfer object for the data to filter
   * 
   * @author cleue
   */
  private static final class InputSourceImpl extends InputSource {

    private final MailViewController controller;

    private final MessageBean message;

    private final MimeResponse response;

    private InputSourceImpl(final MimeResponse aResponse,
        final MessageBean aMessage, final MailViewController aController) {
      message = aMessage;
      response = aResponse;
      controller = aController;
    }
  }

  private static final String ATTR_SRC = Attribute.SRC.toString();

  private static final String ELEMENT_BODY = Tag.BODY.toString();

  private static final String ELEMENT_DIV = Tag.DIV.toString();

  private static final String ELEMENT_IMG = Tag.IMG.toString();

  /** class name for the logger */
  private static final String LOG_CLASS = MessageParser.class.getName();

  /** logging level */
  private static final Level LOG_LEVEL = Level.FINER;

  /** class logger */
  private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

  /**
   * Construct the transfer object
   * 
   * @param aResponse
   *          the response
   * @param aMessage
   *          the current message
   * @param aController
   *          the URL generation APIs
   * 
   * @return the transfer object
   */
  public static final InputSource getInputSource(final MimeResponse aResponse,
      final MessageBean aMessage, final MailViewController aController) {
    return new InputSourceImpl(aResponse, aMessage, aController);
  }

  /**
   * the attributes copy
   */
  private final AttributesImpl attrs = new AttributesImpl();

  /**
   * logging support
   */
  private boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

  /**
   * Efficient was to execute an identity transform
   */
  private final PooledTemplates identityTemplates;

  /**
   * the input elements
   */
  private InputSourceImpl is;

  /**
   * level count to filter out the html and head elements
   */
  private int nestingLevel;

  public MessageParser(final Dependencies aDeps) {
    identityTemplates = aDeps.getIdentityTemplates();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#characters(char[], int, int)
   */
  @Override
  public void characters(final char[] ch, final int start, final int length)
      throws SAXException {
    // only pass on inner content
    if (nestingLevel >= 0) {
      // pass on
      super.characters(ch, start, length);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    // logging support
    final String LOG_METHOD = "endDocument()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // done
    super.endDocument();
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(final String uri, final String localName,
      final String qName) throws SAXException {
    // logging support
    final String LOG_METHOD = "endElement(uri, localName, qName)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { uri, localName,
          qName, nestingLevel });
    }
    // done
    if (nestingLevel > 0) {
      // just pass thru
      nestingLevel--;
      super.endElement(uri, localName, qName);
    } else if (nestingLevel == 0) {
      // convert body to a div
      nestingLevel = Integer.MIN_VALUE;
      super.endElement(uri, ELEMENT_DIV, ELEMENT_DIV);
    } else {
      // log this
      if (bIsLogging) {
        LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Ignoring element.");
      }
    }
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#parse(org.xml.sax.InputSource)
   */
  @Override
  public void parse(final InputSource input) throws SAXException, IOException {
    // logging support
    final String LOG_METHOD = "parse(input)";
    bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // the source
    is = (InputSourceImpl) input;
    nestingLevel = Integer.MIN_VALUE;
    try {
      // parse
      final DisposableTransformer trfrm = identityTemplates.newTransformer();
      try {
        // transform
        trfrm.transform(is.message.getMessageSource(), new SAXResult(this));
      } finally {
        // done
        trfrm.dispose();
      }
    } catch (final Exception ex) {
      // wrap and bail out
      throw new SAXException(ex);
    } finally {
      // reset
      is = null;
    }
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /**
   * Construct a resource URL to the message part
   * 
   * @param aHref
   *          the reference
   * @return the message part
   */
  private final String rewrite(final String aHref) {
    // logging support
    final String LOG_METHOD = "rewrite(aHref)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aHref });
    }
    // construct a reference to the portlet
    final ResourceURL url = is.controller.getMessageResourceURL(aHref);
    final String result = url.toString();
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
    }
    // ok
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
    // logging support
    final String LOG_METHOD = "startDocument()";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD);
    }
    // start
    super.startDocument();
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String,
   * java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(final String uri, final String localName,
      final String qName, final Attributes atts) throws SAXException {
    // logging support
    final String LOG_METHOD = "startElement(uri, localName, qName, atts)";
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { uri, localName,
          qName, atts, nestingLevel });
    }
    // only pass on the body
    if (nestingLevel < 0) {
      // check for the body
      if (ELEMENT_BODY.equalsIgnoreCase(localName)) {
        // convert body to a div
        nestingLevel = 0;
        super.startElement(uri, ELEMENT_DIV, ELEMENT_DIV, atts);
      } else {
        // log this
        if (bIsLogging) {
          LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Ignoring element.");
        }
      }
    } else {
      // increment
      nestingLevel++;
      // delegate attributes
      final Attributes delegate;
      // check if we must rewrite the source reference
      if (ELEMENT_IMG.equalsIgnoreCase(localName)) {
        // copy
        attrs.clear();
        attrs.setAttributes(atts);
        // check for the href
        final int idx = attrs.getIndex(ATTR_SRC);
        if (idx >= 0) {
          attrs.setValue(idx, rewrite(attrs.getValue(idx)));
        }
        // pass on
        delegate = attrs;
      } else {
        // nothing to change
        delegate = atts;
      }
      // pass on
      super.startElement(uri, localName, qName, delegate);
    }
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }
}
