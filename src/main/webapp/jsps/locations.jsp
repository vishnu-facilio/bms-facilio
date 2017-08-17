<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="row">
	<div class="col-lg-12">
   		<table width="100%" class="table table-striped table-hover" id="locations-list">
   			<thead>
   				<tr>
   					<th class="col-md-4 sortable">Name</th>
   					<th class="col-md-2 sortable">Contact</th>
   					<th class="col-md-2 sortable">Phone</th>
   					<th class="col-md-1 sortable">Lat</th>
   					<th class="col-md-1 sortable">Lng</th>
   					<th class="col-md-2 sortable">Actions</th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="location" value="locations">
					<tr class="odd gradeX" id="<s:property value="#location.id" />">
			            <td><a href="#locations/<s:property value="#location.id" />"><s:property value="#location.name" /></a></td>
			            <td><s:property value="#location.contactName" /></td>
			            <td><s:property value="#location.phone" /></td>
			            <td><s:property value="#location.lat" /></td>
			            <td><s:property value="#location.lng" /></td>
			            <td class="text-center">
			            	<div class="btn-group">
	                    		<a type="button" href="https://www.google.com/maps/search/<s:property value="#location.lat" />,<s:property value="#location.lng" />/@<s:property value="#location.lat" />,<s:property value="#location.lng" />,13z" target="_blank" lat="" lng="<s:property value="#location.lng" />" data-toggle="tooltip" data-placement="top" title="Open in Google Maps" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-map-marker"></i>
	                    		</a>
								<button type="button" location-id="<s:property value="#location.id" />" data-toggle="tooltip" data-placement="top" title="Edit location" onclick="editLocation(this);" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-pencil"></i>
	                    		</button>
	                    		<button type="button" location-id="<s:property value="#location.id" />" data-toggle="tooltip" data-placement="top" title="Delete location" onclick="deleteLocation(this);" class="btn btn-outline btn-danger btn-md">
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
		responsive: true,
		lengthChange: false,
		searching: false,
	});

	$('.row').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
	
	$(".action-btn .new-btn").click(function() {
		location.href = '#locations/new';
	});
	
	function editLocation(thisvar)
	{
		var Id = $(thisvar).attr("location-id");
		location.href = '#locations/edit?locationId='+Id;
	}
	
	function deleteLocation(thisvar)
	{
		var Id = $(thisvar).attr("location-id");
		FacilioApp.ajax({
			method : "post",
			url : contextPath + "/app/setup/locations/delete",
			data : {"locationId":Id},
			done: function(data) {
				console.log(data);
				FacilioApp.refreshView();
				FacilioApp.notifyMessage('success', 'Location deleted successfully!');
			},
			fail: function(error) {
				console.log(error);
			} 
		});
	}
	
</script>