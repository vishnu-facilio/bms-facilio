<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Building
       	<a href="#building/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Building
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="building-list">
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
	    	<s:iterator var="building" value="buildings">
				<tr class="odd gradeX" id="<s:property value="#building.buildingId" />">
		            <td><a href="#building/<s:property value="#building.buildingId" />">#<s:property value="#building.buildingId" /></a></td>
		            <td><s:property value="#building.name" /></td>
		            <td><s:property value="#building.currentOccupancy" /></td>
		            <td><s:property value="#building.maxOccupancy" /></td>
		            <td><s:property value="#building.percentOccupied" /></td>
		            <td><s:property value="#building.assignableArea" /></td>
		            <td><s:property value="#building.usableArea" /></td>
		            <td><s:property value="#building.grossArea" /></td>
		            <td><s:property value="#building.areaUnit" /></td>
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
<div class="modal fade" id="newBuildingModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
</div>
<script>
$('#building-list').DataTable({
    responsive: true
});
</script>