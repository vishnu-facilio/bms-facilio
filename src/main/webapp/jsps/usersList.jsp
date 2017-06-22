<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

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
<script>
	$(document).ready(function() {
		$(".fc-admin a").trigger( "click" );
	});
</script>
<div id="maincontent" style="padding:20px;">
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="float: left;font-size: 18px;padding-top: 3px;">Users :</div>
		<div style=" float:right;"><a class="btn btn-default" href="/home/users/invite" role="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Invite User</a></div>
		<div style="clear: both;"></div>
	</div>
	<div>
	<table style="width: 800px; border: 1px solid #ddd;">
		<tr>
			<th>Email</th>
			<th>Role</th>
			<th>Status</th>
			<th>Added Time</th>
			<th></th>
		</tr>
		<s:iterator var="user" value="%{users}">
			<tr>
				<td><a href="<s:property value="#user.userId" />"><s:property value="#user.email" /></a></td>
				<td><s:property value="#user.getRoleAsString()" /></td>
				<td><s:property value="#user.getStatusAsString()" /></td>
				<td><s:property value="#user.getInvitedTimeStr()" /></td>
				<td>
					<s:if test="%{#user.role!=0}">
						<a href="edit?id=<s:property value="#user.userId" />" class="edit-user">Edit</a>
					</s:if>
				</td>
			</tr>
	    </s:iterator>
	</table>
	</div>
</div>
<div id="devicedata" style="padding-top:20px;"></div>