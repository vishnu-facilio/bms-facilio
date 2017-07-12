<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Space
       	<a href="#space/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Space
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="space-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Name</th>
	            <th>Current occupancy</th>
	            <th>Max occupancy</th>
	            <th>Percent occupied</th>
	            <th>Area</th>
	            <th>Area unit</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="space" value="spaces">
				<tr class="odd gradeX" id="<s:property value="#space.spaceId" />">
		            <td><a href="#space/<s:property value="#space.spaceId" />">#<s:property value="#space.spaceId" /></a></td>
		            <td><s:property value="#space.name" /></td>
		            <td><s:property value="#space.currentOccupancy" /></td>
		            <td><s:property value="#space.maxOccupancy" /></td>
		            <td><s:property value="#space.percentOccupied" /></td>
		            <td><s:property value="#space.area" /></td>
		            <td><s:property value="#space.areaUnit" /></td>
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
<div class="modal fade" id="newSpaceModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
</div>
<script>
$('#space-list').DataTable({
    responsive: true
});
</script>