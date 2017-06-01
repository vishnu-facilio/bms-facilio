<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>  
<ul>
	<s:iterator value="ticketsId">
		<li><a href="tickets/<s:property value='key' />"><s:property value="value" /></a></li>
	</s:iterator>
</ul>