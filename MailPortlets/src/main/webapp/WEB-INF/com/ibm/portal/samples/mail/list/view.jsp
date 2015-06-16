<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- paging control --%>
<nav class="lotusPaging lotusPagingTop" role="navigation"
	aria-label="${fn:escapeXml(view.pagingPrimary)}">

	<%-- ACCESSIBILITY NOTE: Always place a space on both sides of the dash between page numbers. 
    If you don't, JAWS will read the text as "one dash one zero" instead of "one dash ten" --%>
	<div class="lotusLeft" title="${fn:escapeXml(view.pagingShowingLarge)}"
		aria-label="${fn:escapeXml(view.pagingShowingLarge)}">
		<c:out value="${view.pagingShowingSmall}" />
	</div>

	<%-- next and previous link --%>
	<ul class="lotusRight lotusInlinelist">
		<c:if test="${not model.firstPage}">
			<li>
				<%-- make sure to escape all URLs! The controller bean returns the URL object that is coerced to a string
        by fn:escapeXml and the resulting string will be escaped --%> <a
				href="${fn:escapeXml(controller.previousPageURL)}"
				<%-- all text strings that come out of the resource bundle require escaping, too. If they appear
          in an attribute, we use the fn:escapeXml function. --%>
          title="${fn:escapeXml(view.pagingPreviousPageTitle)}">
					<%-- if translated text appears in the body of an element, we use c:out for escaping --%>
					<c:out value="${view.pagingPreviousPage}" />
			</a>
			</li>
		</c:if>
		<c:if test="${not model.lastPage}">
			<li><a href="${fn:escapeXml(controller.nextPageURL)}"
				title="${fn:escapeXml(view.pagingNextPageTitle)}"><c:out
						value="${view.pagingNextPage}" /> </a></li>
		</c:if>
	</ul>

	<%-- links that take you directly to the page --%>
	<ul class="lotusInlinelist">
		<c:forEach items="${model.quickPagesIdx}" var="page"
			varStatus="pageStatus">
			<%-- note that string constants for CSS classes are directly coded into the JSP to keep 
        all markup related aspects of the UX in one place. The translated strings are independent
        of the markup structure, so they come out of a central resource bundle. --%>
			<li
				class="${pageStatus.first ? 'lotusFirst' : pageStatus.last ? 'lousLast' : ''}"><c:choose>
					<c:when
						test="${(page ge 0) and not (page eq model.currentPageIdx)}">
						<a href="${fn:escapeXml(controller.quickPageURL[page])}"
							title="${fn:escapeXml(view.pagingQuickPageTitle[page])}"><c:out
								value="${page + 1}" /> </a>
					</c:when>
					<%-- display an ellipsis for the link boundaries. Use the hex based entity to also support xhtml. --%>
					<c:when test="${page lt 0}">&#x2026;</c:when>
					<%-- this is the selected page --%>
					<c:otherwise>
						<c:out value="${page + 1}" />
					</c:otherwise>
				</c:choose></li>
		</c:forEach>
	</ul>

</nav>

<table class="lotusTable" border="0" cellspacing="0" cellpadding="0"
	summary="table summary goes here...">
	<tbody>
		<c:forEach items="${model.items}" var="item">

			<tr class="lotusFirst ${model.selected[item] ? 'lotusSelected' : ''}">
				<td class="lotusFirstCell" style="width: 34px"><img
					width="32px" height="32px"
					src="${fn:escapeXml(view.personImageURL[item])}"
					alt="${fn:escapeXml(item.senderMail)}"
					title="${fn:escapeXml(item.senderMail)}" /><span
					class="lotusAltText">icon</span></td>
				<td width="100%"><h4 class="lotusTitle">
						<a href="${fn:escapeXml(controller.selectionURL[item.id])}"><c:out
								value="${item.subject}" /></a>
					</h4>
					<div class="lotusMeta">
						<ul class="lotusInlinelist">
							<li class="lotusFirst">Sent by <a href="javascript:;"
								class="lotusPerson"><c:out value="${item.sender}" /></a></li>
							<li><c:out value="${view.date[item.lastModified]}" /></li>
						</ul>
					</div></td>
			</tr>
		</c:forEach>

	</tbody>
</table>

<%-- bottom paging control --%>
<nav class="lotusPaging" role="navigation" aria-label="Secondary Paging">
	<ul class="lotusLeft lotusInlinelist" aria-label="Show" role="toolbar">
		<li class="lotusFirst" role="presentation"><c:out
				value="${view.pagingShowPrefix}" /></li>

		<%-- iterate over the page sizes --%>
		<c:forEach items="${model.pageSizes}" var="page"
			varStatus="pageStatus">
			<%-- selector for the page size --%>
			<li class="${pageStatus.first ? 'lotusFirst' : ''}" role="button"
				aria-pressed="${model.pageSize eq page}" aria-disabled="true"
				title="${fn:escapeXml(view.pagingPageSize[page])}"><c:choose>
					<c:when test="${model.pageSize eq page}">
						<c:out value="${page}" />
					</c:when>
					<c:otherwise>
						<a href="${fn:escapeXml(controller.pageSizeURL[page])}"> <c:out
								value="${page}" />
						</a>
					</c:otherwise>
				</c:choose></li>
		</c:forEach>

		<li class="lotusLast" role="presentation"><c:out
				value="${view.pagingShowSuffix}" /></li>
	</ul>

	<%-- next and previous link --%>
	<ul class="lotusRight lotusInlinelist">
		<c:if test="${not model.firstPage}">
			<li><a href="${fn:escapeXml(controller.previousPageURL)}"
				title="${fn:escapeXml(view.pagingPreviousPageTitle)}"> <c:out
						value="${view.pagingPreviousPage}" /></a></li>
		</c:if>
		<c:if test="${not model.lastPage}">
			<li><a href="${fn:escapeXml(controller.nextPageURL)}"
				title="${fn:escapeXml(view.pagingNextPageTitle)}"><c:out
						value="${view.pagingNextPage}" /> </a></li>
		</c:if>
	</ul>

</nav>
<%--end paging--%>

<%-- mail check --%>
<div>
	<a id="${view.namespace}_mailCheck"
		href="${fn:escapeXml(controller.mailCheckURL)}" style="display: none"
		title="${model.itemCount}"></a> <a id="${view.namespace}_mailRefresh"
		href="${fn:escapeXml(controller.refreshURL)}" style="display: none"></a>
</div>