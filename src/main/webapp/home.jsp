<%@page import="com.facilio.aws.util.FacilioProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
 response.sendRedirect(FacilioProperties.getConfig("clientapp.url")+FacilioProperties.getConfig("login.url")); 
%>