<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>

<%-- the sample is based on the IBM OneUI styles that ship as a theme module in WebSphere Portal. This only
matters for styling, functionally there is no dependency on the CSS classes --%>
<div id="${view.namespace}_root" class="lotusui30 mailPortlet"
	lang="${pageContext.response.locale}">

	<%-- also include the list of mails on the initial request to avoid flickering. In this
  scenario we use ajax only for the check mail usecase. --%>
	<%@include file="view.jsp"%>

	<%-- Some simple piece of script. We basically poll periodically for new mail. If 
  a new mail arrives, we send another ajax request to refresh the UI.  --%>
	<script type="text/javascript">
  (function () {
	    var root = document.getElementById("${view.namespace}_root");
	    window.setInterval(

	    function () {
	        var xhr = new XMLHttpRequest(),
	            node = document.getElementById("${view.namespace}_mailCheck"),
	            value = parseInt(node.title);
	        xhr.onreadystatechange = function () {
	            if (xhr.readyState === 4) {
	                if (parseInt(xhr.responseText) != value) {
	                    var refreshXhr = new XMLHttpRequest(),
	                        refresh = document.getElementById("${view.namespace}_mailRefresh");
	                    refreshXhr.onreadystatechange = function () {
	                        if (refreshXhr.readyState === 4) {
	                            root.innerHTML = refreshXhr.responseText;
	                        }
	                    };
	                    refreshXhr.open('GET', refresh.href, true);
	                    refreshXhr.send();
	                }
	            }
	        };
	        xhr.open('GET', node.href, true);
	        xhr.send();
	    }, 10000);
	})();
  </script>

</div>
