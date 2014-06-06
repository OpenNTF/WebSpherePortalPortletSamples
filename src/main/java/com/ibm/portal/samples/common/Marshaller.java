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
 * Abstraction of the encoding aspect of portlet render parameters and action
 * parameters. It is meant to emphasize that such parameters are really just
 * meant to be a serialization of internal model data.
 * 
 * @author cleue
 */
public interface Marshaller {

	/**
	 * Encodes an enumeration as a transfer string.
	 * 
	 * @param aRaw
	 *            the enumeration
	 * @return the string representation
	 * 
	 * @see #unmarshalEnum(String, Enum[], Enum)
	 */
	String marshalEnum(final Enum<?> aRaw);

	/**
	 * Converts an integer into a transfer string representation.
	 * 
	 * @param aRaw
	 *            the integer value
	 * @return the transfer string representation
	 * 
	 * @see #unmarshalInt(String, int)
	 */
	String marshalInt(final int aRaw);

	/**
	 * Converts a string into a transfer string representation.
	 * 
	 * @param aRaw
	 *            the string value
	 * @return the transfer string representation
	 * 
	 * @see #unmarshalString(String, String)
	 */
	String marshalString(final String aRaw);

	/**
	 * Decodes from the string representation of an enumeration to the
	 * enumeration constant.
	 * 
	 * @param aToken
	 *            transfer string to decode
	 * @param aEnums
	 *            array of possible enumerations
	 * @param aDefault
	 *            default value, used in case of an error or a missing value
	 * 
	 * @return the decoded enumeration
	 * 
	 * @see #marshalEnum(Enum)
	 */
	<E extends Enum<E>> E unmarshalEnum(final String aToken, final E[] aEnums,
			final E aDefault);

	/**
	 * Decodes a transfer string representation into an integer
	 * 
	 * @param aToken
	 *            transfer string to decode
	 * @param aDefault
	 *            default value, used in case of an error or a missing value
	 * @return the decoded integer
	 * 
	 * @see #marshalInt(int)
	 */
	int unmarshalInt(String aToken, int aDefault);

	/**
	 * Decodes a transfer string representation into a string value
	 * 
	 * @param aToken
	 *            transfer string to decode
	 * @param aDefault
	 *            default value, used in case of an error or a missing value
	 * @return the decoded string
	 * 
	 * @see #marshalInt(int)
	 */
	String unmarshalString(String aToken, String aDefault);
}
