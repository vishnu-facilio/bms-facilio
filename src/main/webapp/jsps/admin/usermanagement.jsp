<%@page import="com.facilio.accounts.util.AccountUtil, java.util.List, com.facilio.accounts.dto.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
 <%
 	String email = request.getParameter("email");
 List<User> userList = null;
 if (email != null) {
	 User usr = AccountUtil.getUserBean().getFacilioUser(email);
	 long orgId = usr.getOrgId();
	 long roleId = usr.getRoleId();
	 if (AccountUtil.getRoleBean().getRole(roleId).getName().equalsIgnoreCase("Super Administrator")) {
		 userList = AccountUtil.getOrgBean().getAllOrgUsers(orgId);	    
	 }
 }
 %>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body onload="init()">
 <form action="" method="GET">
 <h2>User Management</h2>
 <div class="container col-lg-8 col-md-8">
 
    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="email" type="text" value="<%= email == null ? "" : email %>" class="form-control" name="email" placeholder="superadmin_id@example.com" required/>
    </div>
   
      <div style="margin-top:30px;">
<button  id="show" type="submit"  >Submit</button>
</div> 
<div >
<%
if (userList != null) { %>
<table style=" margin-top:40px;" class="table table-bordered" >
<tr> <th>Id</th>
<th>Name</th>	
<th> Email</th>
<th> Role </th>
<th>Language</th>
<th> Country </th>
<th> Timezone </th>
<th> Status </th>
</tr>
<% for(User user : userList) {
%>
<tr id="id">



<td><%=user.getId() %></td>
<td><%=user.getName() %></td>
<td><%=user.getEmail() %></td>
<td><%=user.getRole().getName()%></td>
<td><%=user.getLanguage()%></td>
<td><%=user.getCountry()%></td>
<td><%=user.getTimezone() %></td>
<td><%=user.getUserStatus()%></td>


</tr>
<%
}%>

</table>
<% }
%>
</div>
    </div>
    </form>
</body>
</html>