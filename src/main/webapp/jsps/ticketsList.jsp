<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Work Orders
       	<a href="#ticket/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Work Order
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="tickets-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Subject</th>
	            <th>Description</th>
	            <th>Requester</th>
	            <th>Status</th>
	            <th>Assigned To</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="ticket" value="tickets">
				<tr class="odd gradeX" id="<s:property value="#ticket.ticketId" />">
		            <td><a href="#tickets/<s:property value="#ticket.ticketId" />">#<s:property value="#ticket.ticketId" /></a></td>
		            <td><s:property value="#ticket.subject" /></td>
		            <td><s:property value="#ticket.description" /></td>
		            <td><s:property value="#ticket.requester" /></td>
		            <td>
		            	<h5><span class="label label-success"><s:property value="#ticket.getStatus()" /></span></h5>
		            </td>
		            <td><s:property value="#ticket.agentId" /></td>
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
$('#tickets-list').DataTable({
    responsive: true
});
</script>