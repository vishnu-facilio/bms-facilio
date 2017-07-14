<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<h3>
	<s:property value="zone.name" />
</h3>
<input type="hidden" name="zoneId"
	value="<s:property value="zone.zoneId" />" />
<table class="table">
	<tr>
		<td class="info">Short Description</td>
		<td><s:property value="zone.shortDescription" /></td>
	</tr>
</table>

<a class="btn btn-default" href="#zone" role="button"><span
	class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back
	To Zone</a>
<button class="btn btn-default" id="addSpaceBtn" role="button">
	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add
	Space
</button>