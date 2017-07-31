<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="facilio-tags" prefix="f" %> 
   
<script>

function showConfigureDevice(el)
{
	var dataObject = new Object();
	dataObject.deviceId = $(el).parent().parent().attr('deviceId');
	FacilioApp.ajax({
	      type: "POST",
	      url: "/bms/app/showConfigureDevice",   
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
	FacilioApp.ajax({
	      type: "POST",
	      url: "/bms/app/showDeviceInfo",   
	      data: dataObject,
	      success: function (response) {
	    	  $('#deviceinfo').html(response);
	      }
	 });
}

function test(obj)
{
	console.log(obj);
}

function test2(obj)
{
	//console.log(obj);
}

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
    		FacilioApp.ajax({
  		      type: "POST",
  		      url: "/bms/app/updateControllerInstances",   
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

</script>
<style>
#devicecontainer ul {
    width:200px;
    display:inline-block;
    vertical-align:top;
    list-style:none;
    padding-left:0px;
}
#devicecontainer li {
    border-bottom:1px dotted Gray;
    cursor: move;
}
#devicecontainer li.selected {
    background:#fcf8e3;
}

</style>
<div id="devicecontainer">
<div style="padding:20px;float:left;">
	<div style="padding-bottom: 20px;">
		<div style="font-size: 18px;padding-top: 3px;">Unmodelled Instances :</div>
	</div>
	<div id="instances" style=" border:1px solid #ddd; width:202px;">
		<div style="padding:10px;background:#e1e1e1; border-bottom:1px solid #ddd; ">Name</div>
		<ul>
		<s:iterator var="instance" value="%{INSTANCES}">
			<li instanceId="<s:property value="#instance.controllerInstanceId" />" style="padding:10px;border-bottom:1px solid #ddd; "><s:property value="#instance.instanceName" /></li>
	    </s:iterator>
	    </ul>
	</div>
</div>
<div style="padding:20px;float:left; width:440px;">
	<div style="padding-bottom: 10px;">
		<div style="float: left;font-size: 18px;padding-top: 3px;">Devices :</div>
		<div style=" float:right;"><input onclick="$('#nodevicefound').hide();$('#deviceadd').show();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
		<div style="clear: both;"></div>
	</div>
	<div id="deviceadd" style="display:none;">
		<input type="hidden" name="controllerId" value="<s:property value="%{CONTROLLERID}" />" />
		<div style="padding-bottom:10px;">
			<div style="width:200px; float:left;">Device Name</div>
			<div style=" float:left;"><input name="deviceName" value="" /></div>
			<div style="clear: both;"></div>
		</div>
		<div style="padding-bottom:10px;">
			<div style=" float:left;padding-left:200px;"><input onclick="addDevice(this);" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
			<div style="clear: both;"></div>
		</div>
	</div>
	<s:if test="%{DEVICES.values.isEmpty()}">
		<div id="nodevicefound" style="text-align:center;">No devices found</div>
	</s:if>
	<s:else>
	<div id="accordion">
	<s:iterator var="device" value="%{DEVICES.values}">
	<div deviceId="<s:property value="#device.id" />" style="outline:none; margin-top:10px; border:1px solid #ddd; width:402px; padding:10px; background:#ddd;">
		<div style="float:left;"><s:property value="#device.name" /></div>
		<div style="float:right; padding-right:20px;">
		<s:if test="%{#device.status == 1}">
			<a href="javascript:void(0);" onclick="showConfigureDevice(this);">Configure</a>
		</s:if>
		<s:else>
			<div style="color:green;">Configured</div>
		</s:else>
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
	</s:else>
</div>
<div style="float:left;">
	<div style="padding:20px; width:402px;">
	<f:chart onmove="test" onclick="test2" id="eb-meter" type="tree-collapsible" width="402" height="150" url="/bms/app/showTree?controllerId=${param.controllerId}"/>
	</div>
</div>	
<div id="deviceconf" style="padding:20px; float:left; width:400px;"></div>
<div style="clear:both"></div>
</div>