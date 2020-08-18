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
 
 %>       
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script>

function changeAction(){
	var selectedOption = "disableagent?orgId="+ $("#orgId").val()+ "&" + "option=" + $("#option").val();
	location.href = selectedOption;
}


</script>
<body>
<form action="disableAgent">

    <div style="margin-top:40px;" >
   
      <label for="txtClassroomName">
			<b><h5>Org </h5></b>
				</label>
      <select class="admin-data-select"
					name="orgId" id="orgId">
					<option value="" disabled selected>Select</option>
					<%
								for (Map<String,Object> domain : orgList) {
									
							%>
					<option value="<%=domain.get("orgId")%>"<%=(request.getParameter("orgId") != null && request.getParameter("orgId").equals(domain.get("orgId") + "")) ? "selected" : " "%>><%=domain.get("orgId")%>
						-
						<%=domain.get("domain")%></option>
					<%
								}
							%>
				</select>
				<br><br>
		<label for="txtClassroomName">
			<b><h5>Option</h5></b>
				</label>
      <select class="admin-data-select"
					name="option" id="option" onChange="changeAction()">
					<option value="" disabled selected>Select</option>
					
					<option value="<%="EnableOrDisable"%>"<%=(request.getParameter("option") != null && request.getParameter("option").equals("EnableOrDisable")) ? "selected" : " "%>><%="Enable or disable"%>
						</option>
					<option value="<%="ChangeProcessor"%>"<%=(request.getParameter("option") != null && request.getParameter("option").equals("ChangeProcessor")) ? "selected" : " "%>><%="Change Processor"%>
						</option>
				</select>		
    </div>
 
  <%if((request.getParameter("option") != null)){ %>
<fieldset>

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
				<%if((request.getParameter("option") != null && request.getParameter("option").equals("EnableOrDisable"))){ %>
		<label for="txtClassroomName">
			<b><h5>Action </h5></b>
				</label>
      <select class="admin-data-select"
					name="action" id="action">
					<option value="" disabled selected>Select</option>
					
					<option value="<%="Enable"%>"<%=(request.getParameter("action") != null && request.getParameter("action").equals("Enable")) ? "Enable" : " "%>><%="Enable"%>
						</option>
					<option value="<%="Disable"%>"<%=(request.getParameter("action") != null && request.getParameter("action").equals("Disable")) ? "Disable" : " "%>><%="Disable"%>
						</option>
				</select>			
				<%} %>	
	<%if((request.getParameter("option") != null && request.getParameter("option").equals("ChangeProcessor"))){ %>			
			<label for="txtClassroomName">
			<b><h5>Pre Processor</h5></b>
				</label>
      <select class="admin-data-select"
					name="prepro" id="prepro">
					<option value="" disabled selected>Select</option>
					
					<option value="<%="V1toV2"%>"<%=(request.getParameter("prepro") != null && request.getParameter("prepro").equals("V1toV2")) ? "selected" : " "%>><%="V1 to V2"%>
						</option>
					
				</select>	
				<br><br>
				<label for="txtClassroomName">
			<b><h5>Processor </h5></b>
				</label>
      <select class="admin-data-select"
					name="pros" id="pros"">
					<option value="" disabled selected>Select</option>
					
					<option value="<%="V1Processor"%>"<%=(request.getParameter("pros") != null && request.getParameter("pros").equals("V1Processor")) ? "selected" : " "%>><%="V1 Processor"%>
						</option>
					<option value="<%="V1Processor"%>"<%=(request.getParameter("pros") != null && request.getParameter("pros").equals("V2Processor")) ? "selected" : " "%>><%="V2 Processor"%>
						</option>
				</select>		
	<%} %>
	<%} %>				
			</fieldset>
			<input type = "submit"  style="margin-left: 200px" name="disableAgent"  value = "Submit"/> 
</form>
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

table.a {
	table-layout: auto;
	width: 100%;
	font-size: 17px;
}

 button[type=submit] {
	width: 10%;
	background-color: #4CAF50;
	color: white;
	padding: 11px 9px;
	margin: 2px 0;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 15px;
}

 button[type=submit]:hover {
	background-color: #45a049;
}
input[type=submit] {
	width: 10%;
	background-color: #4CAF50;
	color: white;
	padding: 11px 9px;
	margin: 2px 0;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 15px;
}

 input[type=submit]:hover {
	background-color: #45a049;
}

.long-message {
	
}

.content {
	overflow: hidden;
	width: 450px;
	max-height: 75px;
	overflow-wrap: break-word;
}

.show-more {
	
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

.admin-data-container{
    width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-top: 20px;
}
.admin-data-grey{
	color: #333;
	font-size: 13px;
	letter-spacing: 0.5px;
	font-weight: 400;
}
.admin-data-date{
	font-size: 13px;
	font-weight: 500;
	letter-spacing: 0.5px;
	color: #333;
}

.admin-data-border{
    min-width: 100%;
    border-collapse: collapse;
    display: table;
    border: solid 1px #e6ecf3;
}
.admin-data-border thead{
	display: table-header-group;
    vertical-align: middle;
    border-color: inherit;
}
.admin-data-border th{
	color: #324056;
    font-size: 11px;
    letter-spacing: 1px;
    font-weight: bold;
	white-space: nowrap;
    padding: 23px 30px;
    text-align: left;
    display: table-cell;	
    text-transform: uppercase;
}
.admin-data-border tbody{
	display: table-row-group;
    vertical-align: middle;
}

.admin-data-border td{
    color: #333333;
    font-size: 14px;
    border-collapse: separate;
    padding: 15px 30px;
    letter-spacing: 0.6px;
    font-weight: normal;
}

.admin-data-border th, admin-data-border td{
    font-size: 12px;
    color: #333;
    padding: 10px 15px;
    line-height: 20px;
}

</style>
</html>