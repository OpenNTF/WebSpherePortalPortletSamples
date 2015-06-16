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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ibm.portal.TimeStamped;
import com.ibm.portal.samples.mail.helper.HtmlParser;

/**
 * Wrapper around an IMAP email message that exposes the desired properties in a
 * bean compatible fashion.
 * 
 * @author cleue
 * 
 */
public class MessageBean implements TimeStamped {

  private final Message message;

  private final long id;

  private Address[] senders;

  private Date lastModified;

  private Date created;

  private String subject;

  /**
   * the message as an html document
   */
  private Document document;

  /**
   * source representation of the document
   */
  private Source source;

  /**
   * the main message part
   */
  private Part messagePart;

  /**
   * the sender information
   */
  private String sender;

  /**
   * email of the sender
   */
  private String senderEMail;

  public MessageBean(final long aID, final Message aMessage) {
    message = aMessage;
    id = aID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.portal.TimeStamped#getCreated()
   */
  @Override
  public Date getCreated() {
    if (created == null) {
      try {
        created = message.getSentDate();
      } catch (final MessagingException ex) {
        created = new Date();
      }
    }
    return created;
  }

  /**
   * Constructs the document
   * 
   * @param aReader
   *          the reader
   * @return the parsed document
   * 
   * @throws TransformerException
   * @throws ParserConfigurationException
   */
  private final Document getDocument(final Reader aReader)
      throws TransformerException, ParserConfigurationException {

    final DocumentBuilderFactory builderFct = DocumentBuilderFactory
        .newInstance();
    builderFct.setNamespaceAware(true);

    final DocumentBuilder builder = builderFct.newDocumentBuilder();
    final Document doc = builder.newDocument();

    TransformerFactory
        .newInstance()
        .newTransformer()
        .transform(new SAXSource(new HtmlParser(), new InputSource(aReader)),
            new DOMResult(doc));

    return doc;
  }

  /**
   * Returns a unique identifier for the message. This value is derived from
   * {@link UIDFolder}.
   * 
   * @return the unique ID of the message
   */
  public long getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.portal.TimeStamped#getLastModified()
   */
  @Override
  public Date getLastModified() {
    // lazily compute the date
    if (lastModified == null) {
      try {
        lastModified = message.getReceivedDate();
      } catch (final MessagingException ex) {
        lastModified = new Date();
      }

    }
    return lastModified;
  }

  /**
   * Locates the "main" part of the message
   * 
   * @return the main part
   * 
   * @throws MessagingException
   * @throws IOException
   */
  private final Part getMessagePart() throws MessagingException, IOException {
    // lazily locale the main message part
    if (messagePart == null) {
      messagePart = getMessagePart(message);
    }
    // the message part
    return messagePart;
  }

  /**
   * Return the primary text content of the message.
   */
  private final Part getMessagePart(final Part p) throws MessagingException,
      IOException {

    if (p.isMimeType("text/*")) {
      return p;
    }

    if (p.isMimeType("multipart/alternative")) {
      // prefer html text over plain text
      final Multipart mp = (Multipart) p.getContent();
      Part text = null;
      for (int i = 0; i < mp.getCount(); i++) {
        final Part bp = mp.getBodyPart(i);
        if (bp.isMimeType("text/plain")) {
          if (text == null) {
            text = getMessagePart(bp);
          }
          continue;
        } else if (bp.isMimeType("text/html")) {
          final Part s = getMessagePart(bp);
          if (s != null) {
            return s;
          }
        } else {
          return getMessagePart(bp);
        }
      }
      return text;
    } else if (p.isMimeType("multipart/*")) {
      final Multipart mp = (Multipart) p.getContent();
      for (int i = 0; i < mp.getCount(); i++) {
        final Part s = getMessagePart(mp.getBodyPart(i));
        if (s != null) {
          return s;
        }
      }
    }

    return null;
  }

  /**
   * Decodes the message as a document
   * 
   * @return the message document
   * 
   * @throws TransformerException
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws MessagingException
   */
  public Source getMessageSource() throws TransformerException,
      ParserConfigurationException, IOException, MessagingException {
    if (document == null) {
      // decode the part
      final Part messagePart = getMessagePart();
      if (messagePart != null) {
        // parse
        document = getDocument(new StringReader(messagePart.getContent()
            .toString()));
        // construct the source
        if (document != null) {
          source = new DOMSource(document);
        }
      }
    }
    // returns the source
    return source;
  }

  /**
   * Locates the message part by a cid:XXX identifier (which is used internally
   * to reference embedded images).
   * 
   * @param aContentURI
   *          the content URI
   * @return the message part
   * 
   * @throws MessagingException
   * @throws IOException
   * @throws URISyntaxException
   */
  public Part getPartByID(final String aContentURI) throws MessagingException,
      IOException, URISyntaxException {
    // decode the URI
    final URI uri = new URI(aContentURI);
    // decode just the ssp
    final String cid = "<" + uri.getRawSchemeSpecificPart() + ">";
    // locate
    return getPartByID(cid, message);
  }

  private Part getPartByID(final String aID, final Part aPart)
      throws MessagingException, IOException {
    // try to get the ID
    final String[] id = aPart.getHeader("Content-ID");
    if ((id != null) && (id.length > 0) && aID.equals(id[0])) {
      return aPart;
    } else if (aPart.isMimeType("multipart/*")) {
      // prefer html text over plain text
      final Multipart mp = (Multipart) aPart.getContent();
      for (int i = 0; i < mp.getCount(); i++) {
        final Part p = getPartByID(aID, mp.getBodyPart(i));
        if (p != null) {
          return p;
        }
      }
    }

    return null;
  }

  /**
   * Returns the display title of the sender
   * 
   * @return the display title, typically the name
   * 
   * @throws MessagingException
   */
  public String getSender() throws MessagingException {
    if (sender == null) {
      final Address[] senders = getSenders();
      final Address addr = senders[0];
      if (addr instanceof InternetAddress) {
        // decode this
        final InternetAddress iaddr = (InternetAddress) addr;
        sender = iaddr.getPersonal();
      } else {
        // just use the default decoding
        sender = addr.toString();
      }
    }
    return sender;
  }

  /**
   * Tries to get the email of the sender
   * 
   * @return mail of the sender
   * @throws MessagingException
   */
  public String getSenderMail() throws MessagingException {
    if (senderEMail == null) {
      final Address[] senders = getSenders();
      final Address addr = senders[0];
      if (addr instanceof InternetAddress) {
        // decode this
        final InternetAddress iaddr = (InternetAddress) addr;
        senderEMail = iaddr.getAddress();
      } else {
        // just use the default decoding
        senderEMail = addr.toString();
      }
    }
    // the email
    return senderEMail;
  }

  private Address[] getSenders() throws MessagingException {
    if (senders == null) {
      senders = message.getFrom();
    }
    return senders;
  }

  /**
   * Returns the subject of the message in plain text
   * 
   * @return the subject in plain text
   */
  public String getSubject() {
    // lazily access the subject
    if (subject == null) {
      try {
        subject = message.getSubject();
      } catch (final MessagingException ex) {
        subject = "";
      }
    }
    return subject;
  }
}
