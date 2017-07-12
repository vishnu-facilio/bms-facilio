<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="space.name" />
</h3>
<input type="hidden" name="spaceId"
	value="<s:property value="space.spaceId" />" />
<table class="table">
	<tr>
		<td class="info">Current Occupancy</td>
		<td><s:property value="space.currentOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Max Occupancy</td>
		<td><s:property value="space.maxOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Percent Occupied</td>
		<td><s:property value="space.percentOccupied" /></td>
	</tr>
	<tr>
		<td class="info">Area</td>
		<td><s:property value="space.grossArea" /></td>
	</tr>
	<tr>
		<td class="info">Area Unit</td>
		<td><s:property value="space.areaUnit" /></td>
	</tr>
</table>

<a class="btn btn-default" href="#space" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Space</a>
<button class="btn btn-default" id="addUserBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	User
</button>