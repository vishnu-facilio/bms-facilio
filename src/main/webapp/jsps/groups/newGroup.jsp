<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newGroupForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
	    		<label>Name</label>
	    		<span class="required">*</span>
	    		<s:textfield name="group.name" class="form-control" placeholder="Eg. HVAC Technicians" required="true"/>
			</div>
			<div class="form-group">
			    <label>Description</label>
			    <textarea name="group.description" class="form-control" placeholder="Eg. Group of HVAC Technicians"></textarea>
			</div>
			<div class="form-group">
			    <label>
			    	<i class="fa fa-user field-label-icon" aria-hidden="true"></i>
			    	Users
			    </label>
			    <select list="userList" name="members" class="form-control select-user" multiple="true" required="true"/>
			</div>
		</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#newGroupForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#groups';
		});
		
		FacilioApp.userPickList('.select-user', {all: true});
		
		$('#newGroupForm').validator().on('submit', function (e) {
			  if (e.isDefaultPrevented()) {
					// handle the invalid form...
			  }
			  else {
					// check if any validation errors
					if ($(this).find('.form-group').hasClass('has-error')) {
						return false;
					}
					
					$(".save-btn").button('loading');
					$.ajax({
						method : "post",
						url : contextPath + "/home/setup/groups/add",
						data : $("#newGroupForm").serialize()
					})
					.done(function(data) {
						FacilioApp.notifyMessage('success', 'Group created successfully!');
						location.href = '#groups';
					})
					.fail(function(error) {
						$(".save-btn").button('reset');
						console.log(error);
						alert(error);
					});
					return false;
			  	}
			});
	});
</script>