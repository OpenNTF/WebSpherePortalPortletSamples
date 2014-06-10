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

import static java.lang.Character.MAX_RADIX;

/**
 * Marshaller for private render parameters. Private parameters are NOT meant to
 * be an interface to outside components, so the marshalling of these parameters
 * can and should be an implementation detail of the portlet. It is advisable to
 * use parameter representations as compact as possible instead of using
 * readable names.
 * 
 * In this example we are simply using a larger numerical base for integer to
 * string conversion to illustrate this aspect. In a production environment
 * you'd use an even more compact encoding using a broader alphabet (e.g.
 * base85) and caching layers to avoid the string creation.
 * 
 * @author cleue
 * 
 */
public class PrivateParameterMarshaller extends AbstractMarshaller {

	/**
	 * Radix used for int conversion
	 */
	private static final int RADIX = MAX_RADIX;

	/**
	 * singleton access
	 */
	public static final Marshaller SINGLETON = new PrivateParameterMarshaller();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.samples.common.Marshaller#marshalInt(int)
	 */
	@Override
	public String marshalInt(final int aRaw) {
		/**
		 * we use the maximum radix to generate a small representation. In a
		 * production environment we'd use an even more compact encoding on the
		 * basis of a larger alphabet, including a caching layer.
		 */
		return Integer.toString(aRaw, RADIX);
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
			return (aToken != null) ? Integer.parseInt(aToken, RADIX)
					: aDefault;
		} catch (final Throwable th) {
			return aDefault;
		}
	}

}
