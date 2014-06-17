<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- the sample is based on the IBM OneUI styles that ship as a theme module in WebSphere Portal. This only
  matters for styling, functionally there is no dependency on the CSS classes --%>
<div class="lotusui30 mailPortlet" lang="${view.locale}">

	<%-- form to enter username and password --%>
	<form class="lotusForm2" enctype="multipart/form-data" method="POST"
		action="${fn:escapeXml(controller.actionURL)}" aria-live="assertive">

		<%-- the browser fills in this field with the correct encoding --%>
		<input type="hidden" name="_charset_"
			value="${pageContext.response.characterEncoding}" />

		<%-- form header (use a div instead of a header tag if you aren't using html5) --%>
		<header class="lotusFormTitle">
			<h1 class="lotusHeading" aria-describedby="${view.namespace}_title">
				<c:out value="${view.authenticationTitle}" />
			</h1>
			<div id="${view.namespace}_title" class="lotusMeta">
				<c:out value="${view.authenticationDescription}" />
			</div>
		</header>

		<%-- form body --%>
		<div class="lotusFormBody">

			<div class="lotusFormField">
				<label for="${view.namespace}_username"><span
					class="lotusFormRequired"
					title="${fn:escapeXml(view.requiredField)}">*</span> <c:out
						value="${view.username}" /></label>
				<div class="lotusFieldWrapper">
					<input class="lotusText" type="text"
						id="${view.namespace}_username" name="${controller.keyUsername}"
						value="" aria-required="true" />
				</div>
			</div>

			<div class="lotusFormField">
				<label for="${view.namespace}_password"><span
					class="lotusFormRequired"
					title="${fn:escapeXml(view.requiredField)}">*</span> <c:out
						value="${view.password}" /></label>
				<div class="lotusFieldWrapper">
					<input class="lotusText" type="password"
						id="${view.namespace}_password" name="${controller.keyPassword}"
						value="" aria-required="true" />
				</div>
			</div>

		</div>
		<%--end form body--%>

		<%-- form footer --%>
		<div class="lotusFormFooter">
			<button id="${view.namespace}_enterCredentials"
				name="${controller.keyAction}" type="submit"
				value="${controller.valueActionEnterCredentials}" class="lotusBtn">
				<c:out value="${view.saveButton}" />
			</button>
		</div>
	</form>

</div>