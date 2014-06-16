package com.ibm.portal.samples.mail.helper;

import static javax.xml.XMLConstants.NULL_NS_URI;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Simplistic HTML parser that converts a character stream into a SAX stream for
 * easier processing. Not rocket science but sufficient for the demo.
 * 
 * @author cleue
 * 
 */
public class HtmlParser extends XMLFilterImpl {

  /**
   * Wrapper between the JDK parser and the real handler
   */
  private final class Handler extends HTMLEditorKit.ParserCallback {

    /**
     * the attributes
     */
    private final AttributesImpl attrs = new AttributesImpl();

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.html.HTMLEditorKit.ParserCallback#handleEndTag(javax
     * .swing.text.html.HTML.Tag, int)
     */
    @Override
    public void handleEndTag(final Tag t, final int pos) {
      try {
        // name
        final String localName = t.toString();
        // send
        endElement(NULL_NS_URI, localName, localName);
      } catch (final SAXException ex) {
        // remember the exception
        exception = ex;
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.html.HTMLEditorKit.ParserCallback#handleSimpleTag(javax
     * .swing.text.html.HTML.Tag, javax.swing.text.MutableAttributeSet, int)
     */
    @Override
    public void handleSimpleTag(final Tag t, final MutableAttributeSet a,
        final int pos) {
      try {
        // initialize
        setAttributes(a);
        // name
        final String localName = t.toString();
        // send
        startElement(NULL_NS_URI, localName, localName, attrs);
        endElement(NULL_NS_URI, localName, localName);
      } catch (final SAXException ex) {
        // remember the exception
        exception = ex;
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.html.HTMLEditorKit.ParserCallback#handleStartTag(javax
     * .swing.text.html.HTML.Tag, javax.swing.text.MutableAttributeSet, int)
     */
    @Override
    public void handleStartTag(final Tag t, final MutableAttributeSet a,
        final int pos) {
      try {
        // initialize
        setAttributes(a);
        // name
        final String localName = t.toString();
        // send
        startElement(NULL_NS_URI, localName, localName, attrs);
      } catch (final SAXException ex) {
        // remember the exception
        exception = ex;
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.html.HTMLEditorKit.ParserCallback#handleText(char[],
     * int)
     */
    @Override
    public void handleText(final char[] data, final int pos) {
      try {
        // delegate
        characters(data, 0, data.length);
      } catch (final SAXException ex) {
        // remember the exception
        exception = ex;
      }
    }

    /**
     * Produces JAXP compatible attributes
     * 
     * @param src
     *          the source
     */
    private final void setAttributes(final MutableAttributeSet src) {
      // clear the old attributes
      attrs.clear();
      // copy over
      final Enumeration<?> e = src.getAttributeNames();
      while (e.hasMoreElements()) {
        // the attribute
        final Object key = e.nextElement();
        if (key != IMPLIED) {
          // convert to the local name
          final String localName = key.toString();
          // set
          attrs.addAttribute(NULL_NS_URI, localName, localName, "CDATA", src
              .getAttribute(key).toString());
        }

      }
    }
  }

  /**
   * the JDK parser
   */
  private final ParserDelegator parser = new ParserDelegator();

  /**
   * our wrapper
   */
  private final Handler handler = new Handler();

  /**
   * carry the exception over
   */
  private SAXException exception;

  /** class name for the logger */
  private static final String LOG_CLASS = HtmlParser.class.getName();

  /** logging level */
  private static final Level LOG_LEVEL = Level.FINER;

  /** class logger */
  private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

  /**
   * logging support
   */
  private boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

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
          qName });
    }
    // delegate
    super.endElement(uri, localName, qName);
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
  public void parse(final InputSource source) throws SAXException, IOException {
    // logging support
    final String LOG_METHOD = "parse(source)";
    bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
    if (bIsLogging) {
      LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { source });
    }
    // reset
    exception = null;
    // parse
    startDocument();
    parser.parse(source.getCharacterStream(), handler, true);
    endDocument();
    // bail out
    if (exception != null) {
      throw exception;
    }
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
          qName, atts });
    }
    // delegate
    super.startElement(uri, localName, qName, atts);
    // exit trace
    if (bIsLogging) {
      LOGGER.exiting(LOG_CLASS, LOG_METHOD);
    }
  }
}
