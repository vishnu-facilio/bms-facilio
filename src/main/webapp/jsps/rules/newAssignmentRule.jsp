<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">New Assignment Rule</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
<div class="col-lg-12">
	<form role="form" id="newRuleForm" method="post" onsubmit="return false;">
		<div class="row">
			<div class="col-md-6">
				<div class="form-group">
					<label>Name</label>
	           	    <s:textfield name="rule.name" id="name" class="form-control"/>
	           	</div>
	           	<div class="form-group">
				    <label>Description</label>
				    <s:textarea name="rule.description" id="description" class="form-control"/>
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group">
					<label>Execution Order</label>
	           	    <s:textfield name="rule.executionOrder" id="executionOrder" value="1" class="form-control"/>
	           	</div>
	           	<div class="form-group">
				    <label>Active</label>
				    <s:checkbox name="rule.isActive" id="isActive" checked="true" class="form-control"/>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">
				  <div class="panel-heading">Applies To</div>
				  <div class="panel-body">
				    <div class="form-group">
						<label>Module</label>
						<s:select class="form-control" list="form.modules" name="rule.module" id="module"/>
		           	</div>
		           	<div class="form-group">
						<label>Conditions</label>
		           	    <div id="conditions"></div>
		           	</div>
				  </div>
				</div>
				<div class="panel panel-default">
				  <div class="panel-heading">Assign To</div>
				  <div class="panel-body">
				    <div class="form-group">
						<label>User</label>
						<s:select class="form-control" list="assignedToList" name="assignedTo" headerKey="-1" headerValue="--" />
		           	</div>
		           	<div class="form-group">
						<label>Group</label>
						<s:select class="form-control" list="assignmentGroupList" name="assignmentGroup" headerKey="-1" headerValue="--" />
		           	</div>
				  </div>
				</div>
	           	<div class="form-group text-right">
	           		<button type="reset" class="btn btn-outline btn-default" onclick="location.href='#tickets';">Go Back</button>
	           		<button type="submit" class="btn btn-outline btn-primary">Save</button>
	           	</div>
	        </div>
	    </div>
   </form>
</div>
</div>
<script>
	$(document).ready(function() {
		
		$("#newRuleForm").submit(function() {
			
			var ruleName = $("#name").val();
			var ruleDesc = $("#description").val();
			var executionOrder = $("#executionOrder").val();
			var isActive = ($("#isActive:checked").length > 0) ? true : false;
			var ruleModule = $("#module").val();
			var assignedTo = $("select[name=assignedTo]").val();
			var assignmentGroup = $("select[name=assignmentGroup]").val();
			
			var action_params = {};
			if (assignedTo != "" && parseInt(assignedTo) > 0) {
				action_params['ASSIGNED_TO_ID'] = assignedTo;
			}
			if (assignmentGroup != "" && parseInt(assignmentGroup) > 0) {
				action_params['ASSIGNMENT_GROUP_ID'] = assignmentGroup;
			}
			
			var action = {};
			action.type = 1; //field update type
			action.params = action_params;
			
			var actions = [];
			actions.push(action);
			
			console.log(actions);
			
			var ruleObj = {};
			ruleObj['name'] = ruleName;
 			ruleObj['description'] = ruleDesc;
 			ruleObj['executionOrder'] = executionOrder;
 			ruleObj['isActive'] = isActive;
 			ruleObj['module'] = ruleModule;
 			//ruleObj['rule.actions'] = actions;
			
			$.ajax({
				method : "post",
				url : "<s:url action='addAssignmentRule' />",
				data : ruleObj
			})
			.done(function(data) {
				console.log(data);
			})
			.fail(function(error) {
				console.log(error);
			});
		});
	});
</script> 