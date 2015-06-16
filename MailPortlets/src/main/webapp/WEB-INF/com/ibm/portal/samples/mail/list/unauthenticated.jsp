<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- the sample is based on the IBM OneUI styles that ship as a theme module in WebSphere Portal. This only
  matters for styling, functionally there is no dependency on the CSS classes --%>
<div class="lotusui30 mailPortlet" lang="${view.locale}">
	<div class="lotusMessage2 lotusWarning" role="alert">
		<img class="lotusIcon lotusIconMsgWarning"
			src="${fn:escapeXml(view.blankImageURL)}" alt="Warning" /><span
			class="lotusAltText">Warning:</span>
		<div class="lotusMessageBody">
			<c:out value="${view.unauthenticatedMessage}" />
		</div>
	</div>
</div>