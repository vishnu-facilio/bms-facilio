<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>    
<s:form action="addTicket">  
	<s:textfield name="requestor" label="Requestor" />
	<s:textfield name="subject" label="Subject" />
	<s:select list="statusList" name="status" label="Status"/>
	<s:select list="agentList" name="agent" label="Agent" headerKey="-1" headerValue="--" />
	<s:select list="assetList" name="asset" label="Asset" headerKey="-1" headerValue="--" />
	<s:textarea name="description" label="Description" />  
	<s:submit value="Add"></s:submit>  
</s:form>  