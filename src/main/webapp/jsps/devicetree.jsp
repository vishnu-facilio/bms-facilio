<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="facilio-tags" prefix="f" %> 
   
<script>

function showConfigureDevice(el)
{
	var dataObject = new Object();
	dataObject.deviceId = $(el).parent().parent().attr('id');
	$.ajax({
	      type: "POST",
	      url: "/bms/home/showConfigureDevice",   
	      data: dataObject,
	      success: function (response) {
	    	  $('#deviceconf').html(response);
	      }
	 });
}

function showDeviceInfo(el)
{
	var dataObject = new Object();
	dataObject.deviceId = $(el).parent().parent().attr('id');
	$.ajax({
	      type: "POST",
	      url: "/bms/home/showDeviceInfo",   
	      data: dataObject,
	      success: function (response) {
	    	  $('#deviceinfo').html(response);
	      }
	 });
}

</script>

<div style="padding:20px; width:350px;">
<f:chart id="eb-meter" type="tree-collapsible" width="300" height="150" url="/bms/home/showTree?controllerId=${param.controllerId}"/>
</div>
<div style="padding:20px; float:left; width:350px;">
	<div>
		<div style="padding-bottom: 20px;width: 800px;">
			<div style="font-size: 18px;padding-top: 3px;">Devices :</div>
		</div>
		<div>
		<table style="width: 300px; border: 1px solid #ddd;">
			<tr>
				<th>Name</th>
				<th></th>
				<th></th>
			</tr>
			<s:iterator var="device" value="%{DEVICES.values}">
				<tr id="<s:property value="#device.id" />">
					<td><s:property value="#device.name" /></td>
					<td>
						<s:if test="%{#device.status == 1}">
							<a href="javascript:void(0);" onclick="showConfigureDevice(this);">Configure</a>
						</s:if>
						<s:else>
							Configured
						</s:else>
					</td>
					<td><a href="javascript:void(0);" onclick="showDeviceInfo(this);">Info</a></td>
				</tr>
		    </s:iterator>
		</table>
		</div>
	</div>
</div>
<div id="deviceconf" style="padding:20px; float:left; width:400px;"></div>
<div id="deviceinfo" style="padding:20px; float:left; width:400px;"></div>
<div style="clear:both"></div>