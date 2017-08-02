<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newUserForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
		    	<label>Name</label>
		    	<span class="required">*</span>
		    	<s:textfield name="user.name" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Email</label>
			    <span class="required">*</span>
			    <s:textfield type="email" name="user.email" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Role</label>
			    <span class="required">*</span>
			    <s:select list="roles" name="user.roleId" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Accessible Spaces</label>
			    <span class="required">*</span>
			    <select name="user.timezone" class="form-control">
			    	<option> - All - </option>
			    </select>
			</div>
		</div>
		<div class="col-lg-6" >
			<div class="form-group">
			    <label>Time zone</label>
			    <select name="user.timezone" class="form-control">
			    	<option> -- </option>
			    </select>
			</div>
			<div class="form-group">
			    <label>Phone</label>
			    <s:textfield type="text" name="user.phone" class="form-control"/>
			</div>
		</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#newUserForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#users';
		});
		
		$('#newUserForm input[name=name]').focus();
		
		$('#newUserForm').validator().on('submit', function (e) {
			  if (e.isDefaultPrevented()) {
					// handle the invalid form...
			  }
			  else {
					// check if any validation errors
					if ($(this).find('.form-group').hasClass('has-error')) {
						return false;
					}
					
					$(".save-btn").button('loading');
					FacilioApp.ajax({
						method : "post",
						url : contextPath + "/app/setup/users/add",
						data : $("#newUserForm").serialize(),
						done: function(data) {
							FacilioApp.notifyMessage('success', 'User created successfully!');
							location.href = '#users';
						},
						fail: function(error) {
							$(".save-btn").button('reset');
							console.log(error);
							alert(JSON.stringify(error.responseJSON.fieldErrors));
						}
					});
					return false;
			  	}
			});
	});
</script>