<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div style="padding:15px 0">
	<a class="btn btn-default" href="edit?id=<s:property value="user.userId" />" role="button"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit</a>
</div>
<table class="table">
	<tr>
		<td class="info">Email</td>
		<td><s:property value="user.email" /></td>
	</tr>
	<tr>
		<td class="info">Role</td>
		<td><s:property value="user.getRoleAsString()" /></td>
	</tr>
	<tr>
		<td class="info">Status</td>
		<td><s:property value="user.getStatusAsString()" /></td>
	</tr>
	<tr>
		<td class="info">Created Time</td>
		<td><s:property value="user.getInvitedTimeStr()" /></td>
	</tr>
</table>
<a class="btn btn-default" href="<s:url action="" />" role="button"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back To Users</a>