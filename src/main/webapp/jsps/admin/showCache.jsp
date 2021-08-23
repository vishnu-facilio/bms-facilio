<%@ page import="com.facilio.fw.cache.LRUCache" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<br>Module Cache =  <textarea rows="48" cols="150"><%
	out.println(LRUCache.getModuleFieldsCache());
%></textarea>

<br>Fields Cache = <textarea  rows="8" cols="150"><%
	out.println(LRUCache.getFieldsCache());
%></textarea>

<br>Field Name Cache = <textarea  rows="8" cols="150"><%
	out.println(LRUCache.getFieldNameCache());
%></textarea>

<br><a href="clearcache"> Clear Cache</a>
<br><a href="reloadBrowser"> Reload all users</a>



</body>
</html>

