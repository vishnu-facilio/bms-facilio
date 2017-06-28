<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<form role="form" id="newUserForm" method="post" onsubmit="return false;">
	<div class="form-group">
	    <label>First Name</label>
	    <s:textfield name="first_name" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Last Name</label>
	    <s:textfield name="last_name" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Email</label>
	    <s:textfield type="email" name="email" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Role</label>
	    <s:select list="roles" name="role" class="form-control" />
	</div>
	<div class="form-group">
	    <label>Password</label>
	    <s:textfield type="password" name="password" class="form-control"/>
	</div>
</form>