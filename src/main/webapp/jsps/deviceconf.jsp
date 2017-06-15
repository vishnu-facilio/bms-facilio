<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="facilio-tags" prefix="f" %> 
   
<script>

function configureDevice()
{
	var json = new Object();
	$.each( $('tr[hasdata=true]'), function(i) {
		var instanceId = $(this).attr('id');
		var columnName = $(this).find('select').val();
		json[instanceId] = columnName;
	});
	var dataObject = new Object();
	dataObject.deviceId = $('#deviceId').val();
	dataObject.data = JSON.stringify(json);
	$.ajax({
	      type: "POST",
	      url: "/bms/home/configureDevice",   
	      data: dataObject,
	      success: function (response) {
	    	  $('#deviceconf').html("");
	      }
	 });
}

</script>
<div>
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="font-size: 18px;padding-top: 3px;">Configuration :</div>
	</div>
	<div>
	<input id="deviceId" type="hidden" value="<s:property value="%{DEVICEID}" />" />
	<table style="width: 300px; border: 1px solid #ddd;">
		<tr>
			<th>Attribute</th>
			<th>Mapping Attribute</th>
		</tr>
		<s:iterator var="deviceinstance" value="%{DEVICEINSTANCES}">
			<tr hasdata="true" id="<s:property value="#deviceinstance.instanceId" />">
				<td><s:property value="#deviceinstance.instancename" /></td>
				<td>
					<select>
						<option>-Select-</option>
						<s:iterator var="attribute" value="%{ATTRIBUTES}">
							<option value="<s:property value="#attribute.key" />"><s:property value="#attribute.value" /></option>
						</s:iterator>
					</select>
				</td>
			</tr>
	    </s:iterator>
	</table>
	</div>
	<div style="padding-bottom:20px;">
		<div style="float:left;padding-top: 20px;padding-left: 110px;"><input onclick="configureDevice();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Save" /></div>
		<div style="clear: both;"></div>
	</div>
</div>