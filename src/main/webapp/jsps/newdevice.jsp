<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<div style="padding:20px;">
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="font-size: 18px;padding-top: 3px;">Add Controller :</div>
	</div>	
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Name</div>
		<div style=" float:left;"><input name="controllerName" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Type</div>
		<div style=" width:150px;float:left;">
			<select style="width:150px; height:28px;" name="controllerType">
				<option value="-">-Select-</option>
				<option value="1">Distech Controls</option>
				<option value="2">Linux</option>
			</select>
		</div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px;  float:left;">Data Source</div>
		<div style=" float:left;">
			<input type="radio" name="datasource" onclick="$(this).parent().parent().next().show();" value="public" checked /> Public IP
	  		<input type="radio" name="datasource" onclick="$(this).parent().parent().next().hide();" value="mqtt" /> MQTT Client<br>
  		</div>
  		<div style="clear: both;"></div>
  	</div>
  	<div style="padding-bottom:20px;padding-top: 7px;">
		<div style="width:200px; float:left;">Public IP</div>
		<div style=" float:left;"><input name="publicip" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style="width:200px; float:left;">Time Interval</div>
		<div style=" float:left;"><input name="timeinterval" value="" /></div>
		<div style="clear: both;"></div>
	</div>
	<div style="padding-bottom:20px;">
		<div style=" float:left;padding-left:200px;"><input onclick="addController();" style="width: 70px;font-size: 13px;padding: 5px;" type="button" name="submit" value="Add" /></div>
		<div style="clear: both;"></div>
	</div>
</div>