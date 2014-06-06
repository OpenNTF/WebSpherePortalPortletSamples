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
 * Marshaller for public render parameters. In contrast to private render
 * parameters, public parameters are meant as an interface to other components,
 * so the marshalling of parameters needs to follow an agreed upon contract.
 * This marshaller implements as the natural contract for integer marshalling
 * the base 10 conversion.
 * 
 * @author cleue
 * 
 */
public class PublicParameterMarshaller extends AbstractMarshaller {

	/**
	 * singleton access
	 */
	public static final Marshaller SINGLETON = new PublicParameterMarshaller();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.samples.common.Marshaller#marshalInt(int)
	 */
	@Override
	public String marshalInt(final int aRaw) {
		return Integer.toString(aRaw);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.portal.samples.common.Marshaller#unmarshalInt(java.lang.String,
	 * int)
	 */
	@Override
	public int unmarshalInt(final String aToken, final int aDefault) {
		try {
			return (aToken != null) ? Integer.parseInt(aToken) : aDefault;
		} catch (final Throwable th) {
			return aDefault;
		}
	}

}
