package com.ibm.portal.samples.common;

import javax.portlet.BaseURL;

/**
 * Some base class for all of our models
 * 
 * @author cleue
 */
public class AbstractController {

	/**
	 * Representation to dependencies on external services
	 */
	public interface Dependencies {
		/**
		 * Marshaller for private render parameters
		 * 
		 * @return the marshaller
		 */
		Marshaller getPrivateParameterMarshaller();

		/**
		 * TODO add dependencies via parameterless getter methods
		 */
	}

	/**
	 * controls how private parameters are marshalled
	 */
	private final Marshaller privateMarshaller;

	/**
	 * Initialize the controller
	 * 
	 * @param aModel
	 *            the base model
	 * @param aResponse
	 *            the portlet response
	 * @param aDeps
	 *            the dependencies
	 */
	protected AbstractController(final Dependencies aDeps) {
		privateMarshaller = aDeps.getPrivateParameterMarshaller();
	}

	protected String getEncoded(final Enum<?> aKey) {
		return privateMarshaller.marshalEnum(aKey);
	}
}
