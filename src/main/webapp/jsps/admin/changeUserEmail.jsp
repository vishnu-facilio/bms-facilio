<%@ page import="com.facilio.bmsconsole.actions.AdminAction" %>
<%@ page import="com.facilio.bmsconsoleV3.context.V3PeopleContext"%>
<%@ page import="com.facilio.bmsconsoleV3.util.V3PeopleAPI"%>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.bean.OrgBean" %>
<%@page import="com.facilio.accounts.util.AccountUtil,
java.util.List,
java.sql.Timestamp,
 java.util.Date,
 java.util.Map,
  com.facilio.accounts.dto.*"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

 <%
    String orgid = request.getParameter("orgid");
    String currentEmail = null;
   	currentEmail = request.getParameter("currentEmail");
    String newEmail = null;
    newEmail = request.getParameter("new_Email");
    String printMessage = "";
    V3PeopleContext user = null;
    V3PeopleContext isUserPresent = null;

    Organization org = null;
    OrgBean orgBean = AccountUtil.getOrgBean();
    if (orgid!=null && !orgid.equals(""))
    org = orgBean.getOrg(Long.parseLong(orgid));

       	if (currentEmail != null && org != null) {
       	AccountUtil.setCurrentAccount(Long.parseLong(orgid));
       	isUserPresent = V3PeopleAPI.getPeople(currentEmail);
       		try {
       		if(isUserPresent != null){
       		    if (AdminAction.isEmailInManyOrg(currentEmail)){
       		        printMessage = "Email found in more than 1 org!";
                }else{
                    user = isUserPresent;
                    AccountUtil.setCurrentAccount(Long.parseLong(orgid));
                }
            }
            else{
            printMessage = "Email or Org not found";
            }
            }
       		catch (Exception e) {
       		    com.facilio.util.UserManagementJSP.LOGGER.error("Exception in change User Email",e);
      		}
      	}
   %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
 <form action="" method="GET">
 <h2><i class=" fa fa-user fa-fw "></i>Update User Email <a id="sc"class="btn btn-default bttn" href="changeUserEmail" role="button">Refresh</a></h2>
  <div class=" col-lg-8 col-md-8">

    <div style="margin-top:15px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
            <input id="orgid" type="number" value="<%= orgid == null ? "" : orgid %>" class="form-control" name="orgid" placeholder="Enter orgID" required min="1"/>
    </div>

    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="currentEmail" type="text" value="<%= currentEmail == null ? "" : currentEmail %>" class="form-control" name="currentEmail" placeholder="Enter user email" required/>
    </div>

    <div style="margin-top:30px;">
<% if(user == null){ %>
<button  id="show" type="submit"  >Submit</button>
<% } %>
<p style=" margin-top:15px; font-weight: bold; font-size: 16px;"><%= printMessage %></p>
</div>

<%
if (user != null) { %>

<table style=" margin-top:40px;" class="table table-bordered" >
<tr> <th>ID</th>
<th>ORGID</th>
<th>Name</th>
<th>Email</th>
<th>People Type</th>
</tr>

<tr id="id">

<td><%=user.getId() %></td>
<td><%=user.getOrgId() %></td>
<td><%=user.getName() %></td>
<td><%=user.getEmail() %></td>
<td><%=user.getPeopleTypeEnum() %></td>
</tr>

</table>
<label style="font-size: 16px;" for="new_Email">Enter New Email:</label>
<div style="margin-top:20px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="new_Email" type="text" value="<%= newEmail == null ? "" : newEmail %>" class="form-control" name="new_Email" placeholder="Enter new email"/>
</div>
<%
String result = "";
if (newEmail!=null && newEmail!="") {
    result = AdminAction.updateAccountUserEmail(currentEmail, newEmail);
    }
    %>
<p style=" margin-top:15px; font-weight: bold; font-size: 16px;"><%= result %></p>

<% if(result!="Email updated successfully"){ %>
<button style="margin-top:15px;"  id="show" type="update"  >Submit</button>
<% }
}
%>
</div>

</form>
</body>
</html>
