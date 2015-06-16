<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="m"
	uri="http://www.ibm.com/xmlns/prod/websphere/portal/sample/portlet/mail"%>

<%-- the sample is based on the IBM OneUI styles that ship as a theme module in WebSphere Portal. This only
matters for styling, functionally there is no dependency on the CSS classes --%>
<div class="lotusui30 mailPortlet" lang="${pageContext.response.locale}">

	<div class="lotusForum">

		<%-- make sure to use multipart/form-data encoding to get the characterset right --%>
		<form id="${view.namespace}_form" class="lotusForm2"
			enctype="multipart/form-data" method="POST"
			action="${fn:escapeXml(controller.actionURL)}" aria-live="assertive">

			<%-- the browser fills in this field with the correct encoding --%>
			<input type="hidden" name="_charset_"
				value="${pageContext.response.characterEncoding}" />

			<c:set var="item" value="${model.selectedMessage}" />
			<c:if test="${not empty item}">

				<!-- article is an HTML5 element. Use div instead if you are using HTML4 -->
				<article class="lotusPost" role="article">
					<div class="lotusPostAuthorInfo">
						<div class="lotusPostAvatar">
							<!-- replace the blank.gif with the URL to the server for person pictures -->
							<img alt="${fn:escapeXml(item.senderMail)}"
								title="${fn:escapeXml(item.senderMail)}"
								src="${fn:escapeXml(view.personImageURL[item])}" width="64px"
								height="64px" />
						</div>
						<div class="lotusPostName">
							<a class="lotusPerson" href="javascript:;"><c:out
									value="${item.sender}" /></a>
						</div>
					</div>
					<!--end author info-->
					<div class="lotusPostContent">
						<h4 class="lotusTitle">
							<c:out value="${item.subject}" />
						</h4>
						<div class="lotusMeta">${view.date[item.lastModified]}</div>
						<div class="lotusPostDetails">
							<p>
								<m:transform source="${view.rewrite[item]}" />
							</p>
						</div>
						<div class="lotusActions">
							<ul class="lotusInlinelist">
								<li class="lotusFirst"><a href="#"
									onclick="document.getElementById('${view.namespace}_form').submit(); return false;"
									role="button"><c:out value="${view.mailReply}" /></a></li>
							</ul>
						</div>
						<!--end actions-->
					</div>
					<!--end commentContent-->
				</article>
				<!--end commment-->

			</c:if>

			<%-- hidden field that identifies the action --%>
			<input type="hidden" name="${controller.keyAction}"
				value="${controller.valueActionReply}" />
		</form>

	</div>
</div>
