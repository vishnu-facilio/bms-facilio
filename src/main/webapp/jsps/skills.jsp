<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Skills
       	<button data-toggle="modal" data-target="#newSkillModel" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New
       	</button>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-lg-12">
   		<table width="100%" class="table table-striped table-bordered table-hover" id="skills-list">
   			<thead>
   				<tr>
   					<th class="col-md-4 sortable">Name</th>
   					<th class="col-md-2 sortable">Description</th>
   					<th class="col-md-2 sortable">Status</th>
   					<th class="col-md-2 sortable"></th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="skill" value="skills">
					<tr class="odd gradeX" id="<s:property value="#skill.skillId" />">
			            <td><a href="#skills/<s:property value="#skill.skillId" />"><s:property value="#skill.name" /></a></td>
			            <td><s:property value="#skill.description" /></td>
			            <td>
	                    	<s:if test="%{#skill.isActive()}">
								<h5><span class="label label-success">Active</span></h5>
							</s:if>
							<s:else>
								<h5><span class="label label-danger">Inactive</span></h5>
							</s:else>
	                    </td>
			            <td class="text-center">
			            	<div class="btn-group">
								<button type="button" skill-id="<s:property value="#skill.skillId" />" data-toggle="tooltip" data-placement="top" title="Edit skill" onclick="editSkill(this);" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-pencil"></i>
	                    		</button>
	                    		<button type="button" skill-id="<s:property value="#skill.skillId" />" data-toggle="tooltip" data-placement="top" title="Delete skill" onclick="deleteSkill(this);" class="btn btn-outline btn-danger btn-md">
	                    			<i class="fa fa-trash"></i>
	                    		</button>
	                    	</div>
			            </td>
			        </tr>
				</s:iterator>
             </tbody>
      </table>
  </div>
</div>
<div class="modal fade" id="newSkillModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">New Skill</h4>
	        </div>
	        <div class="modal-body">
	        	<form role="form" id="newSkillForm" method="post" onsubmit="return false;">
					<div class="form-group">
					    <label>Name</label>
					    <s:textfield name="skill.name" class="form-control"/>
					</div>
					<div class="form-group">
					    <label>Description</label>
					    <s:textarea name="skill.description" class="form-control"/>
					</div>
					<div class="form-group">
				  		<div class="checkbox">
				  			<label><s:checkbox name="skill.isActive" value="true" cssClass="checkbox"/>Active</label>
				  		</div>
				  	</div>
				</form>
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveSkill(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<script>
	$("#skills-list").dataTable({
		responsive: true
	});

	$('.row').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
	
	function saveSkill(btn) {
		$(btn).button('loading');
		
		FacilioApp.ajax({
			method : "post",
			url : contextPath + "/app/skills/add",
			data : $("#newSkillForm").serialize(),
			done: function(data) {
				$('#newSkillModel').modal('hide');
				FacilioApp.notifyMessage('success', 'Skill created successfully!');
				
				setTimeout(function() {
					FacilioApp.refreshView();
	            }, 500);
			},
			fail: function(error) {
				$(btn).button('reset');
				alert(JSON.stringify(error.responseJSON.fieldErrors));
			}
		});
	}
</script>