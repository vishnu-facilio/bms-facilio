<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<form role="form" id="editUserForm" method="post" onsubmit="return false;">
	<s:textfield type="hidden" name="userId" value="%{user.userId}" class="form-control"/>
	<div class="form-group">
	    <label>First Name</label>
	    <s:textfield name="first_name" value="%{user.firstName}" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Last Name</label>
	    <s:textfield name="last_name" value="%{user.lastName}" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Email</label>
	    <s:textfield type="email" name="email" value="%{user.email}" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Role</label>
	    <s:select list="roles" name="role" value="%{user.role}" class="form-control" />
	</div>
	<div class="form-group">
	    <label>Status</label>
	    <s:radio list="#{true:'Active', false:'Inactive'}" name="status" value="%{user.inviteAcceptStatus}"/>
	</div>
</form>