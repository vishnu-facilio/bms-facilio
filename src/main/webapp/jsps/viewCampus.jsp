<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="campus.name" />
</h3>
<input type="hidden" name="campusId"
	value="<s:property value="campus.campusId" />" />
<table class="table">
	<tr>
		<td class="info">Current Occupancy</td>
		<td><s:property value="campus.currentOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Max Occupancy</td>
		<td><s:property value="campus.maxOccupancy" /></td>
	</tr>
	<tr>
		<td class="info">Percent Occupied</td>
		<td><s:property value="campus.percentOccupied" /></td>
	</tr>
	<tr>
		<td class="info">Gross Area</td>
		<td><s:property value="campus.grossArea" /></td>
	</tr>
	<tr>
		<td class="info">Usable Area</td>
		<td><s:property value="campus.usableArea" /></td>
	</tr>
	<tr>
		<td class="info">Assignable Area</td>
		<td><s:property value="campus.assignableArea" /></td>
	</tr>
	<tr>
		<td class="info">Area Unit</td>
		<td><s:property value="campus.areaUnit" /></td>
	</tr>
</table>

<a class="btn btn-default" href="#campus" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Campus</a>
<button class="btn btn-default" id="addBuildingBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	Building
</button>