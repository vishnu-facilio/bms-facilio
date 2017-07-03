<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Assignment Rules
       	<a href="#rules/newAssignmentRule" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-lg-12">
   		<table width="100%" class="table table-striped table-bordered table-hover" id="rules-list">
   			<thead>
   				<tr>
   					<th class="col-md-2 sortable">Module</th>
   					<th class="col-md-3 sortable">Name</th>
   					<th class="col-md-2 sortable">Order</th>
   					<th class="col-md-2 sortable">Status</th>
   					<th class="col-md-1 sortable"></th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="rule" value="rules">
					<tr class="odd gradeX" id="<s:property value="#rule.ruleId" />">
						<td><s:property value="#rule.getModule()" /></td>
			            <td><a href="#rules/<s:property value="#rule.ruleId" />"><s:property value="#rule.name" /></a></td>
			            <td><s:property value="#rule.executionOrder" /></td>
			            <td>
			            	<s:if test="%{#rule.isActive()}">
								<h5><span class="label label-success">Active</span></h5>
							</s:if>
							<s:else>
								<h5><span class="label label-danger">Inactive</span></h5>
							</s:else>
			            </td>
			            <td>
			            	<div class="btn-group">
	                            <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
	                                <i class="fa fa-gear"></i> <span class="caret"></span>
	                            </button>
	                            <ul class="dropdown-menu" role="menu">
	                            	<li><a href="#">Clone</a>
	                            	<li><a href="#">Edit</a>
									<li><a href="#">Delete</a>
	                            </ul>
	                        </div>
			            </td>
			        </tr>
				</s:iterator>
             </tbody>
      </table>
  </div>
</div>
<script>
	$("#rules-list").dataTable({
		responsive: true
	});

	$('.row').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
</script>