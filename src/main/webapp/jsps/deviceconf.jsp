<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
 <form role="form" id="configureDeviceForm" method="post" onsubmit="return false;">
 	<input id="deviceId" type="hidden" value="<s:property value="%{DEVICEID}" />" />
 	<table width="100%" class="table table-striped table-bordered table-hover" id="sub-devices">
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
</form>