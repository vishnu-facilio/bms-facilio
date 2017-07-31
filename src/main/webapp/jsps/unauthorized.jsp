<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.opensymphony.xwork2.ActionContext" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String serverName = request.getServerName();
	int serverPort = request.getServerPort();

	String HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Unauthorized access</title>
</head>
<body>
<h1> You are not authorized to access this portal: <%= serverName %></h1>
<br/>
<h4>You already logged in this portal: <a href="http://${sessionScope.USER_INFO.subdomain}<%=HOSTNAME%>:<%=serverPort%>${pageContext.request.contextPath}/app/index">${sessionScope.USER_INFO.subdomain}<%=HOSTNAME%></a>, so you can click this link to access your portal.</h4>
</body>
</html>