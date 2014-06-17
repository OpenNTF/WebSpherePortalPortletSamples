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
package com.ibm.portal.samples.common;

import java.io.IOException;

import javax.portlet.BaseURL;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.StateAwareResponse;

/**
 * Some base class for all of our models
 * 
 * @author cleue
 */
public abstract class AbstractModel implements Cloneable {

	public interface Dependencies {

		/**
		 * Marshaller for private render parameters
		 * 
		 * @return the marshaller
		 */
		Marshaller getPrivateParameterMarshaller();

		/**
		 * Marshaller for public render parameters
		 * 
		 * @return the marshaller
		 */
		Marshaller getPublicParameterMarshaller();

	}

	private static final boolean equals(final Object aLeft, final Object aRight) {
		return (aLeft == aRight) || ((aLeft != null) && aLeft.equals(aRight));
	}

	/**
	 * checks if there exists error information
	 */
	private Boolean bHasError;

	/**
	 * error information
	 */
	private ErrorBean errorBean;

	/**
	 * controls how private parameters are marshalled
	 */
	private final Marshaller privateMarshaller;

	/**
	 * controls how public parameters are marshalled
	 */
	private final Marshaller publicMarshaller;

	/**
	 * the request
	 */
	private final PortletRequest request;

	/**
	 * Copy constructor. We prefer this copy constructor model over the implicit
	 * java cloning facility. It's just more stable.
	 * 
	 * @param aCopy
	 *            the constructor
	 */
	protected AbstractModel(final AbstractModel aCopy) {
		request = aCopy.request;
		privateMarshaller = aCopy.privateMarshaller;
		publicMarshaller = aCopy.publicMarshaller;
	}

	/**
	 * Decoding constructor
	 * 
	 * @param aRequest
	 *            the request
	 */
	protected AbstractModel(final PortletRequest aRequest,
			final Dependencies aDeps) {
		request = aRequest;
		privateMarshaller = aDeps.getPrivateParameterMarshaller();
		publicMarshaller = aDeps.getPublicParameterMarshaller();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected abstract AbstractModel clone();

	protected boolean decode(final Enum<?> aKey, final boolean aDefaultValue) {
		return privateMarshaller.unmarshalBoolean(
				request.getParameter(privateMarshaller.marshalEnum(aKey)),
				aDefaultValue);
	}

	protected <E extends Enum<E>> E decode(final Enum<?> aKey,
			final E[] aValues, final E aDefaultValue) {
		return privateMarshaller.unmarshalEnum(
				request.getParameter(privateMarshaller.marshalEnum(aKey)),
				aValues, aDefaultValue);
	}

	protected int decode(final Enum<?> aKey, final int aDefaultValue) {
		return privateMarshaller.unmarshalInt(
				request.getParameter(privateMarshaller.marshalEnum(aKey)),
				aDefaultValue);
	}

	protected long decode(final Enum<?> aKey, final long aDefaultValue) {
		return privateMarshaller.unmarshalLong(
				request.getParameter(privateMarshaller.marshalEnum(aKey)),
				aDefaultValue);
	}

	protected String decode(final Enum<?> aKey, final String aDefaultValue) {
		return privateMarshaller.unmarshalString(
				request.getParameter(privateMarshaller.marshalEnum(aKey)),
				aDefaultValue);
	}

	protected boolean decode(final String aKey, final boolean aDefaultValue) {
		return publicMarshaller.unmarshalBoolean(request.getParameter(aKey),
				aDefaultValue);
	}

	protected int decode(final String aKey, final int aDefaultValue) {
		return publicMarshaller.unmarshalInt(request.getParameter(aKey),
				aDefaultValue);
	}

	protected long decode(final String aKey, final long aDefaultValue) {
		return publicMarshaller.unmarshalLong(request.getParameter(aKey),
				aDefaultValue);
	}

	protected String decode(final String aKey, final String aDefaultValue) {
		return publicMarshaller.unmarshalString(request.getParameter(aKey),
				aDefaultValue);
	}

	protected void encode(final BaseURL aURL) throws PortletException,
			IOException {
	}

	protected void encode(final BaseURL aState, final Enum<?> aKey,
			final boolean aValue, final boolean aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalBoolean(aValue));
		}
	}

	protected <E extends Enum<E>> void encode(final BaseURL aState,
			final Enum<?> aKey, final E aValue, final E aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalEnum(aValue));
		}
	}

	protected void encode(final BaseURL aState, final Enum<?> aKey,
			final int aValue, final int aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalInt(aValue));
		}
	}

	protected void encode(final BaseURL aState, final Enum<?> aKey,
			final long aValue, final long aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalLong(aValue));
		}
	}

	protected void encode(final BaseURL aState, final Enum<?> aKey,
			final String aValue, final String aDefaultValue) {
		// only encode if required
		if (!equals(aValue, aDefaultValue)) {
			// encode the text
			aState.setParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalString(aValue));
		}
	}

	protected void encode(final BaseURL aState, String aKey,
			final boolean aValue) {
		// encode the text
		aState.setParameter(aKey, publicMarshaller.marshalBoolean(aValue));
	}

	protected void encode(final BaseURL aState, String aKey, final int aValue) {
		// encode the text
		aState.setParameter(aKey, publicMarshaller.marshalInt(aValue));
	}

	protected void encode(final BaseURL aState, String aKey, final long aValue) {
		// encode the text
		aState.setParameter(aKey, publicMarshaller.marshalLong(aValue));
	}

	protected void encode(final BaseURL aState, String aKey, final String aValue) {
		// encode the text
		aState.setParameter(aKey, publicMarshaller.marshalString(aValue));
	}

	public void encode(final PortletURL aURL) throws PortletException,
			IOException {
		encode((BaseURL) aURL);
	}

	public void encode(final StateAwareResponse aResponse)
			throws PortletException, IOException {

	}

	protected void encode(final StateAwareResponse aState, final Enum<?> aKey,
			final boolean aValue, final boolean aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setRenderParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalBoolean(aValue));
		}
	}

	protected <E extends Enum<E>> void encode(final StateAwareResponse aState,
			final Enum<?> aKey, final E aValue, final E aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setRenderParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalEnum(aValue));
		}
	}

	protected void encode(final StateAwareResponse aState, final Enum<?> aKey,
			final int aValue, final int aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setRenderParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalInt(aValue));
		}
	}

	protected void encode(final StateAwareResponse aState, final Enum<?> aKey,
			final long aValue, final long aDefaultValue) {
		// only encode if required
		if (aDefaultValue != aValue) {
			// encode the text
			aState.setRenderParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalLong(aValue));
		}
	}

	protected void encode(final StateAwareResponse aState, final Enum<?> aKey,
			final String aValue, final String aDefaultValue) {
		// only encode if required
		if (!equals(aValue, aDefaultValue)) {
			// encode the text
			aState.setRenderParameter(privateMarshaller.marshalEnum(aKey),
					privateMarshaller.marshalString(aValue));
		}
	}

	protected void encode(final StateAwareResponse aState, String aKey,
			final boolean aValue) {
		// encode the text
		aState.setRenderParameter(aKey, publicMarshaller.marshalBoolean(aValue));
	}

	protected void encode(final StateAwareResponse aState, String aKey,
			final int aValue) {
		// encode the text
		aState.setRenderParameter(aKey, publicMarshaller.marshalInt(aValue));
	}

	protected void encode(final StateAwareResponse aState, String aKey,
			final long aValue) {
		// encode the text
		aState.setRenderParameter(aKey, publicMarshaller.marshalLong(aValue));
	}

	protected void encode(final StateAwareResponse aState, String aKey,
			final String aValue) {
		// encode the text
		aState.setRenderParameter(aKey, publicMarshaller.marshalString(aValue));
	}

	/**
	 * Returns the error bean or <code>null</code>
	 * 
	 * @return the error bean
	 */
	public ErrorBean getErrorBean() {
		// lazily decode the bean
		if (bHasError == null) {
			errorBean = ErrorBean.getErrorBean(request);
			bHasError = Boolean.valueOf(errorBean != null);
		}
		// returns the bean
		return errorBean;
	}

	/**
	 * Tests if we have error information
	 * 
	 * @return <code>true</code> if error information is available, else
	 *         <code>false</code>
	 */
	public boolean isError() {
		// checks for the existence of an error
		return getErrorBean() != null;
	}

	protected void logException(Throwable ex) {
		ex.printStackTrace();
	}
}
