<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.aws.util.FacilioProperties" %>
<%@page import="com.facilio.accounts.util.AccountUtil,java.util.Comparator"%>
<%@page import="com.facilio.accounts.dto.User,com.facilio.accounts.dto.*, java.util.*"%>
<%@page import="java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List"%>
<%@page import="com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl"%>
<%@page import="com.facilio.bmsconsole.commands.util.CommonCommandUtil, com.facilio.accounts.util.AccountUtil.FeatureLicense"%>
<%@ page import="com.facilio.bmsconsole.util.ApplicationApi" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String orgid = request.getParameter("orgid");
  Organization org = null;
  boolean user=false;
  JSONObject result = null;
  List<User> users = null;
  List<Role> roles = null;
  TreeMap<String,Boolean> features = new TreeMap<>();

  if (orgid != null) {
	  long orgId = Long.parseLong(orgid);

	  OrgBean orgBean = AccountUtil.getOrgBean();
	  org = orgBean.getOrg(Long.parseLong(orgid));
	  result = AccountUtil.getOrgBean(orgId).orgInfo();
	  long appId = AccountUtil.getOrgBean(orgId).getDefaultApplicationId();
	  if(appId > 0) {
		  users = AccountUtil.getOrgBean(orgId).getAppUsers(orgId, appId, false);
		  roles = AccountUtil.getRoleBean(orgId).getRolesForApps(Collections.singletonList(appId));
	  }
	  features  = AccountUtil.getFeatureLicenseMap(orgId);
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
function logs(userId){
	let win = window.open("https://logs.facilio.in/streams/000000000000000000000001/search?saved=5d00ce839b569c766b1f32d7&rangetype=relative&fields=message%2Csource&width=1366&highlightMessage=&relative=86400&q=NOT%20facility%3Astage%20AND%20logger%3A%20com.facilio.filters.AccessLogFilter%20AND%20userId%3A"+userId,'_blank')
	win.focus();
}
function view(userId){
	FacilioApp.ajax({
		method : "get",
		url : contextPath + "/api/verifyusers?userId="+userId,
		done: function(data) {
			document.getElementById(userId).innerHTML = 'true'
		}
	})
}
function showUserInvite() {
  let x = document.getElementById("new");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}
function showLicense() {
  let x = document.getElementById("newlicense");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
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
		<button  id="show" type="submit">Submit</button>
		<% if (org != null) {  %>
			<table style=" margin-top:40px;"  class="table table-bordered" >
				<tr>
					<th>orgId</th>
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
					<th> LoggerLevel</th>
				</tr>

				<tr>
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
					<td><%=org.getLoggerLevel() %></td>
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
    					String value = (String) result.get(key);
					%>
    					<tr>
    						<td><%=key %></td>
    						<td><%=value %></td>
  						</tr>
					<% } %>
				</table>
			<%}%>
			<h4> No Of Users In OrgId  <%= org.getOrgId() %> :  <%= users.size() %> </h4>
		<% } %>

		<% if(users!=null) { %>
			<table style=" margin-top:40px;"  class="table table-bordered">
				<tr>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> OrgUserId </th>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> UserId </th>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> IAM_ORG_UserId </th>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Name </th>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Email  </th>
					<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> UserVerified</th>
					<th class="org-th" style="max-width: 350px; width:350px;text-align: center;">Status</th>
	             	<%
						String rebrand = FacilioProperties.getConfig("rebrand.domain");
						if (rebrand.equals("facilio.com")) {
       				%>
						<th class="org-th" style="max-width: 350px;width:350px;text-align: center;">Activity</th>
      
       				<% } %>
				</tr>
	 			<%
	 				if (users!=null) {
	 					Iterator a = users.iterator();
						while( a.hasNext() ) {
							User b = (User) a.next();
				%>
					<tr id="id">
						<td  style="max-width: 350px;width:350px;" align="center"><%=b.getId() %></td>
						<td  style="max-width: 350px;width:350px;" align="center"><%=b.getUid() %></td>
						<td  style="max-width: 350px;width:350px;" align="center"><%=b.getIamOrgUserId() %></td>
						<td  style="max-width: 350px;width:350px;" align="center"><%=b.getName() %></td>
						<td  style="max-width: 350px;width:350px;"  align="center"><%=b.getEmail() %></td>
						<td  style="max-width: 350px;width:350px;" align="center" id=<%=b.getId() %>><%=b.getUserVerified() %></td>
						<td style="max-width: 350px;width:350px;text-align: center;">
							<button type="button" onclick="view(<%=b.getUid()%>)">Update</button>
						</td>
						<%
           					String rebrandinfo = FacilioProperties.getConfig("rebrand.domain");
           					if (rebrandinfo.equals("facilio.com")) {
       					%>
       						<td style="max-width: 350px;width:350px;" align="center">
								<button type ="button" onclick="logs(<%=b.getOuid()%>)">Logs </button>
							</td>
       					<% } %>
					</tr>
				<%
						}
					}
				%>
			</table>
			<br>
		<% } %>
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
		<br>
		<br>
	</form>
<% } %>

<div class=" col-lg-12 col-md-12">
	<% if(orgid!=null) { %>
		<button onclick="showUserInvite()">INVITE USER</button>
	<% } %>
	<br>
	<br>
	<br>

	<div id="new" style="display:none">
		<% if(orgid!=null ) { %>
			<form action = "updateUser">
				<h4>Invite User:</h4>
				<div>
					<input type = "hidden" name = "orgid" value="<%= orgid %>" />
					<label>Enter the Name:</label><input type = "text" name = "name" id="name" /></br></br>
					<label>Enter the Email:</label><input type = "text" name = "email" id="email" /></br></br>
					<label>Enter Password:</label><input type = "password" name = "password" id="password" /></br></br>
					<label>Enter Role:</label>
					<select name="roleId"  id ="roleId">
						<% for(Role role : roles) {
							if (!AccountUtil.getRoleBean(Long.parseLong(orgid)).getRole(role.getRoleId()).getName().equalsIgnoreCase("Super Administrator")) {
						%>
							<option value="<%= role.getId() %>"><%=role.getName()%></option>
						<%
							}
						}
						%>
					</select></br></br>
					<input type = "submit" style="margin-left: 10px" name="updateUser"  value = "Submit"/>
					<input type="reset" value="Reset"/>
					<button onclick="showUserInvite()">Cancel</button>
					<br>
					<br>
					<br>
				</div>

			</form>
		<% } %>
	</div>
</div>

<div class=" col-lg-12 col-md-12">
	<% if (orgid!=null) { %>
		<button onclick="showLicense()">Add License</button>
	<% } %>
	<br>
	<div id="newlicense" style="display:none">
		<% if (orgid!=null) { %>
			<form method="POST" ACTION="addLicense">
				<h4>License features:</h4>
				<div>
					<input type = "hidden" name = "orgid" value="<%= orgid %>" />
					<table style=" width: 50%; margin-top:40px;"  class="table table-bordered">
						<tr>
							<td style="text-align:center;"><b>ID</b></td>
							<td style="text-align:center;"><b>FEATURES</b></td>
							<td style="text-align:center;"><b>STATUS</b></td>
						</tr>
  
						<%
							int id = 0;
							for(String key  :features.keySet())
							{
  						%>
								<tr>
									<td>
										<label><%=++id%></label>
									</td>
									<td>
										<label><%=key%></label>
									</td>
									<td style="text-align:center;">
										<input type = "checkbox" <% if (features.get(key)) { %> checked <%  }%> name="selected" value="<%=key%>" />
									</td>
								</tr>
 						<%  } %>
					</table>
				</div>
				<%
					if(ApplicationApi.isAdminControlAllowed()) {
  				%>
						<input type="submit" name="addLicense" value="Update" />
				<%  } %>
			</form>

		<% } %>
	</div>
</div>

<style>
.org-th {
  background-color: #f0f2f4;
  font-size: 14px;
  color: #717b85;
  font-weight: 500;
  padding: 18px 20px !important;
}
.org-td {
  color: #333;
  font-size: 13px;
  padding: 15px 20px !important;
}
select {
	width: 17%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 8px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
	font-size: 16px;
	margin-left:44px;
}

input[type=text] {
  width: 50%;
  padding: 12px 20px;
  margin: 8px 16px;
  margin-top:8px;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type=submit] {
  width: 15%;
  background-color: #4CAF50;
  color: white;
  padding: 14px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

input[type=submit]:hover {
  background-color: #45a049;
}
</style>
</body>
</html>