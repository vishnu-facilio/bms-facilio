<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3><s:property value="ticket.subject" /></h3>  
<table class="table">
	<%-- <tr>
		<td class="info">Requester</td>
		<td><s:property value="ticket.requester" /></td>
	</tr>
	<tr>
		<td class="info">Subject</td>
		<td><s:property value="ticket.subject" /></td>
	</tr>
	<tr>
		<td class="info">Description</td>
		<td><s:property value="ticket.description" /></td>
	</tr>
	<tr>
		<td class="info">Status</td>
		<td><s:property value="ticket.status" /></td>
	</tr>
	<tr>
		<td class="info">Agent ID</td>
		<td><s:property value="ticket.agentId" /></td>
	</tr>
	<tr>
		<td class="info">Asset ID</td>
		<td><s:property value="ticket.failedAssetId" /></td>
	</tr>
	<tr>
		<td class="info">Due Date</td>
		<td><s:property value="ticket.dueTime" /></td>
	</tr> --%>
	
	<s:iterator value="ticket">
		<tr>
			<td class="info"><s:property value="key" /></td>
			<td><s:property value="value" /></td>
		</tr>
  	</s:iterator>	
</table>
<a class="btn btn-default" href="<s:url action="" />" role="button"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back To Tickets</a>