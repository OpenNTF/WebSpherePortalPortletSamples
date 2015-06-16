<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- the sample is based on the IBM OneUI styles that ship as a theme module in WebSphere Portal. This only
matters for styling, functionally there is no dependency on the CSS classes --%>
<div class="lotusui30 mailPortlet" lang="${view.locale}">

	<%-- make sure to use multipart/form-data encoding to get the characterset right --%>
	<form class="lotusForm2" enctype="multipart/form-data" method="POST"
		action="${fn:escapeXml(controller.actionURL)}" aria-live="assertive">

		<%-- the browser fills in this field with the correct encoding --%>
		<input type="hidden" name="_charset_"
			value="${pageContext.response.characterEncoding}" />

		<!-- form header (use a div instead of a header tag if you aren't using html5) -->
		<header class="lotusFormTitle">
			<h1 class="lotusHeading" aria-describedby="${view.namespace}_title">Compose</h1>
			<div id="${view.namespace}_title" class="lotusMeta">Compose a
				new message</div>
		</header>

		<!-- form body -->
		<div class="lotusFormBody">

			<div class="lotusFormField">
				<label for="${view.namespace}_address">Address:</label>
				<div class="lotusFieldWrapper">
					<input class="lotusText" type="text" id="${view.namespace}_address"
						name="${controller.keyReceiver}"
						value="${fn:escapeXml(model.address)}" aria-required="true" />
				</div>
			</div>

			<div class="lotusFormField">
				<label for="${view.namespace}_subject">Subject:</label>
				<div class="lotusFieldWrapper">
					<input class="lotusText" type="text" id="${view.namespace}_subject"
						name="${controller.keySubject}"
						value="${fn:escapeXml(model.subject)}" aria-required="true" />
				</div>
			</div>

			<div class="lotusFormField">
				<label for="${view.namespace}_body">Text:</label>
				<div class="lotusFieldWrapper">
					<textarea class="lotusText" id="${view.namespace}_body"
						name="${controller.keyBody}" rows="5" cols="20"></textarea>
				</div>
			</div>

		</div>
		<!--end form body-->

		<!-- form footer -->
		<div class="lotusFormFooter">
			<button id="${view.namespace}_sendMail"
				name="${controller.keyAction}" type="submit"
				value="${controller.valueActionSendMail}" class="lotusBtn">
				<c:out value="${view.sendMail}" />
			</button>
		</div>
	</form>

</div>
