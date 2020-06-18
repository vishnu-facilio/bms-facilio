<%@page import="com.facilio.logging.SysOutLogger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Scanner"%>
<%@page import="com.facilio.fw.TransactionBeanFactory" %>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.facilio.aws.util.FacilioProperties"%>
<%@page import="com.facilio.bmsconsole.actions.AdminAction"%>
<%@page import="com.facilio.time.DateTimeUtil"%>
<%@page import="java.time.ZonedDateTime"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator" %>
<%@page import="com.facilio.service.FacilioService" %>
<%@page import="org.json.simple.parser.JSONParser" %>
<%
long receivedTime =0l;
String receiveddate = "";
List<Organization> org = null;
OrgBean bean =  AccountUtil.getOrgBean();
org = bean.getOrgs();
List<Map<String , Object>> orgList = FacilioService.runAsServiceWihReturn(() -> AdminAction.getOrgsList());
System.out.println("Select org list is  "+orgList);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
function changeThePage(){
	var selectedOption = "data?orgId="+ $("#orgId").val();
	location.href = selectedOption;

}

</script>
<script type="text/javascript">

	function handleShowMore(target)
	 {
		if(target.classList.contains('expanded'))
		{
			//collapse
			target.parentElement.parentElement.children[0].style.maxHeight='75px'
			target.classList.remove('expanded')
			target.innerText='Show more'
			
		}
		else{//expand
			target.parentElement.parentElement.children[0].style.maxHeight='unset'
				target.classList.add('expanded')
				target.innerText='Show less'
	
		}
		
		
	  }
 

</script>
										
<body>									
	<form action="">
			<div class="admin-data-container">
				<div class="">
				<label for="orgId">
			<div class="admin-data-grey">Org:</div>
				</label>
				<select class="admin-data-select"
					name="orgId" id="orgId" onChange="changeThePage()">
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
				<%
					if(request.getParameter("orgId")!=null){
				    String orgDomain =request.getParameter("orgId");
				    long orgId = Long.parseLong(orgDomain);
				    OrgBean orgBean = (OrgBean) TransactionBeanFactory.lookup("OrgBean",orgId);
				    Organization domain = orgBean.getOrg(orgId);
				    JSONArray jsonArray = new JSONArray();
				    jsonArray = AdminAction.getAlertsPointsData(domain.getDomain());
				  for(int i=(jsonArray.size()-1);i>=0;i--) {
						    JSONObject jsonObj = (JSONObject)jsonArray.get(i);
							long arrival = (Long) jsonObj.get("arrivalTime");
							receiveddate = DateTimeUtil.getFormattedTime(arrival);
							receivedTime = arrival;
							break;
						}
				%>
				</div>
				<div class="">
				  <label><div class="admin-data-grey">Last Received Time :</div></label><span class="admin-data-date"><%=receiveddate %><br><%=receivedTime !=0 ? DateTimeUtil.relativeDuration(receivedTime):" " %></span>
				</div>
			</div>
	
<br> <br> <br>

	</form>


	

<%-- 	<%
  if(orgDomain != null && !orgDomain.isEmpty())
  {
  %> --%>
  

	<table class="table admin-data-border table-bordered  a">
		<tr>
			<th>S.No</th>
			<th>Received Time</th>
			<th>Data Time</th>
			<th>Agent</th>
			<th>Data</th>

		</tr>
		<%
		int i=0;
		for(int j=(jsonArray.size()-1);j>=0;j--) {
			 JSONObject jsonObj = (JSONObject)jsonArray.get(j);
			 String msg = jsonObj.get("message").toString();
			    JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(msg);
				long arrival =0;
				long lastReceivedTime = (long)jsonObj.get("arrivalTime");
				if(json.containsKey("timestamp")){
					arrival = (long)jsonObj.get("timestamp");
				}
			 String date = DateTimeUtil.getFormattedTime(lastReceivedTime);
			 String date1 = DateTimeUtil.getFormattedTime(arrival);
	         i+=1;
	       
		%>

		<tr>
			<td align="center"><%=i%></td>
			<td align="center"><%=date %> <br> <%=DateTimeUtil.relativeDuration(lastReceivedTime) %></td>
			<td align="center"><%=date1 %> <br> <%=arrival !=0 ? DateTimeUtil.relativeDuration(arrival):" " %></td>
			<td align="center"><%= jsonObj.get("device")%></td>

			<td align="left">
				<div class='long-message'>

					<div class="content">
						<%= jsonObj.get("message")%>
					</div>
					<div class="show-more">
						<button onclick="handleShowMore(this)">Show more</button>

					</div>
				</div>
			</td>



		</tr>
		<%} %>
	</table>

	<%} %> 
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
