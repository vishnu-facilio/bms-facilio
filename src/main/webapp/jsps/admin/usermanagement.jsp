<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.accounts.util.AccountUtil, 
java.util.List, 
java.sql.Timestamp,
 java.util.Date, 
 java.util.Map,
  com.facilio.accounts.dto.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
 <%
   	String email = request.getParameter("email");
    List<User> userList = null;
    List<Map<String, Object>> sessions = null;
    User usr = null;
    			if (email != null) {
   		usr = AccountUtil.getUserBean().getFacilioUser(email);
   		sessions = AccountUtil.getUserBean().getUserSessions(usr.getUid(), null);

   		long orgId = usr.getOrgId();
   		long roleId = usr.getRoleId();
   		if (AccountUtil.getRoleBean(orgId).getRole(roleId).getName().equalsIgnoreCase("Super Administrator")) {
   			userList = AccountUtil.getOrgBean(orgId).getAllOrgUsers(orgId);
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
 <h2><i class=" fa fa-user fa-fw "></i>User Management</h2>
  <div class=" col-lg-8 col-md-8">
 
    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="email" type="text" value="<%= email == null ? "" : email %>" class="form-control" name="email" placeholder="superadmin_id@example.com" required/>
    </div>
   
    <div style="margin-top:30px;">
    
<button  id="show" type="submit"  >Submit</button>

</div>


<div >
<% 
if (sessions != null) 
{ %>


<div style="margin-top:30px;">
<a href="clearsession?email=<%=email%>&userId=<%=usr.getUid()%>">
<button type="button">Clearsession</button>
</a>
</div>


<br>
<br>
<b>Sessions:</b>
<table style="margin-top:10px;" class="table table-bordered" >
<tr>
<th>ID</th>	
<th>START_TIME</th>
<th>END_TIME</th>
<th>IS_ACTIVE</th>
<th>IPADDRESS</th>
<th>USER_AGENT</th>
</tr>
<% for(Map<String, Object> sss : sessions) {
	Long startTime = (Long) sss.get("startTime");
	Long endTime = (Long) sss.get("endTime");
%>
<tr id="id">
<td><%=sss.get("id") %></td>
<td><%= (startTime != null && startTime > 0) ? new Date(startTime).toString() : "-"  %></td>
<td><%= (endTime != null && endTime > 0) ? new Date(endTime).toString() : "-"  %></td>
<td><%=sss.get("isActive") %></td>
<td><%=sss.get("ipAddress") %></td>
<td><%=sss.get("userAgent") %></td>
</tr>
<%
}%>

</table>
 <% }
%>
<%
if (userList != null) { %>



<table style=" margin-top:40px;" class="table table-bordered" >
<tr> <th>ORG_USERID</th>
<th>USERID</th>
<th>ORGID</th>
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
<td><%=user.getUid() %></td>
<td><%=user.getOrgId() %></td>
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