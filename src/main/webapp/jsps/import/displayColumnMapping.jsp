
<%@ taglib uri="/struts-tags" prefix="s" %>    





processid <s:property value="%{importprocessid}" />



<form id="processImport" name="processImport" action="/bms/app/import/processImport.action" method="post">
<s:iterator value="metainfo.columnheadings">
  <p><s:property/> : <s:select list="metainfo.fields" /></p>
</s:iterator>
<input type="button" name="confirm" value="confirm">
</form>