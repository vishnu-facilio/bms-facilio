<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3>New Group</h3>
<br />
<s:form id="newGroupForm" action="add" theme="simple" method="post" class="form-horizontal" style="width:600px;">
	<div class="form-group">
  		<label for="name" class="col-sm-2 control-label">Name</label>
	    	<div class="col-sm-10">  
				<s:textfield name="name" id="name" class="form-control"/>
			</div>
	</div>
	<div class="form-group">
  		<label for="email_id" class="col-sm-2 control-label">Email (optional)</label>
	    	<div class="col-sm-10">  
				<s:textfield type="email" name="email" id="email_id" class="form-control" placeholder="xyz@example.com" />
			</div>
	</div>
	<div class="form-group">
  		<label for="description" class="col-sm-2 control-label">Description</label>
	    	<div class="col-sm-10">  
				<s:textarea name="description" id="description" class="form-control"/>
			</div>
	</div>
	<div class="form-group">
  		<label for="members" class="col-sm-2 control-label">Select Members</label>
	    	<div class="col-sm-10">  
				<s:select list="userList" name="members" id="members" multiple="true" size="10" class="form-control"/>
			</div>
	</div>
	<div class="form-group">
		<div class="col-sm-10 text-center"> 
			<input class="btn btn-primary" id="addButton" type="button" value="Create">
			<a class="btn btn-default" href="<s:url action="" />" role="button">Cancel</a>
		</div>
	</div>
</s:form> 
<script>
	$(document).ready(function() {
		$("#addButton").click(function() {
			createGroup();
		});
		
		$("#newGroupForm").submit(function() {
			createGroup();
			return false;
		});
		
		createGroup = function() {
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#newGroupForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				alert('Group created successfully!');
				window.location.href='/home/groups/';
			})
			.fail(function(error) {
				alert(JSON.stringify(error.responseJSON.fieldErrors));
			});
		};
	});
</script> 