<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3>Submit <s:property value="moduleName" /></h3>
<s:form id="addTaskForm" theme="simple" method="post" class="form-horizontal">
	
	<div class="form-group">
  		<label for="inputParent" class="col-sm-2 control-label">Parent</label>
	    	<div class="col-sm-10">  
				<s:textfield name="parent" id="inputParent" value="%{ticketId}" readonly="true" class="form-control" />
			</div>
	</div>
	
	<div class="form-group">
  		<label for="inputSubject" class="col-sm-2 control-label">Subject</label>
	    	<div class="col-sm-10">  
				<s:textfield name="subject" id="inputSubject" class="form-control" placeholder="Batmobile is not working" />
			</div>
	</div>
	
	<div class="form-group">
  		<label for="inputAssignedTo" class="col-sm-2 control-label">Assigned To</label>
	    	<div class="col-sm-10">
				<s:select class="form-control" list="assignedToList" name="assignedTo" id="inputAssignedTo" headerKey="-1" headerValue="--" />
			</div>
	</div>	

	<s:iterator var="customFieldName" value="customFieldNames">
	<div class="form-group">
		<label for="input<s:property value="customFieldName"/>" class="col-sm-2 control-label"><s:property value="customFieldName"/></label>
	    	<div class="col-sm-10">
				<s:textfield name="customFields['%{customFieldName}']" id="input%{customFieldName}" class="form-control" />
			</div>
	</div>
	</s:iterator>
	
	<div class="form-group">
  		<label for="inputDescription" class="col-sm-2 control-label">Description</label>
	    	<div class="col-sm-10">
				<s:textarea class="form-control" name="description" label="inputDescription" rows="3" placeholder="More about the problem..." />
			</div>
	</div>
	
	<hr />
	
	<div class="form-group">
  		<label for="inputScheduleStart" class="col-sm-2 control-label">Schedule Start</label>
	    	<div class="col-sm-10">
				<s:textfield class="form-control" type="datetime-local" name="scheduleStart" id="inputScheduleStart" />
			</div>
	</div>
	
	<div class="form-group">
  		<label for="inputEstimatedEnd" class="col-sm-2 control-label">Estimated End</label>
	    	<div class="col-sm-10">
				<s:textfield class="form-control" type="datetime-local" name="estimatedEnd" id="inputEstimatedEnd" readonly="true" />
			</div>
	</div>
	
	<div class="form-group form-inline">
  		<label class="col-sm-2 control-label">Estimated Work Duration</label>
	    	<div class="col-sm-10" id="estimatedDuration">
	    		<div class="input-group col-sm-4">
      				<div class="input-group-addon">Days</div>
					<s:textfield class="form-control" name="durationDays" type="number" value="0"/>
				</div>
				<div class="input-group col-sm-2">
      				<div class="input-group-addon">Hours</div>
					<s:textfield class="form-control" name="durationHours" type="number" value="1" />
				</div>
				<div class="input-group col-sm-2">
      				<div class="input-group-addon">Minutes</div>
					<s:textfield class="form-control" name="durationMinutes" type="number" value="0" />
				</div>
				<div class="input-group col-sm-2">
      				<div class="input-group-addon">Seconds</div>
					<s:textfield class="form-control" name="durationSeconds" type="number" value="0" />
				</div>
			</div>
	</div>
	
	<div class="form-group">
  		<label for="inputActualWorkStart" class="col-sm-2 control-label">Actual Work Start</label>
	    	<div class="col-sm-10">
				<s:textfield class="form-control" type="datetime-local" name="actualWorkStart" id="inputActualWorkStart" />
			</div>
	</div>
	
	<div class="form-group">
  		<label for="inputActualWorkEnd" class="col-sm-2 control-label">Actual Work End</label>
	    	<div class="col-sm-10">
				<s:textfield class="form-control" type="datetime-local" name="actualWorkEnd" label="inputActualWorkEnd" />
			</div>
	</div>
	
	<div class="form-group">
		<div class="col-sm-10 text-center"> 
			<input class="btn btn-primary" id="addButton" type="button" value="Submit">
			<a class="btn btn-default" href="<s:url action="" />" role="button">Cancel</a>
		</div>
	</div>
	
</s:form>

<script>
	$(document).ready(function() {
		$("#addButton").click(function() {
			console.log("test");
			
			/* $("#addTaskForm input").each(function(index, input) {
				console.log($(input).attr("name")+"::"+$(input).val());
			}); */
			
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#addTaskForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				window.location.href=data.taskId;
			})
			.fail(function(error) {
				
			});
		});
		
		var updateEndTime = function() {
			console.log("test");
			var days = $("#estimatedDuration input[name=durationDays]").val();
			var hours = $("#estimatedDuration input[name=durationHours]").val();
			var min = $("#estimatedDuration input[name=durationMinutes]").val();
			var sec = $("#estimatedDuration input[name=durationSeconds]").val();
			
			var date = new Date($("#inputScheduleStart").val());
			date.setDate(date.getDate() + parseInt(days));
			date.setHours(date.getHours() + parseInt(hours));
			date.setMinutes(date.getMinutes() + parseInt(min));
			date.setSeconds(date.getSeconds() + parseInt(sec));
			
			var endDay = ("0" + date.getDate()).slice(-2);
			var endMonth = ("0" + (date.getMonth() + 1)).slice(-2);
			var endHour = ("0" + date.getHours()).slice(-2);
			var endMinute = ("0" + date.getMinutes()).slice(-2);
			
			var endDate = date.getFullYear()+"-"+(endMonth)+"-"+(endDay)+"T"+endHour+":"+endMinute ;
			
			$("#inputEstimatedEnd").val(endDate);
		}
		
		$("#inputScheduleStart").on("change", updateEndTime);
		$("#estimatedDuration input").on("change", updateEndTime);
		
	});
</script>