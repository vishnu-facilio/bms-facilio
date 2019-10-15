<%@page import="com.facilio.logging.SysOutLogger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Scanner"%>
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

<%
long receivedTime =0l;
String receiveddate = "";
List<Organization> org = null;
OrgBean orgBean =  AccountUtil.getOrgBean();
org = orgBean.getOrgs();


%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
function changeThePage(){
	var selectedOption = "data?orgDomain="+ $("#orgDomain").val();
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
		<br> <br> <br> <label for="orgDomain">
			<h4>Org:</h4>
		</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select
			name="orgDomain" id="orgDomain" onChange="changeThePage()">
			<option value="" disabled selected>Select</option>
			<%
						for (Organization domain : org) {
							
					%>
			<option value="<%= domain.getDomain()%>"><%=domain.getId()%>
				-
				<%=domain.getDomain()%></option>
			<%
						}
					%>
		</select> <br> <br> <br>
		<!-- <div align="center">

			<input id="submit" type="submit" style="margin-left: -600px"
				name="data" value="Submit" />


		</div> -->
<br> <br> <br>

	</form>


	<%
	if(request.getParameter("orgDomain")!=null){
    String orgDomain =request.getParameter("orgDomain");

    JSONArray jsonArray = new JSONArray();
    jsonArray = AdminAction.getAlertsPointsData(orgDomain);
    
    for(int j = 0; j <  jsonArray.length();j++) {
		 org.json.JSONObject jsonObj = jsonArray.getJSONObject(j);
		 
		
		 String arrival=  jsonObj.get("arrivalTime").toString() ;
		 long time =Long.parseLong(arrival);
		 long t1 = time;
		 if(time>t1){
			 receivedTime = time;
		 }
		 if(receivedTime != 0 || j==jsonArray.length()-1 ){
			 receiveddate= DateTimeUtil.getFormattedTime(time);
		 }
		 
    }
 %>

<%-- 	<%
  if(orgDomain != null && !orgDomain.isEmpty())
  {
  %> --%>
  
  <label><h5>Last Received Time :</h5></label><%=receiveddate %>
	<table style="margin-top: 40px;" class="table table-bordered  a">
		<tr>
			<th>S.No</th>
			<th>Time</th>
			<th>Agent</th>
			<th>Data</th>

		</tr>
		<%
		
		 for(int i = 0; i <  jsonArray.length();i++) {
			 org.json.JSONObject jsonObj = jsonArray.getJSONObject(i);
			 
			
			 String arrival=  jsonObj.get("arrivalTime").toString() ;
			 long time =Long.parseLong(arrival);
			 String date = DateTimeUtil.getFormattedTime(time);
			
			 System.out.println("Formated time is "+date);
	         
	       
		%>

		<tr>
			<td align="center"><%=i+1 %></td>
			<td align="center"><%=date %></td>
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

select {
	width: 15%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 5px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
	font-size: 13px;
}
</style>
</html>