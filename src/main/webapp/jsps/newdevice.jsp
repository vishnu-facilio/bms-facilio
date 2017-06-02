<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<div style="padding:20px;">
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Device Name</div>
		<div style=" float:left;"><input name="deviceName" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Device Type</div>
		<div style=" width:150px;float:left;">
			<select style="width:150px; height:28px;" name="deviceType">
				<option value="-">-Select-</option>
				<option value="distechcontrols">Distech Controls</option>
			</select>
		</div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px;  float:left;">Data Source</div>
		<div style=" float:left;">
			<input type="radio" name="datasource" value="public" checked /> Public IP
	  		<input type="radio" name="datasource" value="mqtt" /> MQTT Client<br>
  		</div>
  		<div style="clear: both;"></div>
  	</div>
  	<div style="padding-bottom:20px;padding-top: 7px;">
		<div style="width:200px; float:left;">Public IP</div>
		<div style=" float:left;"><input name="publicip" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Poll Time</div>
		<div style=" float:left;"><input name="polltime" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style=" float:left;padding-left:200px;"><input onclick="addDevice();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
		<div style="clear: both;"></div>
	</div>
</div>