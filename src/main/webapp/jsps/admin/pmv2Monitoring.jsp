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

	String qWoForDeletedPMs = "SELECT count(*) as res from WorkOrders inner join Tickets on WorkOrders.ID = Tickets.ID where PM_V2_ID IN(select Tickets.id from Tickets inner join PM_V2 on Tickets.ID = PM_V2.ID where SYS_DELETED = 1) and MODULE_STATE is null and (SYS_DELETED is null or sys_deleted = 0)";
	String qWoForDeletedTriggers = "SELECT count(*) as res from WorkOrders inner join Tickets on WorkOrders.ID = Tickets.ID where PM_V2_Trigger_ID IN(SELECT ID from PM_V2_Trigger WHERE SYS_DELETED  = 1) and MODULE_STATE is null and (SYS_DELETED is null or sys_deleted = 0);";
	String qWoForDeletedPlanners = "SELECT count(*) as res from WorkOrders inner join Tickets on WorkOrders.ID = Tickets.ID where PM_PLANNER_ID IN(SELECT ID from PM_Planner WHERE SYS_DELETED  = 1) and MODULE_STATE is null and (SYS_DELETED is null or sys_deleted = 0);";
	String qWoForDisabledPMs = "SELECT count(*) as res from WorkOrders inner join Tickets on WorkOrders.ID = Tickets.ID where PM_V2_ID IN(select Tickets.id from Tickets inner join PM_V2 on Tickets.ID = PM_V2.ID where (SYS_DELETED is null or sys_deleted = 0) and PM_STATUS = 1) and MODULE_STATE is null and (SYS_DELETED is null or sys_deleted = 0);";
	String qToFetchLastMeta = "select * from Monitoring_Tool_Meta WHERE FEATURE = ? order by ttime desc limit 1;";
	String qToFetchStatusOfNightlyScheduler = "select count(*) as res from Jobs where jobname = 'PMV2NightlyScheduler' and is_active = 1 and status = 3 and EXECUTION_ERROR_COUNT = 0 and orgid in (?)";
	
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
	response.getWriter().println("orgDBMap -- "+orgDBMap);
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

<h2><i class=" fa fa-building-o  fa-fw"></i>PM V2</h2>

<h2>Section 1 : db wise</h2>

<% for(String dbs : orgDBMap.keySet()) { 

	Long orgid = orgDBMap.get(dbs);
	
	int qWoForDeletedPMsRes = -1,qWoForDeletedTriggersRes = -1, qWoForDeletedPlannersRes = -1, qWoForDisabledPMsRes = -1, failedNightlySchedulerCount = -1;
	
	AccountUtil.setCurrentAccount(orgid);
	
	try(ResultSet rs = conn.prepareStatement(qWoForDeletedPMs).executeQuery();) {
		while(rs.next()) { qWoForDeletedPMsRes = rs.getInt("res"); }
	}
	
	try(ResultSet rs = conn.prepareStatement(qWoForDeletedTriggers).executeQuery();) {
		while(rs.next()) { qWoForDeletedTriggersRes = rs.getInt("res"); }
	}
	
	try(ResultSet rs = conn.prepareStatement(qWoForDeletedPlanners).executeQuery();) {
		while(rs.next()) { qWoForDeletedPlannersRes = rs.getInt("res"); }
	}
	
	try(ResultSet rs = conn.prepareStatement(qWoForDisabledPMs).executeQuery();) {
		while(rs.next()) { qWoForDisabledPMsRes = rs.getInt("res"); }
	}
	
	try(ResultSet rs = conn.prepareStatement(qWoForDisabledPMs).executeQuery();) {
		while(rs.next()) { qWoForDisabledPMsRes = rs.getInt("res"); }
	}
	
	AccountUtil.cleanCurrentAccount();
%>

	<h3><%=dbs%></h3>
	<table style=" margin-top:40px;" class="table table-bordered">
		<tr>
			<th style="text-align:center">PROPS</th>
			<th style="text-align:center">VALUES</th>
		</tr>
	
		<tr>
			<td>WO For Deleted PMs</td>
			<td><%=qWoForDeletedPMsRes %></td>
		</tr>
		
		<tr>
			<td>WO For deleted Planners</td>
			<td><%=qWoForDeletedPlannersRes %></td>
		</tr>
		
		<tr>
			<td>WO For deleted Triggers</td>
			<td><%=qWoForDeletedTriggersRes %></td>
		</tr>
		
		<tr>
			<td>WO For disabled PMs</td>
			<td><%=qWoForDisabledPMsRes %></td>
		</tr>
		
	</table>
<% } %>


<h2>Section 2 : public DB</h2>

<%  

	int failedNightlySchedulerCount = -1;
	
	try {
		PreparedStatement pStmt = conn.prepareStatement(qToFetchStatusOfNightlyScheduler);
		pStmt.setString(1, StringUtils.join(activeOrgIds, ","));
		ResultSet rs = pStmt.executeQuery();
		response.getWriter().println("pStmt -- "+pStmt);
		while(rs.next()) {
			int count = rs.getInt("res");
			
			response.getWriter().println("count -- "+count);
			
			failedNightlySchedulerCount = activeOrgIds.size() - count;
		}
	}
	catch(Exception e) {
		response.getWriter().println("pStmt error -- "+e);
		e.printStackTrace();
	}
	
%>

	
	<table style=" margin-top:40px;" class="table table-bordered">
		<tr>
			<th style="text-align:center">PROPS</th>
			<th style="text-align:center">VALUES</th>
		</tr>
	
		<tr>
			<td>Failed Nightly Scheduler Count</td>
			<td><%=failedNightlySchedulerCount %></td>
		</tr>
	</table>


<h2>Section 2 <a id="sc"class="btn btn-default bttn" href="pmMetaRefresh" role="button">Refresh</a></h2>

<br>
<br>
<br>Execution Meta = <textarea style="width: 1000px;height:400px;overflow-y:auto;">
<%
	//
	
	try {
		PreparedStatement pStmt = conn.prepareStatement(qToFetchLastMeta);
		pStmt.setInt(1, MonitoringFeature.PM_V2.getVal());
		ResultSet rs = pStmt.executeQuery();
		String meta = null;
		Long ttime = null;
		while(rs.next()) { 
			ttime = rs.getLong("TTIME"); 
			meta = rs.getString("META"); 
		}
		if(ttime!=null && meta!=null){
            String formatedTime = DateTimeUtil.getFormattedTime(ttime);
            meta = formatedTime + "\n\n\n" + meta;
		}
		out.println(meta);
	} catch(Exception e) {
         response.getWriter().println("pStmt error -- "+e+"<br>");
         e.printStackTrace();
    }
	
%>
</textarea>
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