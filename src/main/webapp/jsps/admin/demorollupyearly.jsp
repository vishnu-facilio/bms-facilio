<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.accounts.dto.Organization"%>  
<%
List<Organization> org = null;
OrgBean bean =  AccountUtil.getOrgBean();
org = bean.getOrgs();
System.out.println("org is :"+org);
%>    


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
  <form action="demoRollUpYearly">
    <label for="orgId">
			<div class="admin-data-grey">Org:</div>
				</label>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;
				<select class="admin-data-select"
					name="orgId" id="orgId">
					<option value="" disabled selected>Select</option>
					<%
								for (Organization domain : org) {
									
							%>
					<option value="<%= domain.getId()%>"><%=domain.getId()%>
						-
						<%=domain.getDomain()%></option>
					<%
								}
							%>
				</select>
				<br>
	<label for="lastYearStartTime"><h5>DateTime:</h5></label>
	<div align="center">
	<input type = "datetime-local" style="margin-left: -550px" name="lastYearStartTime" /> 
    </div>
    <br>
    <br>
    <input type="submit" style="margin-left: 200px"name="Submit" value="Submit" />
  </form>
</body>
<style>
input[type=text]{
  width: 12%;
  padding: 12px 20px;
  margin: 8px 16px;
  margin-top:8px;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type=submit] {
  width: 13%;
  background-color: #4CAF50;
  color: white;
  padding: 12px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
 font-size: 15px;
}

input[type=submit]:hover {
  background-color: #45a049;
}
.admin-data-grey{
	color: #333;
	font-size: 17px;
	letter-spacing: 0.5px;
	font-weight: 400;
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


</style>
</html>