<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.facilio.accounts.util.AccountUtil,java.util.Comparator, com.facilio.accounts.dto.User,com.facilio.accounts.dto.Role, java.util.*, java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List, com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl, com.facilio.bmsconsole.commands.util.CommonCommandUtil, com.facilio.accounts.util.AccountUtil.FeatureLicense"%>
  <%
  	
  String orgid = request.getParameter("orgid");
    Organization org = null;
    boolean user=false;
    JSONObject result = null;
    List<User> users = null;
    List<Role> roles = null;
    if (orgid != null) {
  	  org = AccountUtil.getOrgBean().getOrg(Long.parseLong(orgid));
  	  result = CommonCommandUtil.getOrgInfo(Long.parseLong(orgid));
  	  users = AccountUtil.getOrgBean().getAllOrgUsers(Long.parseLong(orgid));
  	  roles =AccountUtil.getRoleBean().getRoles(Long.parseLong(orgid));
  	}
    
    Map<Long,FeatureLicense> FEATUREMAPUNORDERED  = AccountUtil.FeatureLicense.getAllFeatureLicense();
    Map<FeatureLicense,Long> FEATUREMAPreversed = new HashMap<>();
    Map<String,Long> FEATUREMAPstring = new HashMap<>();
    long val1;
    FeatureLicense val2;
    
    for(Long key :FEATUREMAPUNORDERED.keySet())
    {
    	val1 = key;
    	val2 = FEATUREMAPUNORDERED.get(key);
    	FEATUREMAPreversed.put(val2,val1);
    }
    
    
    for(FeatureLicense key :FEATUREMAPreversed.keySet()) 
    {
    	String val3 = (String.valueOf(key));
    	long val4 = FEATUREMAPreversed.get(key);
    	FEATUREMAPstring.put(val3,val4);
    	
    }
    TreeMap<String,Long> FEATUREMAP = new TreeMap<String,Long>(FEATUREMAPstring);
    
	int module = AccountUtil.getFeatureLicense();
	int remainder,i=0;
	List<Long> enabledFeatures = new ArrayList<Long>();
	while(module!=0)
	{
		remainder=module%2;
		if(remainder==1)
		{
			//indicate the corresponding pow(2,i);
			FeatureLicense licensename = AccountUtil.FeatureLicense.getFeatureLicense(pow(i));
			enabledFeatures.add(licensename.getLicense());
		}
		module/=2;
		i++;	
	}
  %>
 <%! 

public long pow(int num) {
long res=1;
for(int i=0;i<num;i++)
res=res*2;
return res;
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
		url : contextPath + "/api/verifyusers?userid="+userId,
		done: function(data) {
			document.getElementById(userId).innerHTML = 'true'
		}
	})
}
</script>
<script>
function myFunction() {
  var x = document.getElementById("new");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}
</script>
<script>
function myLicenseFunction() {
  var x = document.getElementById("newlicense");
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
<%if(users!=null){ %>
<table style=" margin-top:40px;"  class="table table-bordered">
	<tr>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Id </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Name </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Email  </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> UserVerified</th>
	<th class="org-th" style="max-width: 350px;width:350px;text-align: center;">Status</th>
	</tr>
	 <% 
	 if(users!=null){
	 Iterator a = users.iterator();
		while( a.hasNext() ) { 
	User b = (User) a.next();  %>
	<tr id="id">
	<td  style="max-width: 350px;width:350px;" align="center"><%=b.getId() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=b.getName() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%=b.getEmail() %></td>
	<td  style="max-width: 350px;width:350px;" align="center" id=<%=b.getId() %>><%=b.getUserVerified() %></td>
	<td style="max-width: 350px;width:350px;text-align: center;"> <button type="button" onclick="view(<%=b.getId()%>)">Update</button> </td>
	</tr>
<%
}
}%>
</table>
<br>



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
<%} %>

<div class=" col-lg-12 col-md-12">

<%if(orgid!=null) {%>
<button onclick="myFunction()">INVITE USER</button>
<%} %>
<br>
<br>
<br>

<div id="new" style="display:none">
<%if(orgid!=null ){ %>
<form action = "updateUser">
</br></br><h4>Invite User:</h4>
	<div >
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
			</select></br></br>
			<input type = "submit" style="margin-left: 10px" name="updateUser"  value = "Submit"/> 
			<input type="reset" value="Reset"/>
			<button onclick="myFunction()">cancel</button>
			
			<br>
			<br>
			<br>
		</div> 

</form>
<%} %>
</div>
</div>
 
<div class=" col-lg-12 col-md-12">

<%if(orgid!=null) {%>
<button onclick="myLicenseFunction()">Add License</button>
<%} %>
<br><br><br>


<div id="newlicense" style="display:none">
<%if(orgid!=null ){ %>
<form method="POST" ACTION="addLicense">
<br><br>

<h4>License features:</h4>


<div >
<input type = "hidden" name = "orgid" value="<%= orgid %>" />
<table style=" width: 50%; margin-top:40px;"  class="table table-bordered">

<tr>
	<td style="text-align:center;"><b>FEATURES</b></td>
	<td style="text-align:center;"><b>STATUS</b></td>
</tr>

  <%  
  
  	for(String key  :FEATUREMAP.keySet())
  	 {
  		long value = FEATUREMAP.get(key);
  		boolean isEnabled = false;
  		if(enabledFeatures.contains(value)) {
  			isEnabled = true;
  	}
  %>
	<tr>
	<td><label><%=key%></label> </td>
	<td style="text-align:center;">
   		
  		<input type = "checkbox" <% if (isEnabled == true) { %> checked <%  }%> name="selected" value = "<%=value%>"   id="<%=orgid%>" />
  		
	</td>
	</tr>


 <%} %>	

</table>


</div> 

<input type="submit" name="addLicense" value="Update" />
</form>

<%} %>
<br><br><br>
</div>
</div>


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