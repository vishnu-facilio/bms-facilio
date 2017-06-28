<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<form role="form" id="editGroupForm" method="post" onsubmit="return false;">
	<s:textfield type="hidden" name="groupId" id="groupId" value="%{group.groupId}" class="form-control"/>
	<div class="form-group">
	    <label>Name</label>
	    <s:textfield name="name" value="%{group.name}" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Email</label>
	    <s:textfield type="email" name="email" value="%{group.email}" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Description</label>
	    <s:textarea name="description" value="%{group.description}" class="form-control"/>
	</div>
	<div class="form-group">
  		<label>Select Members</label>
  		<s:select list="userList" value="%{selectedUserList}" name="members" id="members" multiple="true" size="5" class="form-control"/>
  	</div>
</form>