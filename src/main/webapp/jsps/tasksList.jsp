<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Tasks
       	<a href="#tasks/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Task
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="tasks-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Subject</th>
	            <th>Description</th>
	            <th>Related To</th>
	            <th>Assigned To</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="task" value="tasks">
				<tr class="odd gradeX" id="<s:property value="#task.taskId" />">
		            <td><a href="#tasks/<s:property value="#task.taskId" />">#<s:property value="#task.taskId" /></a></td>
		            <td><s:property value="#task.subject" /></td>
		            <td><s:property value="#task.description" /></td>
		            <td><s:property value="#task.parent" /></td>
		            <td><s:property value="#task.assignedToId" /></td>
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
$('#tasks-list').DataTable({
    responsive: true
});
</script>