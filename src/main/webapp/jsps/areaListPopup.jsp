<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="area-list">
	    <thead>
	        <tr>
	            <th>Name</th>
	            <th>Type</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="area" value="areas">
				<tr class="odd gradeX" id="<s:property value="#area.areaId" />">
		            <td>
		            	<input name="areaId" type="hidden" value="<s:property value="#area.areaId" />" />
						<input name="areaName" type="hidden" value="<s:property value="#area.name" />" />
		            	<s:property value="#area.name" />
		            </td>
		            <td><s:property value="#area.type" /></td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
<script>
$('#area-list').DataTable({
    responsive: true
});
$("#area-list tbody tr").click( function() {
	FacilioApp.selectValue($(this).find('input[name=areaId]').val(), $(this).find('input[name=areaName]').val(), $('#areapopup'));
});
</script>