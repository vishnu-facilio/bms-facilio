<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="facilio-tags" prefix="f" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header"><a href="#device">Devices</a>&nbsp;&nbsp;/&nbsp;&nbsp;<s:property value="%{CONTROLLER_INFO.name}" /></h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
       <h4>Device Tree</h4>
       <f:chart id="eb-meter" type="tree-collapsible" width="600" height="150" url="/home/showTree?controllerId=${requestScope.CONTROLLER_ID}"/>
   </div>
   <!-- /.col-lg-12 -->
</div>
<br/>
<div class="row">
   <div class="col-lg-12">
       <h4>Sub Devices</h4>
       <table width="100%" class="table table-striped table-bordered table-hover" id="sub-devices">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Name</th>
	            <th>Configure</th>
	            <th>Info</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="device" value="%{DEVICES.values}">
	    		<tr class="odd gradeX" id="<s:property value="#device.id" />">
		            <td>#<s:property value="#device.id" /></td>
		            <td><a href="#device/<s:property value="#device.id" />"><s:property value="#device.name" /></a></td>
		            <td>
		            	<s:if test="%{#device.status == 1}">
							<button type="button" onclick="showConfigureDevice(this);" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs">Configure</button>
						</s:if>
						<s:else>
							<button type="button" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs disabled">Already Configured</button>
						</s:else>
		            </td>
		            <td>
		            	<button type="button" onclick="showDeviceInfo(this);" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs">Show Info</button>
		            </td>
		        </tr>
	    	</s:iterator>
	    </tbody>
	</table>
	<!-- /.table-responsive -->
   </div>
   <!-- /.col-lg-12 -->
</div>
<br/>
<div class="row">
   <div class="col-lg-12">
       <h4>Energy Usage</h4>
       <div id="device-energy-usage"></div>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="modal fade" id="configureDeviceModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Configure Device</h4>
	        </div>
	        <div class="modal-body" style="max-height: 500px; overflow: scroll;">
	        	Loading...
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="configureDevice(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<style>
#eb-meter svg {
	border: 1px solid #e7e7e7;
}
</style>
<script>

	function showConfigureDevice(el) {
		
		$("#configureDeviceModel").modal("show");
		$('#configureDeviceModel .modal-body').html("Loading...");
		
		var dataObject = new Object();
		dataObject.deviceId = $(el).attr('device-id');
		$.ajax({
		      type: "POST",
		      url: "/home/showConfigureDevice",   
		      data: dataObject,
		      success: function (response) {
		    	  $('#configureDeviceModel .modal-body').html(response);
		      }
		 });
	}
	
	
	function configureDevice(btn) {
		$(btn).button('loading');
		
		var json = new Object();
		$.each($('tr[hasdata=true]'), function(i) {
			var instanceId = $(this).attr('id');
			var columnName = $(this).find('select').val();
			json[instanceId] = columnName;
		});
		var dataObject = new Object();
		dataObject.deviceId = $('#deviceId').val();
		dataObject.data = JSON.stringify(json);
		$.ajax({
			type : "POST",
			url : "/home/configureDevice",
			data : dataObject,
			success : function(response) {
				$('#newDeviceModel').modal('hide');
				
				setTimeout(function() {
					FacilioApp.refreshView();
	            }, 800);
			}
		});
	}
	
	var chart;
	function showDeviceData(controllerId)
	{
		var dataObject = new Object();
		dataObject.controllerId = controllerId;
		$.ajax({
		      type: "GET",
		      url: "/home/showDeviceData",   
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
				chart = ChartLibrary.timeseries("#device-energy-usage", chart_json); 
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
	
	$(document).ready(function() {
		var controllerId = "${requestScope.CONTROLLER_ID}";
		showDeviceData(controllerId);
	});
</script>