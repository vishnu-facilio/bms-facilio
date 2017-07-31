<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newRoleForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
	    		<label>Name</label>
	    		<span class="required">*</span>
	    		<s:textfield name="role.name" class="form-control" placeholder="Eg. Energy Manager" required="true"/>
			</div>
			<div class="form-group">
			    <label>Description</label>
			    <textarea name="role.description" class="form-control" placeholder="Eg. Role for Energy managers"></textarea>
			</div>
			<div class="form-group">
			    <div class="checkbox checkbox-primary">
       		 	 	<input type="checkbox" name="can_manage_org" id="can_manage_org">
       		 	 	<label for="can_manage_org">Can Manage the Organization?</label>
       		 	</div>
			</div>
		</div>
		<div class="col-lg-12 fc-dashed-line"></div>
		<div class="col-lg-6" >
			<div class="section-group">
	    		<span class="section-title">User Access</span>
	    		<div class="col-sm-12">
	    			<div class="col-sm-4">
	    				 <div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="user_access_administer" id="user_access_administer">
		       		 	 	<label for="user_access_administer">Administer</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="user_access_read" id="user_access_read">
		       		 	 	<label for="user_access_read">Read</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="user_access_delete" id="user_access_delete">
		       		 	 	<label for="user_access_delete">Delete</label>
		       		 	</div>
	    			</div>
	    		</div>
			</div>
			<div class="section-group">
	    		<span class="section-title">Group Access</span>
	    		<div class="col-sm-12">
	    			<div class="col-sm-4">
	    				 <div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="group_access_administer" id="group_access_administer">
		       		 	 	<label for="group_access_administer">Administer</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="group_access_read" id="group_access_read">
		       		 	 	<label for="group_access_read">Read</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="group_access_delete" id="group_access_delete">
		       		 	 	<label for="group_access_delete">Delete</label>
		       		 	</div>
	    			</div>
	    		</div>
			</div>
			<div class="section-group">
	    		<span class="section-title">Work Order Access</span>
	    		<div class="col-sm-12">
	    			<div class="col-sm-4">
	    				 <div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_create" id="wo_access_create">
		       		 	 	<label for="wo_access_create">Administer</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_read" id="wo_access_read">
		       		 	 	<label for="wo_access_read">Read</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_delete" id="wo_access_delete">
		       		 	 	<label for="wo_access_delete">Delete</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				 <div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_can_assign" id="wo_access_can_assign">
		       		 	 	<label for="wo_access_can_assign">Can assign</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				 <div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_can_be_assigned" id="wo_access_can_be_assigned">
		       		 	 	<label for="wo_access_can_be_assigned">Can be assigned?</label>
		       		 	</div>
	    			</div>
	    			<div class="col-sm-4">
	    				<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="wo_access_can_access_due_date" id="wo_access_can_access_due_date">
		       		 	 	<label for="wo_access_can_access_due_date">Can access due date</label>
		       		 	</div>
	    			</div>
	    		</div>
			</div>
			<div class="section-group">
	    		<span class="section-title">Space Management Access</span>
	    		<div class="col-sm-12">
	    			<div class="col-sm-4">
		    			<div class="checkbox checkbox-primary">
		       		 	 	<input type="checkbox" name="space_access_enable" id="space_access_enable">
		       		 	 	<label for="space_access_enable">Enable</label>
		       		 	</div>
		       		 </div>
	    		</div>
			</div>
		</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#newRoleForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#roles';
		});
		
		$('#newRoleForm').validator().on('submit', function (e) {
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
						url : contextPath + "/app/setup/roles/add",
						data : $("#newRoleForm").serialize(),
						done: function(data) {
							FacilioApp.notifyMessage('success', 'Role created successfully!');
							location.href = '#roles';
						},
						fail: function(error) {
							$(".save-btn").button('reset');
							console.log(error);
							alert(error);
						} 
					});
					return false;
			  	}
			});
	});
</script>