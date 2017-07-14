<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="campus-list">
	    <thead>
	        <tr>
	            <th>Name</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="campus" value="campuses">
				<tr class="odd gradeX">
		            <td>
			            <input name="campusId" type="hidden" value="<s:property value="#campus.campusId" />" />
						<input name="campusName" type="hidden" value="<s:property value="#campus.name" />" />
		            	<s:property value="#campus.name" />
		            </td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
<script>
$('#campus-list').DataTable({
    responsive: true
});

$("#campus-list tbody tr").click( function() {
	FacilioApp.selectValue($(this).find('input[name=campusId]').val(), $(this).find('input[name=campusName]').val(), $('#campuspopup'));
});
</script>