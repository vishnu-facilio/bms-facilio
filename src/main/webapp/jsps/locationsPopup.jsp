<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
	<div class="col-lg-12">
   		<table width="100%" class="table table-striped table-bordered table-hover" id="locations-list">
   			<thead>
   				<tr>
   					<th class="col-md-4 sortable">Name</th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="location" value="locations">
					<tr class="odd gradeX">
			            <td>
			            	<input name="locationId" type="hidden" value="<s:property value="#location.locationId" />" />
			            	<input name="locationName" type="hidden" value="<s:property value="#location.name" />" />
			            	<a href="javascript:void(0);"><s:property value="#location.name" /></a>
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
	
	$("#locations-list tbody tr").click( function() {
		FacilioApp.selectValue($(this).find('input[name=locationId]').val(), $(this).find('input[name=locationName]').val(), $('#locationspopup'));
	});
</script>