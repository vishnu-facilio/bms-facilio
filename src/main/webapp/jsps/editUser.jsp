<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container">
	<form role="form" id="newUserForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
		    	<label>Name</label>
		    	<span class="required">*</span>
		    	<input type="hidden" name="user.userId" value="<s:property value="user.userId" />"/>
		    	<input type="hidden" name="user.orgUserId" value="<s:property value="user.orgUserId" />"/>
		    	<s:textfield name="user.name" value="%{user.name}" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Email</label>
			    <span class="required">*</span>
			    <s:textfield type="email" value="%{user.email}" readonly="true" name="user.email" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Role</label>
			    <span class="required">*</span>
			    <s:select list="roles" value="%{user.role.name}" name="user.role.name" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Time zone</label>
			    <select name="user.timezone" class="form-control">
			    	<option> -- </option>
			    </select>
			</div>
			<div class="form-group">
			    <label>Phone</label>
			    <s:textfield type="text" value="%{user.phone}" name="user.phone" class="form-control"/>
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
						url : contextPath + "/app/setup/users/update",
						data : $("#newUserForm").serialize(),
						done: function(data) {
							FacilioApp.notifyMessage('success', 'User updated successfully!');
							location.href = '#users';
						},
						fail: function(error) {
							$(".save-btn").button('reset');
							console.log(error);
							alert(JSON.stringify(error));
						}
					});
					return false;
			  	}
			});
	});
</script>