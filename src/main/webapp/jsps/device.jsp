<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<script>
	var chart;
	function showAddDevice()
	{
		$.ajax({
		      type: "GET",
		      url: "/bms/home/newDevice",   
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
	
	function enableDeviceMonitoring(el)
	{
		var dataObject = new Object();
		dataObject.deviceId = $(el).parent().parent().attr('id');
		$.ajax({
		      type: "POST",
		      url: "/bms/home/enableDeviceMonitoring",   
		      data: dataObject,
		      success: function (response) {
		    	  $(el).parent().html("<a href='javascript:void(0);' onclick='disableDeviceMonitoring(this);'>Disable</a>");
		      }
		 });
	}
	
	function disableDeviceMonitoring(el)
	{
		var dataObject = new Object();
		dataObject.deviceId = $(el).parent().parent().attr('id');
		$.ajax({
		      type: "POST",
		      url: "/bms/home/disableDeviceMonitoring",   
		      data: dataObject,
		      success: function (response) {
		         $(el).parent().html("<a href='javascript:void(0);' onclick='enableDeviceMonitoring(this);'>Enable</a>");
		      }
		 });
	}
	
	function showDevices(el)
	{
		var dataObject = new Object();
		dataObject.controllerId = $(el).parent().parent().attr('id');
		$.ajax({
		      type: "GET",
		      url: "/bms/home/showDevices",   
		      data: dataObject,
		      success: function (response) {
		    	$('#devicetree').html(response)
		      }
		 });
	}
	
	function showDeviceData(el)
	{
		var dataObject = new Object();
		dataObject.controllerId = $(el).parent().parent().attr('id');
		$.ajax({
		      type: "GET",
		      url: "/bms/home/showDeviceData",   
		      data: dataObject,
		      success: function (response) {
		    	console.log(response);
	    		var chart_json = 
				{
					'data': 
						{
							'x': 'x',
							'columns': 
								[
									response.x,
									response.y
								]
						},
					'axis': 
						{
							'x': 
								{
									'label': 'Time',
									'type': 'timeseries',
									'tick': {'format': '%H:%M:%S'}
								},
							'y': 
								{
									'label': 'kW'
								}
						}
				};
				chart = ChartLibrary.timeseries("#data", chart_json); 
		      }
		 });
	}
	
	WebMessenger.subscribe(3, function(event){ 
		var message = JSON.parse(event.data);
		console.log(message);
		console.log(message.content);
		console.log(message.content.x);
		console.log(message.content.y);
		chart.flow(
			{
        		columns: 
        			[
        				message.content.x,
        				message.content.y
	        		]
    		}
		);
	});
	
</script>

<style>
	#maincontent th, #devicetree th
	{
		border-bottom: 1px solid #ddd;
	    background: #f2f2f2;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
	#maincontent td, #devicetree td
	{
		border-bottom: 1px solid #ddd;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
</style>

<div id="maincontent" style="padding:20px;">
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="float: left;font-size: 18px;padding-top: 3px;">Controllers :</div>
		<div style=" float:right;"><input onclick="showAddDevice();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
		<div style="clear: both;"></div>
	</div>
	<div>
	<table style="width: 800px; border: 1px solid #ddd;">
		<tr>
			<th>Name</th>
			<th>Type</th>
			<th>Poll Time</th>
			<th></th>
			<th></th>
		</tr>
		<s:iterator var="device" value="%{DEVICES}">
			<tr id="<s:property value="#device.id" />">
				<td><s:property value="#device.name" /></td>
				<td><s:property value="#device.type" /></td>
				<td><s:property value="#device.polltime" /></td>
				<td>
					<s:if test="%{#device.status}">
						<a href="javascript:void(0);" onclick="disableDeviceMonitoring(this);">Disable</a>
					</s:if>
					<s:else>
						<a href="javascript:void(0);" onclick="enableDeviceMonitoring(this);">Enable</a>
					</s:else>
				</td>
				<td><a href="javascript:void(0);" onclick="showDevices(this);">Show Devices</a></td>
			</tr>
	    </s:iterator>
	</table>
	</div>
</div>
<div id="devicetree"></div>
<div id="devicedata" style="padding-top:20px;"><div id="data"></div></div>