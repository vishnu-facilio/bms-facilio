<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Floor
       	<a href="#floor/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Floor
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="floor-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Name</th>
	            <th>Current occupancy</th>
	            <th>Max occupancy</th>
	            <th>Percent occupied</th>
	            <th>Assignable area</th>
	            <th>Usable area</th>
	            <th>Gross area</th>
	            <th>Area unit</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="floor" value="floors">
				<tr class="odd gradeX" id="<s:property value="#floor.floorId" />">
		            <td><a href="#floor/<s:property value="#floor.floorId" />">#<s:property value="#floor.floorId" /></a></td>
		            <td><s:property value="#floor.name" /></td>
		            <td><s:property value="#floor.currentOccupancy" /></td>
		            <td><s:property value="#floor.maxOccupancy" /></td>
		            <td><s:property value="#floor.percentOccupied" /></td>
		            <td><s:property value="#floor.assignableArea" /></td>
		            <td><s:property value="#floor.usableArea" /></td>
		            <td><s:property value="#floor.grossArea" /></td>
		            <td><s:property value="#floor.areaUnit" /></td>
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
<div class="modal fade" id="newFloorModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
</div>
<script>
$('#floor-list').DataTable({
    responsive: true
});
</script>