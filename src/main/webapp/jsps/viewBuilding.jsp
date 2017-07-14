<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="building.name" />
</h3>
<input type="hidden" name="buildingId"
	value="<s:property value="building.buildingId" />" />
<table class="table">
	<tr>
		<td class="info">Current Occupancy</td>
		<td><s:property value="building.currentOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Max Occupancy</td>
		<td><s:property value="building.maxOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Percent Occupied</td>
		<td><s:property value="building.percentOccupied" /></td>
	</tr>
	<tr>
		<td class="info">Gross Area</td>
		<td><s:property value="building.grossArea" /></td>
	</tr>
	<tr>
		<td class="info">Usable Area</td>
		<td><s:property value="building.usableArea" /></td>
	</tr>
	<tr>
		<td class="info">Assignable Area</td>
		<td><s:property value="building.assignableArea" /></td>
	</tr>
	<tr>
		<td class="info">Area Unit</td>
		<td><s:property value="building.areaUnit" /></td>
	</tr>
</table>

<a class="btn btn-default" href="#building" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Building</a>
<button class="btn btn-default" id="addFloorBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	Floor
</button>