<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="building-list">
	    <thead>
	        <tr>
	            <th>Name</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="building" value="buildings">
				<tr class="odd gradeX">
		            <td>
		            	<input name="buildingId" type="hidden" value="<s:property value="#building.buildingId" />" />
						<input name="buildingName" type="hidden" value="<s:property value="#building.name" />" />
		            <s:property value="#building.name" />
		            </td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
<script>
$('#building-list').DataTable({
    responsive: true
});
$("#building-list tbody tr").click( function() {
	FacilioApp.selectValue($(this).find('input[name=buildingId]').val(), $(this).find('input[name=buildingName]').val(), $('#buildingpopup'));
});
</script>