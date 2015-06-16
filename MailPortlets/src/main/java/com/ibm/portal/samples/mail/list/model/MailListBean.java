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
package com.ibm.portal.samples.mail.list.model;

import static com.ibm.portal.portlet.service.credentialvault.CredentialVaultService.SECRET_TYPE_USERID_STRING_PASSWORD_STRING;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletRequest;

import com.ibm.portal.ObjectID;
import com.ibm.portal.portlet.service.credentialvault.CredentialSlotConfig;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultException;
import com.ibm.portal.portlet.service.credentialvault.CredentialVaultService;
import com.ibm.portal.samples.mail.common.AbstractBean;

/**
 * Helper bean to access mail aspects specific to the list portlet
 * 
 * @author cleue
 */
public class MailListBean extends AbstractBean {

	public interface Dependencies extends AbstractBean.Dependencies {

		@Override
		CredentialVaultService getCredentialVaultService();

	}

	/** class name for the logger */
	private static final String LOG_CLASS = MailListBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * logging support
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * access to the credential vault
	 */
	private final CredentialVaultService credentialVaultService;

	/**
	 * the request
	 */
	private final PortletRequest request;

	public MailListBean(final PortletRequest aRequest, final Dependencies aDeps) {
		super(aRequest, aDeps);
		// the service
		credentialVaultService = aDeps.getCredentialVaultService();
		request = aRequest;
	}

	/**
	 * Create a new mail slot
	 * 
	 * @return the new slot
	 * 
	 * @throws CredentialVaultException
	 */
	private final CredentialSlotConfig createMailSlot()
			throws CredentialVaultException {
		// logging support
		final String LOG_METHOD = "createMailSlot()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the objectID of the default user-managed segment, where the new slot
		// is to be created
		final ObjectID userSegmentID = credentialVaultService
				.getDefaultUserCredentialSegmentId();
		// an appropriate description to find the slot
		final Map<Locale, String> descriptions = new HashMap<Locale, String>();
		descriptions.put(DEFAULT_LOCALE, CREDENTIAL_SLOT_DESCRIPTION);
		// no keywords needed
		final Map<Locale, String> keywords = null;
		// the secret type is userID / password
		final int secretType = SECRET_TYPE_USERID_STRING_PASSWORD_STRING;
		// use passive credentials to be able to access the username
		final boolean active = false;
		// the slot must NOT be portlet private because the mail portlet must
		// have access
		final boolean portletPrivate = false;
		// use vault service to create the slot with the specified values
		final CredentialSlotConfig result = credentialVaultService
				.createCredentialSlot("mail password slot", userSegmentID,
						descriptions, keywords, secretType, active,
						portletPrivate, request);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// ok
		return result;
	}

	/**
	 * Constructs a new credential slot
	 * 
	 * @param aUserMail
	 * @param aPassword
	 * 
	 * @throws CredentialVaultException
	 */
	public boolean storeCredentials(final String aUserMail,
			final String aPassword) throws CredentialVaultException {
		// logging support
		final String LOG_METHOD = "storeCredentials(aUserMail, aPassword)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aUserMail });
		}
		// the result
		final boolean bResult;
		// sanity check
		if (isValid(aUserMail) && isValid(aPassword)) {
			// check if we can find the slot
			CredentialSlotConfig slot = getCredentialSlot();
			if (slot == null) {
				// create a new slot
				slot = createMailSlot();
				assert slot != null;
			}
			// use vault service to store the userID / password secret in the
			// slot
			credentialVaultService.setCredentialSecretUserPassword(
					slot.getSlotId(), aUserMail, aPassword.toCharArray(),
					request);
			// we persisted the data
			bResult = true;
		} else {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Invalid username or password.");
			}
			// nothing changed
			bResult = false;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}
}
