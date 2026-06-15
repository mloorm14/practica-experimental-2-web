<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.sendRedirect(request.getContextPath() +
            (session.getAttribute("user_id") != null ? "/dashboard" : "/login"));
%>