<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="lotusui30" lang="${view.locale}">

	<!-- always render a form using multipart/form-data for best compatibility -->
	<form id="${view.namespace}_form" class="lotusForm2" method="POST"
		enctype="multipart/form-data"
		action="${fn:escapeXml(controller.actionURL)}" aria-live="assertive">

		<!-- character encoding support -->
		<input type="hidden" name="_charset_"
			value="${view.characterEncoding}" />

		<!-- form header (use a div instead of a header tag if you aren't using 
				html5) -->
		<header class="lotusFormTitle">
			<h1 class="lotusHeading">
				<c:out value="${view.formTitle}" />
			</h1>
		</header>

		<!-- form body -->
		<div class="lotusFormBody">
			<div class="lotusFormField">
				<label for="${view.namespace}_textField"><span
					class="lotusFormRequired"
					title="${fn:escapeXml(view.requiredFieldHint)}">*</span> <c:out
						value="${view.sampleTextTitle}" /></label>
				<div class="lotusFieldWrapper">
					<input class="lotusText" type="text"
						id="${view.namespace}_textField"
						name="${controller.keySampleText}"
						value="${fn:escapeXml(model.sampleText)}" aria-required="true">
				</div>

				<span class="lotusFormErrorIcon" id="errorTitle"><img
					class="lotusIconMsgError" src="${fn:escapeXml(view.blankImageURL)}"
					alt="Error"><span class="lotusAltText">X</span></span>
			</div>
		</div>

		<!-- form footer -->
		<div class="lotusFormFooter">
			<button id="${view.namespace}_saveButton"
				name="${controller.keyAction}" type="submit"
				value="${controller.valueActionSave}" class="lotusBtn">
				<c:out value="${view.saveButtonTitle}" />
			</button>
			<button id="${view.namespace}_cancelButton"
				name="${controller.keyAction}" type="submit"
				value="${controller.valueActionCancel}" class="lotusBtn">
				<c:out value="${view.cancelButtonTitle}" />
			</button>
		</div>

	</form>

	<div class="lotusMessage2 lotusInfo" role="alert">
		<img class="lotusIcon lotusIconMsgInfo"
			src="${fn:escapeXml(view.blankImageURL)}"
			alt="${fn:escapeXml(view.infoIconHint)}"><span
			class="lotusAltText"><c:out value="${view.infoIconTitle}" /></span>

		<div class="lotusMessageBody">
			<div class="lotusMeta">
				<ul class="lotusInlinelist">
					<li>${fn:escapeXml(model.sampleInt)}</li>
					<li><a href="${fn:escapeXml(controller.decSampleIntURL)}"
						title="${fn:escapeXml(view.decSampleIntHint)}"><c:out
								value="${view.decSampleIntTitle}" /> </a></li>
					<li><a href="${fn:escapeXml(controller.incSampleIntURL)}"
						title="${fn:escapeXml(view.incSampleIntHint)}"><c:out
								value="${view.incSampleIntTitle}" /></a></li>
					<li><a href="${fn:escapeXml(controller.clearURL)}"
						title="${fn:escapeXml(view.resetHint)}"><c:out
								value="${view.resetTitle}" /></a></li>
				</ul>
			</div>
		</div>
	</div>

</div>