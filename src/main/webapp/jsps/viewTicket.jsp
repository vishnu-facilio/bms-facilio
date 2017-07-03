<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="ticket.subject" />
</h3>
<input type="hidden" name="ticketId"
	value="<s:property value="ticket.ticketId" />" />
<table class="table">
	<tr>
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
	</tr>

	<s:iterator value="ticket.customProps">
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
					<td><a href="#tasks/<s:property value='taskId' />"><s:property
								value="subject" /></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<a class="btn btn-default" href="#tickets" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Tickets</a>
<button class="btn btn-default" id="addTaskBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	Task
</button>
<div class="modal fade" id="newTaskModel" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"></div>
<script>
	$(document).ready(function() {
		$("#addTaskBtn").click(function() {

			var ticketId = $('input[name=ticketId]').val();
			$.ajax({
				type : "GET",
				url : contextPath + "/home/tasks/new?ticketId=" + ticketId,
				success : function(response) {
					$('#newTaskModel').html(response);
					$("#newTaskModel").modal("show");
				}
			});
		});
	});

	
</script>
