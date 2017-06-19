<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3>Invite User</h3>
<br />
<s:form id="inviteUserForm" action="add" theme="simple" method="post" class="form-horizontal" style="width:600px;">
	<div class="form-group">
  		<label for="email_id" class="col-sm-2 control-label">Email</label>
	    	<div class="col-sm-10">  
				<s:textfield type="email" name="email" id="email_id" class="form-control" placeholder="xyz@example.com" />
			</div>
	</div>
	<div class="form-group">
  		<label for="role" class="col-sm-2 control-label">Role</label>
	    	<div class="col-sm-10">  
				<s:select class="form-control" list="roles" name="role" id="role"/>
			</div>
	</div>
	<div class="form-group">
  		<label for="password" class="col-sm-2 control-label">Password</label>
	    	<div class="col-sm-10">  
				<s:textfield type="password" name="password" id="password" class="form-control"/>
			</div>
	</div>
	<div class="form-group">
		<div class="col-sm-10 text-center"> 
			<input class="btn btn-primary" id="addButton" type="button" value="Invite">
			<a class="btn btn-default" href="<s:url action="" />" role="button">Cancel</a>
		</div>
	</div>
</s:form> 
<script>
	$(document).ready(function() {
		$("#addButton").click(function() {
			inviteUser();
		});
		
		$("#inviteUserForm").submit(function() {
			inviteUser();
			return false;
		});
		
		inviteUser = function() {
			console.log("invite user func");
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#inviteUserForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				alert('User invited successfully!');
				window.location.href='/home/users/';
			})
			.fail(function(error) {
				alert(JSON.stringify(error.responseJSON.fieldErrors));
			});
		};
	});
</script> 