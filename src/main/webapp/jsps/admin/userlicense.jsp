<%@page import="com.facilio.accounts.util.AccountUtil, com.facilio.modules.FieldUtil, com.facilio.license.LicenseApi, com.facilio.license.LicenseContext, com.facilio.license.LicenseContext.FacilioLicense, com.facilio.accounts.dto.Role,java.util.Iterator, java.util.List, java.sql.Timestamp, java.util.Date, java.util.Map, com.facilio.accounts.dto.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
 <%
 Long orgId =  null;
 List<LicenseContext> licenses = null;
 if (request.getParameter("orgId") != null) {
	 
 	orgId = Long.parseLong(request.getParameter("orgId").trim());
 // List<Role> roles = null;
 

if (orgId != null)
{	
	licenses = LicenseApi.getLicenseMap();
	// roles = AccountUtil.getRoleBean().getRoles(orgId);
}
 }
  
 
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action = "" method ="GET">
<h2>User License:</h2>
<div >
<div style="margin-top:30px">

<label>Enter the OrgID:</label><input style="margin-left: 10px" type = "number" name = "orgId" id="orgId" />
</div>
<div style="margin-top:30px">
<input type = "submit" value = "submit"> 
</div>
<div >
<% if(licenses != null) { 
	%>
<h2> License </h2> 
<table style=" margin-top:40px;" class="table table-bordered">
<tr>
  <th style="text-align:center">License</th>
  <th style="text-align:center">Total License</th>
  <th style="text-align:center">Used License</th>
</tr>

<% for (LicenseContext license : licenses) {
	%>
	<tr>
	<td><%= license.getLicenseEnum() %></td>
	<td><%= license.getTotalLicense() %></td>
	<td><%= license.getUsedLicense() %></td>
	</tr>
	<% } %>
</table>
<%}%>


<%-- <% for (Role role : roles) {
    %>
    <tr>
    <td><%= role.getName() %></td>
    <td><input type="text" id="<%= role.getId() %>"></td> 
  </tr>
<% } %>
</table>
<%}%> --%>

</div>
</div>
</form>

</body>
</html>