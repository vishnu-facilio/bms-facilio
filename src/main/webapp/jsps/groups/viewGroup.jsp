<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<style>
	#maincontent th
	{
		border-bottom: 1px solid #ddd;
	    background: #f2f2f2;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
	#maincontent td
	{
		border-bottom: 1px solid #ddd;
	    padding-left: 15px;
	    height: 30px;
	    font-size: 13px;
	}
</style>
<div style="padding:15px 0">
	<a class="btn btn-default" href="<s:url action="" />" role="button"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Back To Groups</a>
	<a class="btn btn-default" href="edit?id=<s:property value="group.groupId" />" role="button"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit</a>
</div>
<table class="table">
	<tr>
		<td class="info">Name</td>
		<td><s:property value="group.name" /></td>
	</tr>
	<tr>
		<td class="info">Email</td>
		<td><s:property value="group.email" /></td>
	</tr>
	<tr>
		<td class="info">Description</td>
		<td><s:property value="group.description" /></td>
	</tr>
	<tr>
		<td class="info">Status</td>
		<td><s:property value="group.getStatusAsString()" /></td>
	</tr>
	<tr>
		<td class="info">Created By</td>
		<td><s:property value="group.getCreatedByUser().getEmail()" /></td>
	</tr>
	<tr>
		<td class="info">Created Time</td>
		<td><s:property value="group.getFormattedCreatedTime()" /></td>
	</tr>
</table>
<div id="maincontent">
	<h2>Members</h2>
	<table style="width: 800px; border: 1px solid #ddd;">
		<tr>
			<th>Email</th>
			<th>Group Member Role</th>
			<th></th>
		</tr>
		<s:iterator var="member" value="%{members}">
			<tr>
				<td>
					<s:property value="#member.getEmail()" />
				</td>
				<td>
					<s:property value="#member.getMemberRoleAsString()" />
				</td>
			</tr>
	    </s:iterator>
	</table>
</div>