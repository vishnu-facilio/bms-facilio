<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.facilio.accounts.util.AccountUtil, com.facilio.accounts.dto.User, java.util.*, java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List, com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl, com.facilio.bmsconsole.commands.util.CommonCommandUtil"%>
  <%
  	
  String orgid = request.getParameter("orgid");
    Organization org = null;
    JSONObject result = null;
    Boolean verified = null;
    List<User> users = null;
    if (orgid != null) {
  	  org = AccountUtil.getOrgBean().getOrg(Long.parseLong(orgid));
  	  result = CommonCommandUtil.getOrgInfo(Long.parseLong(orgid));
  	  users = AccountUtil.getOrgBean().getAllOrgUsers(Long.parseLong(orgid));
  	}
  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script>

function view(userId){
	console.log(userId)
	FacilioApp.ajax({
		method : "get",
		url : contextPath + "/internal/verifyUsers?userid="+userId,
		done: function(data) {
			
		}
	})
}
</script>
<body>
 <form action="" method="GET">
 <h2><i class=" fa fa-building-o  fa-fw"></i>Org Info</h2>
 <div class=" col-lg-8 col-md-8">
 
    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
    </div>
    <div style="margin-top:30px;">

<button  id="show" type="submit"  >Submit</button>
<% if (org != null) {  %>
<table style=" margin-top:40px;"  class="table table-bordered" >
<tr> <th>orgId</th>
<th> Name </th>
<th> Domain </th>
<th> LogoId </th>
<th> Phone </th>
<th> Mobile </th>
<th> Fax </th>
<th> Street </th>
<th> City </th>
<th> State </th>
<th> Zip </th>
<th> Country </th>
<th> Timezone </th>
<th> Currency </th>
<th> CreatedTime </th>
<th> PortalId </th>
</tr>

<tr id="id">
<td><%=org.getOrgId() %></td>
<td><%=org.getName() %></td>
<td><%=org.getDomain() %></td>
<td><%=org.getLogoId() %></td>
<td><%=org.getPhone() %></td>
<td><%=org.getMobile() %> </td>
<td><%=org.getFax() %> </td>
<td><%=org.getStreet() %> </td>
<td><%=org.getCity() %> </td>
<td><%=org.getState() %> </td>
<td><%=org.getZip() %> </td>
<td><%=org.getCountry() %> </td>
<td><%=org.getTimezone() %> </td>
<td><%=org.getCurrency() %> </td>
<td><%=org.getCreatedTime() %> </td>
<td><%=org.getPortalId() %> </td>
</tr>
</table>


<% if(result.size() != 0) { 
	Iterator<?> keys = result.keySet().iterator();%>
<h2> Org-Properties</h2> 
<table style=" margin-top:40px;" class="table table-bordered">
<tr>
  <th style="text-align:center">NAME</th>
  <th style="text-align:center">VALUES</th>
</tr>

<% while( keys.hasNext() ) {
    String key = (String) keys.next();
    String value = (String) result.get(key); %>
    <tr>
    <td><%=key %></td>
    <td><%=value %></td> 
  </tr>
<% } %>
</table>
<%}%>
<h4> No Of Users In OrgId  <%= org.getOrgId() %> :  <%= users.size() %> </h4>
<% } %>
<table style=" margin-top:40px;"  class="table table-bordered">
	<tr>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Id </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Name </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Email  </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> UserVerified</th>
	<th class="org-th" style="max-width: 350px;width:350px;text-align: center;">Status</th>
	</tr>
	 <% Iterator a = users.iterator();
		while( a.hasNext() ) { 
	User b = (User) a.next();  %>
	<tr id="id">
	<td  style="max-width: 350px;width:350px;" align="center"><%=b.getId() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=b.getName() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%=b.getEmail() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=b.getUserVerified() %></td>
	<td style="max-width: 350px;width:350px;text-align: center;"> <button type="button" onclick="view(<%=b.getId()%>)">Update</button> </td>
	</tr>
<%}%>
</table>

</div>
</div>
</form>

<% if (org != null) { %>
<form action="updateCRM">
<input type="hidden" name="orgid" value="<%= org.getOrgId() %>">
<input type="hidden" name="freshsales.faciliodomainname" value="<%= org.getDomain() %>">
<input type="hidden" name="freshsales.name" value="<%= org.getDomain() %>">

<input type="hidden" name="freshsales.ORGID" value="<%= org.getOrgId() %>">
<input type="text" name="freshsales.amount" value="1000">
<input type="hidden" name="freshsales.sales_account" value="<%=  "{\"name\":\"Sample Account\"}" %>">



<input type="submit" name="update CRM" value="update FreshSales">

</form>

<% } %>

<style>
.org-th{
  background-color: #f0f2f4;
  font-size: 14px;
  color: #717b85;
  font-weight: 500;
  padding: 18px 20px !important;
}
.org-td{
  color: #333;
  font-size: 13px;
  padding: 15px 20px !important;
}
</style>
</body>

</html>