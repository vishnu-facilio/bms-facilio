<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Zone
       	<a href="#zone/new" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Zone
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="zone-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Name</th>
	            <th>Short Description</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="zone" value="zones">
				<tr class="odd gradeX" id="<s:property value="#zone.zoneId" />">
		            <td><a href="#zone/<s:property value="#zone.zoneId" />">#<s:property value="#zone.zoneId" /></a></td>
		            <td><s:property value="#zone.name" /></td>
		            <td><s:property value="#zone.shortDescription" /></td>
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
<div class="modal fade" id="newZoneModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
</div>
<script>
$('#zone-list').DataTable({
    responsive: true
});
</script>