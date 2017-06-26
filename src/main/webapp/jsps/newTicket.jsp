<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3>Submit a <s:property value="moduleName" /></h3>
<br />
<s:form id="addTicketForm" action="add" theme="simple" method="post" class="form-horizontal">
	<div class="form-group">
  		<label for="inputRequester" class="col-sm-2 control-label">Requester</label>
	    	<div class="col-sm-10">  
				<s:textfield type="email" name="requestor" id="inputRequester" class="form-control" placeholder="xyz@example.com" />
			</div>
	</div>
	<div class="form-group">
  		<label for="inputSubject" class="col-sm-2 control-label">Subject</label>
	    	<div class="col-sm-10">  
				<s:textfield name="subject" id="inputSubject" class="form-control" placeholder="Batmobile is not working" />
			</div>
	</div>
	<div class="form-group">
  		<label for="inputStatus" class="col-sm-2 control-label">Status</label>
	    	<div class="col-sm-10">  
				<s:select class="form-control" list="statusList" name="status" id="inputStatus"/>
			</div>
	</div>
	<div class="form-group">
  		<label for="inputAgent" class="col-sm-2 control-label">Agent</label>
	    	<div class="col-sm-10">
				<s:select class="form-control" list="agentList" name="agent" id="inputAgent" headerKey="-1" headerValue="--" />
			</div>
	</div>			
	<div class="form-group">
  		<label for="inputAsset" class="col-sm-2 control-label">Asset</label>
	    	<div class="col-sm-10">
				<s:select class="form-control" list="assetList" name="asset" id="inputAsset" headerKey="-1" headerValue="--" />
			</div>
	</div>
	<s:iterator var="customField" value="customFields">
	<div class="form-group">
		<label for="input<s:property value="customField"/>" class="col-sm-2 control-label"><s:property value="customField"/></label>
	    	<div class="col-sm-10">
				<s:textfield name="customFields['%{customField}']" id="input%{customField}" class="form-control" />
			</div>
	</div>
	</s:iterator>
	<div class="form-group">
  		<label for="inputDescription" class="col-sm-2 control-label">Description</label>
	    	<div class="col-sm-10">
				<s:textarea class="form-control" name="description" label="inputDescription" rows="3" placeholder="More about the problem..." />
			</div>
	</div>
	<div class="form-group">
		<div class="col-sm-10 text-center"> 
			<input class="btn btn-primary" id="addButton" type="button" value="Submit">
			<a class="btn btn-default" href="<s:url action="" />" role="button">Cancel</a>
		</div>
	</div>
	<s:textfield type="hidden" name="moduleName" value="%{moduleName}" />
</s:form> 
<script>
	$(document).ready(function() {
		$("#addButton").click(function() {
			console.log("test");
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#addTicketForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				window.location.href=data.ticketId;
			})
			.fail(function(error) {
				
			});
		});
	});
</script> 