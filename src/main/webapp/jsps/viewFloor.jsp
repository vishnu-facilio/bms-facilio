<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="floor.name" />
</h3>
<input type="hidden" name="floorId"
	value="<s:property value="floor.floorId" />" />
<table class="table">
	<tr>
		<td class="info">Current Occupancy</td>
		<td><s:property value="floor.currentOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Max Occupancy</td>
		<td><s:property value="floor.maxOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Percent Occupied</td>
		<td><s:property value="floor.percentOccupied" /></td>
	</tr>
	<tr>
		<td class="info">Gross Area</td>
		<td><s:property value="floor.grossArea" /></td>
	</tr>
	<tr>
		<td class="info">Usable Area</td>
		<td><s:property value="floor.usableArea" /></td>
	</tr>
	<tr>
		<td class="info">Assignable Area</td>
		<td><s:property value="floor.assignableArea" /></td>
	</tr>
	<tr>
		<td class="info">Area Unit</td>
		<td><s:property value="floor.areaUnit" /></td>
	</tr>
</table>

<a class="btn btn-default" href="#floor" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Floor</a>
<button class="btn btn-default" id="addSpaceBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	Space
</button>