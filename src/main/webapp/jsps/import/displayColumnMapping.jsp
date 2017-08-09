
<%@ taglib uri="/struts-tags" prefix="s" %>    


<div class="form-container form-content">


processid <s:property value="%{metainfo.columnHeadings}" />



<form id="processImport" name="processImport" onsubmit="return false;" method="post">
<s:hidden name="metainfo.importprocessid" />
<s:iterator var="field" value="metainfo.fields" >
  <p><s:property/> : <s:select name="metainfo.fieldMapping['%{field}']" list="metainfo.columnHeadings" headerKey="-1" headerValue="Select Import field"/></p>
</s:iterator>
<input type="submit" value="confirm">
</form>
</div>
<script>
	$('#processImport').submit(function() {
		
		FacilioApp.ajax({
			method : "post",
			url : contextPath + "/app/import/processImport",
			data : $(this).serialize(),
			success: function(data) {
				FacilioApp.notifyMessage('success', 'Data imported successfully!');
				FacilioApp.refreshView();
			},
			error: function(error) {
				alert(error);
			}
		});
		return false;
	});
</script>