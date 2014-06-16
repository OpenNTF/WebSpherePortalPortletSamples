#
#

AdminTask.retrieveSignerFromPort('[-host @com.ibm.portal.samples-MailPortlets.host@ -port @com.ibm.portal.samples-MailPortlets.port.ssl@ -keyStoreName NodeDefaultTrustStore -certificateAlias com.ibm.portal.samples-MailPortlets]')

AdminConfig.save()

