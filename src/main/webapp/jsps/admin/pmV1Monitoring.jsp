<%@ page import="java.sql.Connection" %>
<%@ page import="com.facilio.db.transaction.FacilioConnectionPool" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.facilio.accounts.bean.OrgBean" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.facilio.time.DateTimeUtil" %>
<%@ page import="com.facilio.bmsconsole.jobs.monitoring.utils.MonitoringFeature" %>


<%--
  Created by IntelliJ IDEA.
  User: kavinrajus
  Date: 02/03/23
  Time: 8:35 am
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%!
    // PM v1 Job names
    String jobPMNewScheduler = "PMNewScheduler";
    String jobSchedulePMBackgroundJob = "SchedulePMBackgroundJob";
    String jobScheduleNewPM = "ScheduleNewPM";
    String jobOpenScheduledWO = "OpenScheduledWO";
    String jobScheduleWOStatusChange = "ScheduleWOStatusChange";

    // SQL Queries
    String qForPMv1JobCheck = "SELECT COUNT(*) as res FROM Jobs WHERE JOBNAME = ? AND EXECUTION_ERROR_COUNT = 0 AND STATUS = 3 AND ORGID IN (?);";
    String qForOneTimeRecordWisePMv1JobCheck = "SELECT COUNT(*) as res FROM Jobs WHERE JOBNAME = ? AND EXECUTION_ERROR_COUNT > 0 AND ORGID IN (?);";
    String qForCheckingWO_GENERATION_STATUS = "SELECT COUNT(*) as res FROM Preventive_Maintenance where WO_GENERATION_STATUS = 1 AND ORGID IN (?);";
    String qToFetchLastMeta = "SELECT * FROM Monitoring_Tool_Meta WHERE FEATURE = ? ORDER BY TTIME DESC LIMIT 1;";
%>

<%
    Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
    Organization org = null;

    // SQL Query
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT COUNT(*) as res FROM WorkOrders INNER JOIN Tickets ON WorkOrders.ID = Tickets.ID ")
            .append(" WHERE WorkOrders.ORGID = ? AND (WorkOrders.PM_ID IS NOT NULL) ")
            .append(" AND Tickets.MODULE_STATE IS NULL AND Tickets.SYS_DELETED IS NULL and WorkOrders.JOB_STATUS = 1 ")
            .append(" AND WorkOrders.CREATED_TIME >= (UNIX_TIMESTAMP() * 1000 - 86400000) AND WorkOrders.CREATED_TIME <= (UNIX_TIMESTAMP() * 1000);");
    String qForPreOpenWorkOrdersForPast24hours =  stringBuilder.toString();

    stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT COUNT(*) as res FROM WorkOrders INNER JOIN Tickets ON WorkOrders.ID = Tickets.ID ")
            .append(" WHERE WorkOrders.ORGID = ? AND (WorkOrders.PM_ID IS NOT NULL) ")
            .append(" AND Tickets.MODULE_STATE IS NULL AND Tickets.SYS_DELETED IS NULL and WorkOrders.JOB_STATUS = 1 ")
            .append(" AND WorkOrders.CREATED_TIME >= (UNIX_TIMESTAMP()* 1000) AND WorkOrders.CREATED_TIME <= (UNIX_TIMESTAMP() * 1000 + 86400000);");

    String qForPreOpenWorkOrdersForNext24hours =  stringBuilder.toString();


    List<String> jobNames = new ArrayList<>();
    jobNames.add(jobPMNewScheduler);
    jobNames.add(jobScheduleWOStatusChange);

    List<String> oneTimeRecordWisePMv1JobNames = new ArrayList<>();
    // one-time record wise jobs
    oneTimeRecordWisePMv1JobNames.add(jobSchedulePMBackgroundJob);
    oneTimeRecordWisePMv1JobNames.add(jobScheduleNewPM);
    oneTimeRecordWisePMv1JobNames.add(jobOpenScheduledWO);

    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    Map<String,Long> orgDBMap = new HashMap<>();
    List<Long> activeOrgIds = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(orgs)) {
        for (Organization _org : orgs) {
            if (_org.getOrgId() > 0) {

                orgDBMap.put(_org.getDataSource(), _org.getId());
                activeOrgIds.add(_org.getOrgId());
            }
        }
    }
    //response.getWriter().println("orgDBMap -- "+orgDBMap + "\n");
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
<h2>PM V1 Monitoring <a id="sc"class="btn btn-default bttn" href="pmV1Monitoring" role="button">Refresh</a></h2>
<h4>1. Jobs:</h4>
<%
    int failedNightlySchedulerJobCount = -1;
    int failedSchedulePMBackgroundJobCount = -1;
    int failedScheduleNewPMJobCount = -1;
    int failedOpenScheduledWOJobCount = -1;
    int failedScheduleWOStatusChangeJobCount = -1;

    for(String jobName: jobNames) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(qForPMv1JobCheck);
            pStmt.setString(1, jobName);
            pStmt.setString(2, StringUtils.join(activeOrgIds, ","));
            ResultSet rs = pStmt.executeQuery();
            //response.getWriter().println("pStmt -- "+ pStmt + "\n");
            while (rs.next()){
                int count = rs.getInt("res");
                //response.getWriter().println("count -- "+count);
                int countDifference = activeOrgIds.size() - count;
                switch (jobName){
                    case "PMNewScheduler":
                        failedNightlySchedulerJobCount = countDifference;
                        break;
                    case "ScheduleWOStatusChange":
                        failedScheduleWOStatusChangeJobCount = countDifference;
                        break;
                }
            }

        }
        catch (Exception e) {
            response.getWriter().println("pStmt error -- " + e);
            e.printStackTrace();
        }
    }

    for(String jobName: oneTimeRecordWisePMv1JobNames) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(qForOneTimeRecordWisePMv1JobCheck);
            pStmt.setString(1, jobName);
            pStmt.setString(2, StringUtils.join(activeOrgIds, ","));
            ResultSet rs = pStmt.executeQuery();
            //response.getWriter().println("pStmt -- "+ pStmt + "\n");
            while (rs.next()){
                int count = rs.getInt("res");
                //response.getWriter().println("count -- "+count);
                switch (jobName){
                    case "SchedulePMBackgroundJob":
                        failedSchedulePMBackgroundJobCount = count;
                        break;
                    case "ScheduleNewPM":
                        failedScheduleNewPMJobCount = count;
                        break;
                    case "OpenScheduledWO":
                        failedOpenScheduledWOJobCount = count;
                        break;
                }
            }

        }
        catch (Exception e) {
            response.getWriter().println("pStmt error -- " + e);
            e.printStackTrace();
        }
    }
%>

<table style=" margin-top:40px;" class="table table-bordered">
    <!-- HEADER -->
    <tr>
        <th style="text-align:center">PROPS</th>
        <th style="text-align:center">VALUE</th>
        <th style="text-align:center">EXPECTED VALUE</th>
    </tr>

    <!-- RECORDS -->
    <!-- Daily Jobs -->
    <tr style="text-align: center; font-weight: bold; background-color: #e0e0e0">
        <td colspan=3>
            Daily Jobs
        </td>
    </tr>
    <tr>
        <td>Failed Nightly Scheduler Count</td>
        <td><%=failedNightlySchedulerJobCount %></td>
        <td>0</td>
    </tr>

    <!-- 30 Mins Jobs -->
    <tr style="text-align: center; font-weight: bold; background-color: #e0e0e0">
        <td colspan=3>
            30 Mins Jobs
        </td>
    </tr>
    <tr>
        <td>Failed WO status change Job Count (ScheduleWOStatusChange)</td>
        <td><%=failedScheduleWOStatusChangeJobCount %></td>
        <td>0</td>
    </tr>

    <!-- Record wise Jobs -->
    <tr style="text-align: center; font-weight: bold; background-color: #e0e0e0">
        <td colspan=3>
            Record wise Jobs
        </td>
    </tr>
    <tr>
        <td>Failed WO Generation Job Count - 1 (SchedulePMBackgroundJob)</td>
        <td><%=failedSchedulePMBackgroundJobCount %></td>
        <td>0</td>
    </tr>
    <tr>
        <td>Failed WO Generation Job Count - 2 (ScheduleNewPM)</td>
        <td><%=failedScheduleNewPMJobCount %></td>
        <td>0</td>
    </tr>
    <tr>
        <td>Failed Pre-Open to Open WO conversion Job Count (OpenScheduledWO)</td>
        <td><%=failedOpenScheduledWOJobCount %></td>
        <td>0</td>
    </tr>
</table>

<br>
<br>
<h4 style="margin-top:40px;">2. Execution Meta:</h4>
<textarea readonly style="width: 1000px;height:400px;overflow-y:auto;">
<%
    //

    try {
        PreparedStatement pStmt = conn.prepareStatement(qToFetchLastMeta);
        pStmt.setInt(1, MonitoringFeature.PM_V1.getVal());
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

<h4 style="margin-top:40px;">3. Enter ORG ID to check more details.</h4>
<%
    String orgid = request.getParameter("orgid");

    if (orgid != null) {
        OrgBean orgBean = AccountUtil.getOrgBean();
        org = orgBean.getOrg(Long.parseLong(orgid));
    }
%>
<form action="" method="GET">
<div style="margin-top:15px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
    <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
</div>
<div style="margin-top:30px;">
    <button  id="show" type="submit"  >Submit</button>
    <p><%=org%></p>
</div>
</form>
<%
    long countOfPreOpenWorkOrdersForPast24Hours = 0;
    long countOfPreOpenWorkOrdersForNext24Hours = 0;
    long countOfPMsWith_WO_GENERATION_STATUS_SetTo1 = 0;

    if(org != null) {
        try {
            // 1.
            PreparedStatement pStmt = conn.prepareStatement(qForPreOpenWorkOrdersForPast24hours);
            pStmt.setLong(1, org.getOrgId());
            ResultSet rs = pStmt.executeQuery();
            //response.getWriter().println("pStmt -- "+ pStmt + "\n");
            if (rs.next()) {
                long count = rs.getInt("res");
                //response.getWriter().println("count -- "+count);
                if (count > 0) {
                    countOfPreOpenWorkOrdersForPast24Hours = count;
                }
            }

            // 2.
            pStmt = conn.prepareStatement(qForPreOpenWorkOrdersForNext24hours);
            pStmt.setLong(1, org.getOrgId());
            rs = pStmt.executeQuery();
            //response.getWriter().println("pStmt -- "+ pStmt + "\n");
            if (rs.next()) {
                long count = rs.getInt("res");
                //response.getWriter().println("count -- "+count);
                if (count > 0) {
                    countOfPreOpenWorkOrdersForNext24Hours = count;
                }
            }

            // 3.
            pStmt = conn.prepareStatement(qForCheckingWO_GENERATION_STATUS);
            pStmt.setLong(1, org.getOrgId());
            rs = pStmt.executeQuery();
            if (rs.next()){
                long count = rs.getLong("res");
                if (count > 0){
                    countOfPMsWith_WO_GENERATION_STATUS_SetTo1 = count;
                }
            }


        }
        catch (Exception e) {
            response.getWriter().println("pStmt error -- " + e);
            e.printStackTrace();
        }
%>

<table style=" margin-top:40px;" class="table table-bordered">
    <tr>
        <th style="text-align:center">PROPS</th>
        <th style="text-align:center">VALUE</th>
        <th style="text-align:center">EXPECTED VALUE</th>
    </tr>

    <tr>
        <td>Count of PreOpen WorkOrders for past 24-Hours</td>
        <td><%=countOfPreOpenWorkOrdersForPast24Hours %></td>
        <td>0</td>
    </tr>
    <tr>
        <td>Count of PreOpen WorkOrders for next 24-Hours</td>
        <td><%=countOfPreOpenWorkOrdersForNext24Hours %></td>
        <td>>=0</td>
    </tr>
    <tr>
        <td>WO_GENERATION_STATUS set to 1</td>
        <td><%=countOfPMsWith_WO_GENERATION_STATUS_SetTo1 %></td>
        <td>0</td>
    </tr>

</table>

<% } // end of  if(org != null) :274 %>
</body>
</html>
