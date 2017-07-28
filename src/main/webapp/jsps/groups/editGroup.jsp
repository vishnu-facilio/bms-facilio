<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="editGroupForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
				<input type="hidden" value="<s:property value="group.groupId" />" name="group.groupId">
	    		<label>Name</label>
	    		<span class="required">*</span>
	    		<s:textfield name="group.name" value="%{group.name}" class="form-control" placeholder="Eg. HVAC Technicians" required="true"/>
			</div>
			<div class="form-group">
			    <label>Description</label>
			    <textarea name="group.description" class="form-control" placeholder="Eg. Group of HVAC Technicians"><s:property value="group.description"/></textarea>
			</div>
			<div class="form-group">
			    <label>
			    	<i class="fa fa-user field-label-icon" aria-hidden="true"></i>
			    	Users
			    </label>
			    <s:iterator var="memberId" value="members">
	       			<input type="hidden" value="<s:property value="#memberId" />" class="member-id"/>
	       		</s:iterator>
			    <select name="members" class="form-control select-user" multiple required></select>
			</div>
		</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#editGroupForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#groups';
		});
		
		var members = [];
		for (var i=0; i< $('.member-id').length; i++) {
			var mid = $($('.member-id')[i]).val();
			members.push(mid);
		}
		FacilioApp.userPickList('.select-user', {all: true, default_value: members});
		
		$('#editGroupForm').validator().on('submit', function (e) {
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
						url : contextPath + "/home/setup/groups/update",
						data : $("#editGroupForm").serialize()
					})
					.done(function(data) {
						FacilioApp.notifyMessage('success', 'Group update successfully!');
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