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
	
	function deleteGroup(obj) {
		var groupId = $(obj).attr('group-id');
		
		$.ajax({
			method : "post",
			url : "<s:url action='delete' />",
			data : {groupId: groupId}
		})
		.done(function(data) {
			console.log(data);
			alert('Group deleted successfully!');
			window.location.href='/home/groups/';
		})
		.fail(function(error) {
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
</script>
<div id="maincontent" style="padding:20px;">
	<div style="padding-bottom: 20px;width: 800px;">
		<div style="float: left;font-size: 18px;padding-top: 3px;">Groups :</div>
		<div style=" float:right;"><a class="btn btn-default" href="/home/groups/new" role="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Group</a></div>
		<div style="clear: both;"></div>
	</div>
	<div>
	<table style="width: 800px; border: 1px solid #ddd;">
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Status</th>
			<th>Created By</th>
			<th>Created On</th>
			<th></th>
		</tr>
		<s:iterator var="group" value="%{groups}">
			<tr>
				<td><a href="<s:property value="#group.groupId" />"><s:property value="#group.name" /></a></td>
				<td><s:property value="#group.description" /></td>
				<td><s:property value="#group.getStatusAsString()" /></td>
				<td><s:property value="#group.getCreatedByUser().getEmail()" /></td>
				<td><s:property value="#group.getFormattedCreatedTime()" /></td>
				<td>
					<a href="edit?id=<s:property value="#group.groupId" />" class="edit-user">Edit</a>&nbsp;&nbsp;&nbsp;
					<a href="javascript:;" onclick="deleteGroup(this);" group-id="<s:property value="#group.groupId" />" class="edit-user">Delete</a>
				</td>
			</tr>
	    </s:iterator>
	</table>
	</div>
</div>
<div id="devicedata" style="padding-top:20px;"></div>