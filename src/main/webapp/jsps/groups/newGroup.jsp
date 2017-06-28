<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<form role="form" id="newGroupForm" method="post" onsubmit="return false;">
	<div class="form-group">
	    <label>Name</label>
	    <s:textfield name="name" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Email</label>
	    <s:textfield type="email" name="email" class="form-control"/>
	</div>
	<div class="form-group">
	    <label>Description</label>
	    <s:textarea name="description" class="form-control"/>
	</div>
	<div class="form-group">
  		<label>Select Members</label>
  		<s:select list="userList" name="members" id="members" multiple="true" size="5" class="form-control"/>
  	</div>
</form>