<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newSkillForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
	    		<label>Name</label>
	    		<span class="required">*</span>
	    		<s:hidden name="skill.id"/>
	    		<s:textfield name="skill.name" class="form-control" placeholder="Eg. HVAC Maintanence" required="true"/>
			</div>
			<div class="form-group">
			    <label>Description</label>
			    <s:textarea name="skill.description" value="%{skill.description}" class="form-control" placeholder="Eg. AHU Cleaning and Reading Maintanence"/>
			    <!-- textarea name="skill.description" value= class="form-control" placeholder="Eg. AHU Cleaning and Reading Maintanence"></textarea -->
			</div>
			<div class="form-group">
       		 	 	<s:checkbox name="skill.active" value="%{skill.active}" />
       		 	 	<!-- input type="checkbox" value="%{skill.active} name="skill.active" id="isActive" -->
       		 	 	<label>Is Active Skill ?</label>
			</div>
		</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#newSkillForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#skills';
		});
		
		$('#newSkillForm').validator().on('submit', function (e) {
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
						url : contextPath + "/app/setup/skills/update",
						data : $("#newSkillForm").serialize(),
						done: function(data) {
							FacilioApp.notifyMessage('success', 'Skill updated successfully!');
							location.href = '#skills';
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