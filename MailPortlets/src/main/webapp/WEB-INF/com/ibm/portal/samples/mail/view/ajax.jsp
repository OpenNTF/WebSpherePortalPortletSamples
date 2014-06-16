<%-- Per default a JSP generates a session. Make sure to disable this for performance reasons. --%>
<%@ page session="false" buffer="none"%>
<%-- Just the standard JSTL includes --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- hosting div, content will be replaced by AJAX response --%>
<div>

  <%-- the anchor that contains the URL of the AJAX resource request --%>
  <a id="${view.namespace}_xhr" href="${fn:escapeXml(controller.renderMessageURL)}"> <img
    src="${fn:escapeXml(view.loadingImageURL)}">
  </a>

  <%-- some simple script that replaces the view --%>
  <script type="text/javascript">
              (function() {
                var xhr = new XMLHttpRequest(), node = document
                    .getElementById("${view.namespace}_xhr");
                xhr.onreadystatechange = function() {
                  if (xhr.readyState === 4) {
                    node.parentNode.innerHTML = xhr.responseText;
                  }
                };
                xhr.open('GET', node.href, true);
                xhr.send();
              })();
            </script>
</div>
