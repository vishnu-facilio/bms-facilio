<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<script>

	function showAddDevice()
	{
		$.ajax({
		      type: "GET",
		      url: "/bms/home/users/new",   
		      success: function (response) {
		         $('#maincontent').html(response);
		      }
		 });
	}

	function addDevice()
	{
		var dataObject = new Object();
		dataObject.name = $('input[name=deviceName]').val();
		dataObject.type = $('select[name=deviceType]').val();
		dataObject.datasource = $('input[name=datasource]:checked').val();
		dataObject.publicip = $('input[name=publicip]').val();
		dataObject.polltime = $('input[name=polltime]').val();
		$.ajax({
		      type: "POST",
		      url: "/bms/home/addDevice",   
		      data: dataObject,
		      success: function (response) {
		         window.location.reload();
		      }
		 });
	}
	
	function showDeviceData()
	{
		$.ajax({
		      type: "GET",
		      url: "/bms/home/showDeviceData",   
		      success: function (response) {
		         $('#devicedata').html(response);
		      }
		 });
	}

</script>

<style>
	#maincontent th
	{
		border-bottom: 1px solid #ddd;
	    background: #f2f2f2;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
	#maincontent td
	{
		border-bottom: 1px solid #ddd;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
</style>

<div id="maincontent" style="padding:20px;">
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="float: left;font-size: 18px;padding-top: 3px;">Users :</div>
		<div style=" float:right;"><input onclick="showAddDevice();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
		<div style="clear: both;"></div>
	</div>
	<div>
	<table style="width: 800px; border: 1px solid #ddd;">
		<tr>
			<th>Email</th>
			<th>Role</th>
			<th>Status</th>
			<th>Added Time</th>
		</tr>
		<s:iterator var="user" value="%{users}">
			<tr>
				<td><s:property value="#user.email" /></td>
				<td><s:property value="#user.getRoleAsString()" /></td>
				<td><s:property value="#user.getStatusAsString()" /></td>
				<td><s:property value="#user.getInvitedTimeStr()" /></td>
			</tr>
	    </s:iterator>
	</table>
	</div>
</div>
<div id="devicedata" style="padding-top:20px;"></div>