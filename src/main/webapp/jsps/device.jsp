<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Devices
       	<button data-toggle="modal" data-target="#newDeviceModel" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Device
       	</button>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-12">
	<div class="panel-body">
		<table width="100%" class="table table-striped table-bordered table-hover" id="devices-list">
	    <thead>
	        <tr>
	            <th>ID</th>
	            <th>Name</th>
	            <th>Type</th>
	            <th>Poll Time</th>
	            <th>Job Status</th>
	            <th></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="device" value="%{DEVICES}">
	    		<tr class="odd gradeX" id="<s:property value="#device.id" />">
		            <td>#<s:property value="#device.id" /></td>
		            <td><a href="#device/<s:property value="#device.id" />"><s:property value="#device.name" /></a></td>
		            <td><s:property value="#device.type" /></td>
		            <td><s:property value="#device.polltime" /></td>
		            <td>
		            	<s:if test="%{#device.status}">
		            		<h5><span class="label label-success">Running</span></h5>
						</s:if>
						<s:else>
							<h5><span class="label label-success">Stopped</span></h5>
						</s:else>
		            </td>
		            <td>
		            	<div class="btn-group">
                            <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                <i class="fa fa-gear"></i> <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                            	<s:if test="%{#device.status}">
									<li><a href="javascript:disableDeviceMonitoring(this);" device-id="<s:property value="#device.id" />">Disable Monitoring</a></li>
								</s:if>
								<s:else>
									<li><a href="javascript:enableDeviceMonitoring(this);" device-id="<s:property value="#device.id" />">Enable Monitoring</a></li>
								</s:else>
								<li><a href="#">Delete</a>
                            </ul>
                        </div>
		            </td>
		        </tr>
	    	</s:iterator>
	    </tbody>
	</table>
	<!-- /.table-responsive -->
	</div>
	</div>
	<div class="modal fade" id="newDeviceModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
		    <div class="modal-content">
		        <div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		            <h4 class="modal-title" id="myModalLabel">New Device</h4>
		        </div>
		        <div class="modal-body">
		            <form role="form" id="newDeviceForm" method="post" onsubmit="return false;">
			            <div class="form-group">
			                <label>Device Name</label>
			                <input class="form-control" name="deviceName">
			                <p class="help-block">Name of the device.</p>
			            </div>
			            <div class="form-group">
			               <label>Device Type</label>
			               <select class="form-control" name="deviceType">
			                   <option value="-">-Select-</option>
			                   <option value="distechcontrols">Distech Controls</option>
			               </select>
			            </div>
			            <div class="form-group">
			                <label>Data Source</label>
			                <div class="radio">
			                    <label>
			                        <input type="radio" name="dataSource" value="public" checked>Public IP
			                    </label>
			                </div>
			                <div class="radio">
			                    <label>
			                        <input type="radio" name="dataSource" value="mqtt">MQTT Client
			                    </label>
			                </div>
			            </div>
			            <div class="form-group">
			                <label>Public IP</label>
			                <input class="form-control" name="publicIp">
			            </div>
			            <div class="form-group">
			                <label>Poll Time</label>
			                <input class="form-control" name="pollTime">
			            </div>
					</form>
		        </div>
		        <div class="modal-footer">
		            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveDevice(this, $('#newDeviceForm'));" class="btn btn-primary">Save</button>
		        </div>
		    </div>
		    <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
</div>
<script>
	
	function saveDevice(btn, form) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/addDevice",
			data : $(form).serialize()
		})
		.done(function(data) {
			$('#newDeviceModel').modal('hide');
			FacilioApp.notifyMessage('success', 'Device created successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
		return false;
		
		//resetting the form
		$(form).trigger('reset');
	}

	function enableDeviceMonitoring(btn)
	{
		var deviceId = $(btn).attr('device-id');
		
		var dataObject = new Object();
		dataObject.deviceId = deviceId;
		$.ajax({
		      type: "POST",
		      url: contextPath + "/bms/home/enableDeviceMonitoring",   
		      data: dataObject,
		      success: function (response) {
		    	  FacilioApp.notifyMessage('success', 'Device monitoring enabled successfully!');
					
				  setTimeout(function() {
						FacilioApp.refreshView();
		            }, 500);
		      }
		 });
	}
	
	function disableDeviceMonitoring(btn)
	{
		var deviceId = $(btn).attr('device-id');
		
		var dataObject = new Object();
		dataObject.deviceId = deviceId;
		$.ajax({
		      type: "POST",
		      url: contextPath + "/bms/home/disableDeviceMonitoring",   
		      data: dataObject,
		      success: function (response) {
		    	  FacilioApp.notifyMessage('success', 'Device monitoring disabled successfully!');
					
				  setTimeout(function() {
						FacilioApp.refreshView();
		            }, 500);
		      }
		 });
	}
	
	$('#devices-list').DataTable({
        responsive: true
    });
</script>