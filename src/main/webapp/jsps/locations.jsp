<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Locations
       	<a href="#locations/create" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New
       	</a>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-lg-12">
   		<table width="100%" class="table table-striped table-bordered table-hover" id="locations-list">
   			<thead>
   				<tr>
   					<th class="col-md-4 sortable">Name</th>
   					<th class="col-md-2 sortable">Contact</th>
   					<th class="col-md-2 sortable">Phone</th>
   					<th class="col-md-1 sortable">Lat</th>
   					<th class="col-md-1 sortable">Lng</th>
   					<th class="col-md-2 sortable"></th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="location" value="locations">
					<tr class="odd gradeX" id="<s:property value="#location.locationId" />">
			            <td><a href="#locations/<s:property value="#location.locationId" />"><s:property value="#location.name" /></a></td>
			            <td><s:property value="#location.contact" /></td>
			            <td><s:property value="#location.phone" /></td>
			            <td><s:property value="#location.lat" /></td>
			            <td><s:property value="#location.lng" /></td>
			            <td class="text-center">
			            	<div class="btn-group">
	                    		<a type="button" href="https://www.google.com/maps/search/<s:property value="#location.lat" />,<s:property value="#location.lng" />/@<s:property value="#location.lat" />,<s:property value="#location.lng" />,13z" target="_blank" lat="" lng="<s:property value="#location.lng" />" data-toggle="tooltip" data-placement="top" title="Open in Google Maps" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-map-marker"></i>
	                    		</a>
								<button type="button" location-id="<s:property value="#location.locationId" />" data-toggle="tooltip" data-placement="top" title="Edit location" onclick="editLocation(this);" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-pencil"></i>
	                    		</button>
	                    		<button type="button" location-id="<s:property value="#location.locationId" />" data-toggle="tooltip" data-placement="top" title="Delete location" onclick="deleteLocation(this);" class="btn btn-outline btn-danger btn-md">
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
<script>
	$("#locations-list").dataTable({
		responsive: true
	});

	$('.row').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
</script>