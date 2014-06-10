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

/**
 * Convenience base class for marshallers.
 * 
 * @author cleue
 * 
 */
public abstract class AbstractMarshaller implements Marshaller {

	/**
	 * Do not instantiate this directly
	 */
	protected AbstractMarshaller() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.samples.common.Marshaller#marshalEnum(java.lang.Enum)
	 */
	@Override
	public String marshalEnum(final Enum<?> aRaw) {
		// sanity check
		assert aRaw != null;
		// consider the enum a private parameter
		return marshalInt(aRaw.ordinal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.portal.samples.common.Marshaller#marshalString(java.lang.String)
	 */
	@Override
	public String marshalString(final String aRaw) {
		return aRaw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.portal.samples.common.Marshaller#unmarshalEnum(java.lang.String,
	 * java.lang.Enum[], java.lang.Enum)
	 */
	@Override
	public <E extends Enum<E>> E unmarshalEnum(final String aToken,
			final E[] aEnums, final E aDefault) {
		// sanity check
		assert aDefault != null;
		assert aEnums != null;
		// decode the ordinal
		final int idx = unmarshalInt(aToken, aDefault.ordinal());
		// map the ordinal or fallback
		return ((idx >= 0) && (idx < aEnums.length)) ? aEnums[idx] : aDefault;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.portal.samples.common.Marshaller#unmarshalString(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public String unmarshalString(final String aToken, final String aDefault) {
		return (aToken != null) ? aToken : aDefault;
	}
}
