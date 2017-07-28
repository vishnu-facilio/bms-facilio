<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
	<table width="100%" class="table table-striped table-bordered table-hover" id="basespace-list">
	    <thead>
	        <tr>
	            <th>Name</th>
	            <th>Type</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="basespace" value="basespaces">
				<tr class="odd gradeX" id="<s:property value="#basespace.id" />">
		            <td>
		            	<input name="basespaceId" type="hidden" value="<s:property value="#basespace.id" />" />
						<input name="basespaceName" type="hidden" value="<s:property value="#basespace.name" />" />
		            	<s:property value="#basespace.name" />
		            </td>
		            <td><s:property value="#basespace.type" /></td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
<script>
$('#basespace-list').DataTable({
    responsive: true
});
$("#basespace-list tbody tr").click( function() {
	FacilioApp.selectValue($(this).find('input[name=basespaceId]').val(), $(this).find('input[name=basespaceName]').val(), $('#basespacepopup'));
});
</script>