<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.time.DateTimeUtil"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.facilio.db.transaction.FacilioConnectionPool"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
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
<%@ page import="com.facilio.bmsconsole.jobs.monitoring.utils.MonitoringFeature" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%
	Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
	List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
	Map<String,Long> orgDBMap = new HashMap<>();
	List<Long> activeOrgIds = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(orgs)) {
	    for (Organization org : orgs) {
	        if (org.getOrgId() > 0) {
	        	orgDBMap.put(org.getDataSource(), org.getId());
	        	activeOrgIds.add(org.getOrgId());
	        }
	    }
	}
	response.getWriter().println("orgDBMap -- "+orgDBMap+"<br>");
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

<br><br><br><br>
<h2><i class=" fa fa-building-o  fa-fw"></i>Service Request Monitoring Tool</h2>

<h2>Section 1 : public DB</h2>

<%
	int failedSrMailCount = 0;
	try {
	    String failedSrMail = "select count(*) as res from WorkOrderRequest_EMail where state=2";
		PreparedStatement pStmt = conn.prepareStatement(failedSrMail);
		ResultSet rs = pStmt.executeQuery();
		response.getWriter().println("pStmt -- "+pStmt+"<br>");
		while(rs.next()) {
			int count = rs.getInt("res");
			response.getWriter().println("count -- "+count+"<br>");
			failedSrMailCount = count;
		}
	}
	catch(Exception e) {
		response.getWriter().println("pStmt error -- "+e+"<br>");
		e.printStackTrace();
	}

%>


<table style=" margin-top:40px;" class="table table-bordered">
    <tr>
        <th style="text-align:center">PROPS</th>
        <th style="text-align:center">VALUES</th>
    </tr>

    <tr>
        <td>Failed Records Count</td>
        <td><%=failedSrMailCount %></td>
    </tr>

</table>

<br>
<br>

<br>
<br>

<% conn.close(); %>

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