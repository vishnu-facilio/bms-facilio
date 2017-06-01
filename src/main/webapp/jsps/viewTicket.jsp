<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>  
<ul>
	<s:iterator value="ticketProps">
		<li><span><s:property value='key' /> : <s:property value="value" /></span></li>
	</s:iterator>
</ul>