<%@page import="com.facilio.accounts.util.AccountUtil, com.facilio.bmsconsole.modules.FieldUtil, com.facilio.accounts.dto.Organization ,com.facilio.license.LicenseApi, com.facilio.license.LicenseContext, com.facilio.license.LicenseContext.FacilioLicense, com.facilio.accounts.dto.Role,java.util.Iterator, java.util.List, java.sql.Timestamp, java.util.Date, java.util.Map, com.facilio.accounts.dto.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
 <%
 Organization org = null;
 Long orgid =  null;
 if (request.getParameter("orgid") != null && !"".equals(request.getParameter("orgid").trim())) { 
 	orgid = Long.parseLong(request.getParameter("orgid").trim());
 	
 		org = AccountUtil.getOrgBean().getOrg(orgid);
 	}
 	List<Role> roles = null;
		if (orgid != null)
		{	
			roles = AccountUtil.getRoleBean().getRoles(orgid);
		}
		  
 
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script>
<%
if (request.getParameter("updateUser") != null)
{%>
alert("User Updated Succesfully");
<%
}
%>
</script>
<body>
 <form action="" method="GET" style="height: 150px;">
 <h2><i class=" fa fa-building-o  fa-fw"></i>Org Info</h2>
 <div class=" col-lg-12 col-md-12">
 
    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
    	<span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      	<input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
    </div>
    <div style="margin-top:30px;">
		<button  id="show" type="submit" >Show Form</button>
	</div>
</div>
</form>
<% if (org != null) {  %>
<form action = "updateUser">
</br></br><h4>Invite User:</h4>
		<div>
			<input type = "hidden" name = "orgid" value="<%= orgid %>" />
			<label>Enter the Name:</label><input type = "text" name = "name" id="name" /></br></br>
			<label>Enter the Email:</label><input type = "text" name = "email" id="email" /></br></br>
			<label>Enter Password:</label><input type = "password" name = "password" id="password" /></br></br>
			<label>Enter Role:</label><select name="roleId"  id ="roleId">
			<% for(Role role : roles) {
			if (!AccountUtil.getRoleBean().getRole(role.getRoleId()).getName().equalsIgnoreCase("Super Administrator")) {
			%>
			<option value="<%= role.getId() %>"><%=role.getName()%></option>
			<% }
			}%>
			</select>
			<input type = "submit" style="margin-left: 10px" name="updateUser"  value = "submit"/> 
		</div>

</form>
<% }%>
</body>
</html>