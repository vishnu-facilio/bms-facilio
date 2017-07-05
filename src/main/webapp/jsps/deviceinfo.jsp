<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="facilio-tags" prefix="f" %> 

<style>
ul {
    vertical-align:top;
    list-style:none;
    padding-left:0px;
}
li {
    border-bottom:1px dotted Gray;
    cursor: move;
}
li.selected {
    background:#fcf8e3;
}

</style>
   
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	<a href="#controller">Controllers</a>&nbsp;&nbsp;/&nbsp;&nbsp;<s:property value="%{CONTROLLER_INFO.name}" />
       	<button data-toggle="modal" data-target="#newDeviceModel" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Device
       	</button>
       </h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row" style="float:left; width:32%;">
   <div class="col-lg-12">
       <h4 style="padding-bottom: 15px;">Unmodelled Instances</h4>
       	<div id="instances" style=" border:1px solid #ddd;">
		<div style="padding:10px;background:#e1e1e1; border-bottom:1px solid #ddd; ">
			<div style="float:left;">Name</div>
			<div style="float:right;"><button type="button" onclick="selectAll();" class="btn btn-outline btn-primary btn-xs">Select All</button></div>
			<div style="clear:both;"></div>
		</div>
		<ul>
		<s:iterator var="instance" value="%{INSTANCES}">
			<li instanceId="<s:property value="#instance.controllerInstanceId" />" style="padding:10px;border-bottom:1px solid #ddd; "><s:property value="#instance.instanceName" /></li>
	    </s:iterator>
	    </ul>
		</div>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row" style="float:left; width:50%;  margin-left:20px;">
   <div class="col-lg-12">
       <div style="float:left;"><h4>Devices</h4></div>
       <s:if test="%{!DEVICES.values.isEmpty()}">
       <div onclick="$('#accordion').toggle(); $('#devicetree').toggle();" style="cursor:pointer;float: right;padding: 15px;padding-right: 65px;"><i class="fa fa-sitemap" aria-hidden="true"></i></div>
       </s:if>
       <div style="clear:both;"></div>
       	<s:if test="%{DEVICES.values.isEmpty()}">
		<div id="nodevicefound" style="text-align:center;">No devices found</div>
		</s:if>
		<s:else>
		<div id="accordion">
		<s:iterator var="device" value="%{DEVICES.values}">
		<div deviceId="<s:property value="#device.id" />" style="outline:none; margin-top:10px; border:1px solid #ddd; width:402px; padding:10px; background:#ddd;">
			<div style="float:left;"><s:property value="#device.name" /></div>
			<div style="float:right;">
			<s:if test="%{#device.status == 1}">
				<button type="button" onclick="showConfigureDevice(this);" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs">Configure</button>
			</s:if>
			<s:else>
				<button type="button" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs disabled">Already Configured</button>
			</s:else>
			<button type="button" onclick="showDeviceInfo(this);" device-id="<s:property value="#device.id" />" class="btn btn-outline btn-primary btn-xs">Show Info</button>
			</div>
			<div style="clear:both;"></div>
		</div>
		<div style=" border:1px solid #ddd; width:402px; margin-bottom:10px;">
			<ul style="overflow:scroll;height:200px; width:400px;">
			<s:iterator var="instance" value="#device.instances">
				<li instanceId="<s:property value="#instance.controllerInstanceId" />" style="padding:10px;border-bottom:1px solid #ddd; "><s:property value="#instance.instanceName" /></li>
			</s:iterator>
			</ul>
		</div>
		</s:iterator>
		</div>
		<div id="devicetree" style="display:none;">
			<f:chart id="eb-meter" type="tree-collapsible" width="400" height="450" url="${pageContext.request.contextPath}/home/showTree?controllerId=${requestScope.CONTROLLER_ID}"/>
		</div>
		</s:else>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div style="clear:both;"></div>
<br/>
<div class="row" style="display:none;">
   <div class="col-lg-12">
       <h4>Energy Usage</h4>
       <div id="device-energy-usage"></div>
   </div>
   <!-- /.col-lg-12 -->
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
		                <input type="hidden" class="form-control" name="controllerId" value="<s:property value="%{CONTROLLER_ID}" />">
		                <p class="help-block">Name of the device.</p>
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
	
	function saveDevice(btn, form) 
	{
		//$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/addDevice",
			data : $(form).serialize()
		})
		.done(function(data) {
			$('#newDeviceModel').modal('hide');
			FacilioApp.notifyMessage('success', 'Controller created successfully!');
			
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

	function addDevice(el)
	{
		var dataObject = new Object();
		dataObject.name = $(el).parent().parent().parent().find('input[name=deviceName]').val();
		dataObject.controllerId = $(el).parent().parent().parent().find('input[name=controllerId]').val();
		$.ajax({
		      type: "POST",
		      url: contextPath + "/home/addDevice",   
		      data: dataObject,
		      success: function (response) {
		    	  showControllerDevices($(el).parent().parent().parent().find('input[name=controllerId]').val());
		      }
		 });
	}
	
	function showConfigureDevice(el) {
		
		$("#configureDeviceModel").modal("show");
		$('#configureDeviceModel .modal-body').html("Loading...");
		
		var dataObject = new Object();
		dataObject.deviceId = $(el).attr('device-id');
		$.ajax({
		      type: "POST",
		      url: contextPath + "/home/showConfigureDevice",   
		      data: dataObject,
		      success: function (response) {
		    	  $('#configureDeviceModel .modal-body').html(response);
		      }
		 });
	}
	
	function selectAll() {
		
	}
	
	
	function configureDevice(btn) {
		//$(btn).button('loading');
		
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
			url : contextPath + "/home/configureDevice",
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
		      url: contextPath + "/home/showDeviceData",   
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
		//showDeviceData(controllerId);
		
		$( document ).ready(function() {
			$("ul").on('click', 'li', function (e) {
			    if (e.ctrlKey || e.metaKey) {
			        $(this).toggleClass("selected");
			    } else {
			        $(this).addClass("selected").siblings().removeClass('selected');
			    }
			}).sortable({
			    connectWith: "ul",
			    delay: 150, //Needed to prevent accidental drag when trying to select
			    revert: 0,
			    helper: function (e, item) {
			        //Basically, if you grab an unhighlighted item to drag, it will deselect (unhighlight) everything else
			        if (!item.hasClass('selected')) {
			            item.addClass('selected').siblings().removeClass('selected');
			        }
			        
			        //////////////////////////////////////////////////////////////////////
			        //HERE'S HOW TO PASS THE SELECTED ITEMS TO THE `stop()` FUNCTION:
			        
			        //Clone the selected items into an array
			        var elements = item.parent().children('.selected').clone();
			        
			        //Add a property to `item` called 'multidrag` that contains the 
			        //  selected items, then remove the selected items from the source list
			        item.data('multidrag', elements).siblings('.selected').remove();
			        
			        //Now the selected items exist in memory, attached to the `item`,
			        //  so we can access them later when we get to the `stop()` callback
			        
			        //Create the helper
			        var helper = $('<li/>');
			        return helper.append(elements);
			    },
			    stop: function (e, ui) {
			        //Now we access those items that we stored in `item`s data!
			        var elements = ui.item.data('multidrag');
			        
			        var deviceId = $(ui.item).parent().parent().prev().attr('deviceId');
			        var json = new Array();
			        var dataObject = new Object();
			        if(deviceId != undefined)
		        	{
			        	dataObject.deviceId = deviceId;
		        	}
		        	$(elements).parent().find('li').each(function (i){
		        		var instanceId = $(this).attr("instanceId");
		        		console.log(instanceId);
		        		json[i] = instanceId;
		        	});
		        	
		    		dataObject.instances = JSON.stringify(json);
		    		dataObject.controllerId = $('input[name=controllerId]').val();
		    		$.ajax({
		  		      type: "POST",
		  		      url: contextPath + "/home/updateControllerInstances",   
		  		      data: dataObject,
		  		      success: function (response) {
		  		         
		  		      }
		  		 	});
			        
			        //`elements` now contains the originally selected items from the source list (the dragged items)!!
			        
			        //Finally I insert the selected items after the `item`, then remove the `item`, since 
			        //  item is a duplicate of one of the selected items.
			        ui.item.after(elements).remove();
			    }

			});
			$( "#accordion" ).accordion();
		});
	});
</script>