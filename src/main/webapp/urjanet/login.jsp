<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib uri="/struts-tags" prefix="s" %>  
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
This is the login page 
<%
java.util.Enumeration in = request.getParameterNames();
while(in.hasMoreElements()) {
 String paramName = in.nextElement().toString();
 out.println(paramName + " = " + request.getParameter(paramName)+"<br>");
}

%>
</body>
</html>