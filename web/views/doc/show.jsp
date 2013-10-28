<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/views/layouts/main_header.jsp" />
<div>
    <%=request.getAttribute("content")%>
</div>

<br /><br />

<jsp:include page="/views/layouts/main_footer.jsp" />