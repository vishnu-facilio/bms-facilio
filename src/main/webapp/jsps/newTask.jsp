<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title" id="myModalLabel">New Task</h4>
        </div>
        <div class="modal-body">
        	<form role="form" id="addTaskForm" method="post" onsubmit="return false;">
				<div class="form-group">
				    <label>Parent</label>
				    <s:textfield name="task.parent" id="inputParent" value="%{ticketId}" readonly="true" class="form-control" />
				</div>
				<div class="form-group">
				    <label>Subject</label>
				    <s:textfield name="task.subject" id="inputSubject" class="form-control" placeholder="Batmobile is not working" />
				</div>
				<div class="form-group">
				    <label>Assignment Group</label>
				    <s:select class="form-control" list="actionForm.groupList" name="task.assignmentGroupId" id="inputAssignmentGroup" headerKey="0" headerValue="--" />
				</div>
				<div class="form-group">
				    <label>Assigned To</label>
				    <select  class="form-control" name="task.assignedToId" id="inputAssignedTo" disabled=true>
				    	<option value="0">--</option>
				    </select>
				</div>
				<s:iterator var="customFieldName" value="customFieldNames">
					<div class="form-group">
					    <label><s:property value="customFieldName"/></label>
					    <s:textfield name="task.customProps['%{customFieldName}']" id="input%{customFieldName}" class="form-control" />
					</div>
				</s:iterator>
				<div class="form-group">
				    <label>Description</label>
				    <s:textarea class="form-control" name="task.description" label="inputDescription" rows="3" placeholder="More about the problem..." />
				</div>
				<hr />
				<div class="form-group">
			  		<label>Scheduled Start</label>
				    	<div>
							<s:textfield class="form-control" type="datetime-local" name="scheduleObj.scheduledStart" id="inputScheduleStart" />
						</div>
				</div>
				
				<div class="form-group">
			  		<label>Estimated End</label>
				    	<div>
							<s:textfield class="form-control" type="datetime-local" name="scheduleObj.estimatedEnd" id="inputEstimatedEnd" readonly="true" />
						</div>
				</div>
				
				<div class="form-group form-inline">
			  		<label>Estimated Work Duration</label>
				    <div id="estimatedDuration">
			    		<div class="input-group col-sm-3">
		      				<div class="input-group-addon">Days</div>
							<input class="form-control" name="durationDays" type="number" value="0"/>
						</div>
						<div class="input-group col-sm-3">
		      				<div class="input-group-addon">Hours</div>
							<input class="form-control" name="durationHours" type="number" value="1" />
						</div>
						<div class="input-group col-sm-3">
		      				<div class="input-group-addon">Mins</div>
							<input class="form-control" name="durationMinutes" type="number" value="0" />
						</div>
					<%-- 	<div class="input-group col-sm-3">
		      				<div class="input-group-addon">Secs</div>
							<input class="form-control" name="durationSeconds" type="number" value="0" />
						</div>--%>
					</div>
				</div>
				
				<div class="form-group">
			  		<label>Actual Work Start</label>
				    	<div>
							<s:textfield class="form-control" type="datetime-local" name="scheduleObj.actualWorkStart" id="inputActualWorkStart" />
						</div>
				</div>
				
				<div class="form-group">
			  		<label>Actual Work End</label>
				    	<div>
							<s:textfield class="form-control" type="datetime-local" name="scheduleObj.actualWorkEnd" label="inputActualWorkEnd" />
						</div>
				</div>
			</form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveTask(this);" class="btn btn-primary">Save</button>
        </div>
    </div>
    <!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
<script>
	function saveTask(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/tasks/add",
			data : $("#addTaskForm").serialize()
		})
		.done(function(data) {
			$('#newTaskModel').modal('hide');
			FacilioApp.notifyMessage('success', 'Task created successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
	
	$(document).ready(function() {
		
		var updateEndTime = function() {
			var days = $("#estimatedDuration input[name=durationDays]").val();
			var hours = $("#estimatedDuration input[name=durationHours]").val();
			var min = $("#estimatedDuration input[name=durationMinutes]").val();
			//var sec = $("#estimatedDuration input[name=durationSeconds]").val();
			
			var date = new Date($("#inputScheduleStart").val());
			date.setDate(date.getDate() + parseInt(days));
			date.setHours(date.getHours() + parseInt(hours));
			date.setMinutes(date.getMinutes() + parseInt(min));
			//date.setSeconds(date.getSeconds() + parseInt(sec));
			
			var endDay = ("0" + date.getDate()).slice(-2);
			var endMonth = ("0" + (date.getMonth() + 1)).slice(-2);
			var endHour = ("0" + date.getHours()).slice(-2);
			var endMinute = ("0" + date.getMinutes()).slice(-2);
			
			var endDate = date.getFullYear()+"-"+(endMonth)+"-"+(endDay)+"T"+endHour+":"+endMinute ;
			
			$("#inputEstimatedEnd").val(endDate);
		}
		
		$("#inputScheduleStart").on("change", updateEndTime);
		$("#estimatedDuration input").on("change", updateEndTime);
		
		$("#inputAssignmentGroup").on("change", function() {
			var groupId = $(this).val();
			
			if (groupId == 0) {
				updateSelect("#inputAssignedTo", {});
				$("#inputAssignedTo").attr("disabled",true);
			}
			else {
				$.ajax({
					method : "post",
					url : "<s:url action='getGroupMembers' namespace='/home/groups' />",
					data : {groupId : groupId}
				})
				.done(function(data) {
					FacilioApp.updateSelect("#inputAssignedTo", data);
					$("#inputAssignedTo").attr("disabled",false);
				})
				.fail(function(error) {
					
				});
			}
		});
		
	});
</script>