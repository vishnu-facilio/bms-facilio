<%@page import="com.facilio.logging.SysOutLogger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator" %>
<%@page import="com.facilio.service.FacilioService" %>    
<%@page import="com.facilio.bmsconsole.actions.AdminAction"%>
 <%
 List<Organization> org = null;
 OrgBean bean =  AccountUtil.getOrgBean();
 org = bean.getOrgs();
 List<Map<String , Object>> orgList = AdminAction.getOrgsList();
 List<Map<String , Object>> agentVersions = AdminAction.getAgentVersions();
 long text=-1L;
 %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Agent Version</title>
</head>
<script>
function myFunction() {
  var x = document.getElementById("new");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}
function upgradeFunction() {
	  var x = document.getElementById("upgrade");
	  if (x.style.display === "block") {
	    x.style.display = "none";
	  } else {
	    x.style.display = "block";
	  }
	}

</script>

<script>

function changeOrg(){
	var selectedOption = "agentversion?orgId="+ $("#orgId").val()
	location.href=selectedOption;
}
function changeVersion(){
	var selectedOption = "agentversion?orgId="+ $("#orgId").val()+ "&" + "version=" + $("#version").val();
	location.href = selectedOption;
}


</script>
<script>
function validateForm() {
	  var ver = document.forms["myForm"]["version"].value;
	  var desc = document.forms["myForm"]["desc"].value;
	  var createdby =document.forms["myForm"]["user"].value;
	  var url = document.forms["myForm"]["url"].value;	  
	  if (ver == "" || ver == null ||ver.indexOf('.') === -1) {
	    alert("version must be filled out and include with (.) dot (eg.2.0)");
	    return false;
	  }
	  else if(desc == "" || desc == null){
		  alert("description must be filled out");
		  return false;
	  }else if(createdby == "" || createdby == null){
		  alert("createdBy must be filled out"); 
		  return false;
	  }else if(url == "" || url == null){
		  alert("url must be filled out");  
		  return false;
	  }
	}
</script>
<body>
<br><br><br>
<h5><b>Agent Upgrade</b></h5>


<form action = "upgradeAgentVersion">
</br></br>
	<div >
			<fieldset>
			<label for="txtClassroomName"><h5><b>Organization</b> </h5> </label>	<select class="admin-data-select"
					name="orgId" id="orgId" onChange="changeOrg()">
					<option value="" disabled selected>Select</option>
					<%
								for (Map<String,Object> domain : orgList) {
									
							%>
					<option value="<%=domain.get("orgId")%>"<%=(request.getParameter("orgId") != null && request.getParameter("orgId").equals(domain.get("orgId") + "")) ? "selected" : " "%>><%=domain.get("orgId")%>
						-
						<%=domain.get("domain")%> </option>
					<%
								}
							%>
				</select></br></br>
				
				<label for="txtClassroomName"><h5><b>Agent</b></h5> </label>	<select class="admin-data-select"
					name="agentId" id="agentId">
					<option value="" disabled selected>Select</option>
					
				<%
				
					if ((request.getParameter("orgId") != null)) {
						
						long orgId = Long.parseLong(request.getParameter("orgId"));
							List<Map<String,Object>> agentIds= AdminAction.getAgentList(orgId);
							for (Map<String,Object> list : agentIds) {
				%>
				<option value="<%=list.get("id")%>"><%=list.get("displayName") %></option> 
				<%
							}
						}
				%>
				</select></br></br>	
			<label for="txtClassroomName"><h5><b>Version</b> </h5> </label>	<select class="admin-data-select"
					name="version" id="version">
					<option value="" disabled selected>Select</option>
					<%
								for (Map<String,Object> agentVersion : agentVersions) {
									
							%>
					<option value="<%=agentVersion.get("id")%>"><%=agentVersion.get("version")%></option>
								<%
								}
								%>
							
				</select> </br></br>	
				
			</fieldset>
			</br></br>
			
			<input type = "submit"  style="margin-left: 200px" name="upgradeAgentVersion"  value = "Submit"/> 
			
			<br>
			<br>
			<br>
		</div> 

</form>

<button onclick="myFunction()" class="button button1"><b>Add Version</b></button>
<div id="new" style="display:none">

<form name="myForm" action = "addAgentVersion"  onsubmit="return validateForm()" required>
</br></br>
	<div >
			<fieldset>
			<label for="txtClassroomName"><h5><b>Version<span class="required"></span> </b></p> </h5> </label><input type = "text" name = "version" size="10"/></br></br>
			<label for="txtClassroomName"><h5><b>Description<span class="required"></b></h5>  </label><input type = "text" name = "desc"   size="5"/></br></br>
			<label for="txtClassroomName"><h5><b>CreatedBy<span class="required"></b></h5> </label><input type = "text" name = "user"   size="5"/></br></br>
			<label for="txtClassroomName"><h5><b>URL<span class="required"></b></h5>    </label><input type = "text" name = "url"  size="5"/>
			</fieldset>
			</br></br>
			
			<input type = "submit"  style="margin-left: 200px" name="addAgentVersion"  value = "Submit"/> 
			
			<br>
			<br>
			<br>
		</div> 

</form>
</div>
<br><br><br>

</body>
<style>
input[type=text] {
	width: 20%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 8px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}
input[type=submit] {
	width: 10%;
	background-color: #4CAF50;
	color: white;
	padding: 11px 9px;
	justify-content: center;
    align-items: center;
	margin-right: -12px 0;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 15px;
}

input[type=submit]:hover {
	background-color: #45a049;
}
.button {
  background-color: #4CAF50; /* Green */
  border: none;
  color: white;
  padding: 16px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 14px;
  margin: 4px 2px;
  transition-duration: 0.4s;
  cursor: pointer;
}
label{
display:inline-block;
width:150px;
margin-left:100px;
text-align:left;
}


  .required:after {
    content:" *";
    color: red;
  }

.admin-data-select {
	font-size: 14px;
	color: #333;
	background: #fff;
	padding: 12px 15px;
	margin: 8px 16px;
	margin-top: 5px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
}

.button1 {
  background-color: white; 
  color: black; 
  border: 2px solid #4CAF50;
}
.button1:hover {
  background-color: #4CAF50;
  color: white;
}
</style>
</html>