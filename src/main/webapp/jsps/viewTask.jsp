<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div style="width : 100%; height : 100%; margin : 0; padding: 0; overflow : auto;">
	<h3><s:property value="taskProps.subject" /></h3>  
	<table class="table">
		<s:iterator value="taskProps">
			<tr>
				<td class="info"><s:property value="key" /></td>
				<td><s:property value="value" /></td>
			</tr>
	  	</s:iterator>
	</table>
	
	<s:if test="%{scheduleProps != null}">
		<hr />
		<h4>Schedule</h4>
		<table class="table">
			<s:iterator value="scheduleProps">
				<tr>
					<td class="info"><s:property value="key" /></td>
					<td><s:property value="value" /></td>
				</tr>
		  	</s:iterator>	
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
	<a class="btn btn-default" href="<s:url action="" />" role="button"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back To Tasks</a>
	<button class="btn btn-default" id="addNoteBtn" data-toggle="modal" data-target="#addNoteModal" role="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Note</button>
</div>

<div class="modal fade" id="addNoteModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Add Note</h4>
      </div>
      <div class="modal-body row">
	    <s:form id="addNote" action="" theme="simple" method="post">
			<s:textfield type="hidden" name="taskId" value="%{taskId}" id="taskId" />
			<div class="form-group">
	    		<div class="col-sm-12">
					<s:textarea style="resize : none" class="form-control" name="note" label="inputNote" rows="5" placeholder="Work Notes..." id="noteBody"/>
				</div>
			</div>
		</s:form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary" id="saveNoteBtn">Save</button>
      </div>
    </div>
  </div>
</div>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<script>
	 $(document).ready(function() {
		$("#saveNoteBtn").click(function() {
			var noteBody = $("#noteBody").val();
			var noteTitle = "test";
			var taskId = $("#taskId").val();
			
			$.ajax({
				method : "post",
				url : "<s:url action='addNote' />",
				data : {body : noteBody, title : noteTitle, taskId : taskId}
			})
			.done(function(data) {
				console.log(data);
				$("#addNoteModal").modal('toggle');
				window.location.reload();
			})
			.fail(function(error) {
				
			});
			
		});
	}); 
</script> 