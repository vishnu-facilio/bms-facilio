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

<s:if test="%{tasks != null}">
	<hr />
	<h4>Tasks</h4>
	<table class="table table-striped table-hover table-bordered">
		<thead>
			<tr>
				<th>ID</th>
				<th>Subject</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="tasks">
				<tr>
					<td><s:property value="taskId" /></td>
					<td><a href="${pageContext.request.contextPath}/home/tasks/<s:property value='taskId' />"><s:property value="subject" /></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<a class="btn btn-default" href="<s:url action="" />" role="button"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back To Tickets</a>
<button class="btn btn-default" id="addTaskBtn" role="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Task</button>
<s:form id="addTask" action="/home/tasks/new" theme="simple" method="post">
	<s:textfield type="hidden" name="ticketId" value="%{ticketId}" />
</s:form>

<script>
	$(document).ready(function() {
		$("#addTaskBtn").click(function() {
			console.log("test");
			$("#addTask").submit();
		});
	});
</script> 