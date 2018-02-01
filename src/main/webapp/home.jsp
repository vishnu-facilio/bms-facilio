<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
response.sendRedirect(com.facilio.aws.util.AwsUtil.getConfig("clientapp.url")+com.facilio.aws.util.AwsUtil.getConfig("login.url"));
%>