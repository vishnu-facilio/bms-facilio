<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="floor-list">
	    <thead>
	        <tr>
	            <th>Name</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="floor" value="floors">
				<tr class="odd gradeX" id="<s:property value="#floor.floorId" />">
		            <td>
		            	<input name="floorId" type="hidden" value="<s:property value="#floor.floorId" />" />
						<input name="floorName" type="hidden" value="<s:property value="#floor.name" />" />
		            	<s:property value="#floor.name" />
		            </td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
<script>
$('#floor-list').DataTable({
    responsive: true
});
$("#floor-list tbody tr").click( function() {
	FacilioApp.selectValue($(this).find('input[name=floorId]').val(), $(this).find('input[name=floorName]').val(), $('#floorpopup'));
});
</script>