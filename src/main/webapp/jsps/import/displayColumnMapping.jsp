
<%@ taglib uri="/struts-tags" prefix="s" %>    


<div class="form-container form-content">


processid <s:property value="%{importprocessid}" />



<form id="processImport" name="processImport" action="/bms/app/import/processImport.action" method="post">
<s:hidden name="importprocessid" />
<s:iterator value="metainfo.columnHeadings" >
<s:set var="columnheading"/>
  <p><s:property/> : <s:select name="metainfo.fieldMapping['%{columnheading}']" list="metainfo.fields" /></p>
</s:iterator>
<input type="submit" name="confirm" value="confirm">
</form>
</div>