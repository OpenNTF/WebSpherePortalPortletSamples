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
package com.ibm.portal.samples.mail.tag;

import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

import com.ibm.portal.resolver.service.CorPocServiceHome;
import com.ibm.portal.resolver.xml.DisposableTransformer;
import com.ibm.portal.resolver.xml.PooledTemplates;

/**
 * Quick tag that executes an identity transform and serializes the source onto
 * the target stream. Strange that this does not exist in JSTL ...
 * 
 * @author cleue
 * 
 */
public class TransformerTag extends TagSupport {

	/**
	 * serialization support
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the XML source
	 */
	private Source source;

	/**
	 * access the templates
	 */
	private static final PooledTemplates POOLED_TEMPLATES = getPooledTemplates();

	/**
	 * Statically lookup the templates, avoid to do this for every call
	 * 
	 * @return the templates
	 */
	private static final PooledTemplates getPooledTemplates() {
		try {
			final InitialContext context = new InitialContext();
			final CorPocServiceHome pocHome = (CorPocServiceHome) context
					.lookup(CorPocServiceHome.JNDI_NAME);
			return pocHome.getIdentityTemplates();
		} catch (final NamingException ex) {
			// just bail out
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// transform
			final DisposableTransformer trfrm = POOLED_TEMPLATES
					.newTransformer();
			try {
				// execute the transform
				trfrm.setOutputProperty(OMIT_XML_DECLARATION, "yes");
				trfrm.transform(source, new StreamResult(pageContext.getOut()));
			} finally {
				// release
				trfrm.dispose();
			}
		} catch (final Exception ex) {
			// bail out
			throw new JspException(ex);
		}
		// default
		return super.doStartTag();
	}

	/**
	 * Assign the source
	 * 
	 * @param aSource
	 *            the source object
	 */
	public void setSource(final Source aSource) {
		source = aSource;
	}

}
