<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div
	style="width: 100%; height: 100%; margin: 0; padding: 0; overflow: auto;">
	<h3>
		<s:property value="taskProps.subject" />
	</h3>
	<table class="table">
		<tr>
			<td class="info">Task ID</td>
			<td><s:property value="task.taskId" /></td>
		</tr>
		<tr>
			<td class="info">Parent</td>
			<td><s:property value="task.parent" /></td>
		</tr>
		<tr>
			<td class="info">Subject</td>
			<td><s:property value="task.subject" /></td>
		</tr>
		<tr>
			<td class="info">Description</td>
			<td><s:property value="task.description" /></td>
		</tr>
		<tr>
			<td class="info">Assignment Group</td>
			<td><s:property value="task.assignmentGroupId" /></td>
		</tr>
		<tr>
			<td class="info">Assigned To</td>
			<td><s:property value="task.assignedToId" /></td>
		</tr>
		<tr>
			<td class="info">Schedule ID</td>
			<td><s:property value="task.scheduleId" /></td>
		</tr>
		<s:iterator value="task.customProps">
			<tr>
				<td class="info"><s:property value="key" /></td>
				<td><s:property value="value" /></td>
			</tr>
		</s:iterator>
	</table>

	<s:if test="%{scheduleObj != null}">
		<hr />
		<h4>Schedule</h4>
		<table class="table">
			<tr>
				<td class="info">Schedule ID</td>
				<td><s:property value="scheduleObj.scheduleId" /></td>
			</tr>
			<tr>
				<td class="info">Scheduled Start</td>
				<td><s:property value="scheduleObj.scheduledStart" /></td>
			</tr>
			<tr>
				<td class="info">Estimated End</td>
				<td><s:property value="scheduleObj.estimatedEnd" /></td>
			</tr>
			<tr>
				<td class="info">Actual Work Start</td>
				<td><s:property value="scheduleObj.actualWorkStart" /></td>
			</tr>
			<tr>
				<td class="info">Actual Work End</td>
				<td><s:property value="scheduleObj.actualWorkEnd" /></td>
			</tr>
		</table>
	</s:if>

	<s:if test="%{notes != null}">
		<h4>Notes</h4>
		<s:iterator value="notes">
			<div class="panel panel-default">
				<div class="panel-body">
					<s:property value="body" />
				</div>
			</div>
		</s:iterator>
	</s:if>
	<a class="btn btn-default" href="#tasks" role="button"><span
		class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
		To Tasks</a>
	<button class="btn btn-default" id="addNoteBtn" data-toggle="modal"
		data-target="#addNoteModal" role="button">
		<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
		Note
	</button>
</div>
<div class="modal fade" id="addNoteModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">Add Note</h4>
			</div>
			<div class="modal-body">
				<form role="form" id="addNoteForm" method="post"
					onsubmit="return false;">
					<s:textfield type="hidden" name="taskId" value="%{taskId}"
						id="taskId" />
					<div class="form-group">
						<textarea style="resize: none" class="form-control"
							name="note.body" rows="5" placeholder="Work Notes..."
							id="noteBody"></textarea>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="submit"
					data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving"
					onclick="saveNote(this);" class="btn btn-primary">Save</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<script>
	function saveNote(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/tasks/addNote",
			data : $("#addNoteForm").serialize()
		})
		.done(function(data) {
			$('#addNoteModal').modal('hide');
			FacilioApp.notifyMessage('success', 'Note added successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
</script>
