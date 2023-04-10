<%@ page import="java.sql.Connection" %>
<%@ page import="com.facilio.db.transaction.FacilioConnectionPool" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.facilio.accounts.bean.OrgBean" %>
<%@ page import="com.facilio.time.DateTimeUtil" %>
<%@ page import="com.facilio.bmsconsole.jobs.monitoring.utils.MonitoringFeature" %>
<%@ page import="java.util.*" %>


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
    String jobRemoveDeletedPreOpenWorkOrdersJob = "RemoveDeletedPreOpenWorkOrdersJob";

    // SQL Queries
    String qForPMv1JobCheck = "SELECT COUNT(*) as res FROM Jobs WHERE ORGID IN (?) AND JOBNAME = ? AND EXECUTION_ERROR_COUNT > 0;";
    String qForOneTimeRecordWisePMv1JobCheck = "SELECT COUNT(*) as res FROM Jobs WHERE ORGID IN (?) AND JOBNAME = ? AND EXECUTION_ERROR_COUNT > 0;";
    String qForCheckingWO_GENERATION_STATUS = "SELECT COUNT(*) as res FROM Preventive_Maintenance where WO_GENERATION_STATUS = 1 AND ORGID IN (?);";
    String qToFetchLastMeta = "SELECT * FROM Monitoring_Tool_Meta WHERE ORGID IN (?) AND FEATURE = ? ORDER BY TTIME DESC LIMIT 1;";
    String qToFetchMetaOrgEntries = "SELECT * FROM Monitoring_Tool_Meta WHERE FEATURE = ? AND TTIME > ? AND TTIME < ?;";
    String qToFetchSingleSitePMCount = "SELECT ORGID, COUNT(ORGID) FROM Preventive_Maintenance WHERE PM_CREARTION_TYPE = 1 GROUP BY ORGID;";
    String qToFetchMultipleSitePMCount = "SELECT ORGID, COUNT(ORGID) FROM Preventive_Maintenance WHERE PM_CREARTION_TYPE = 2 GROUP BY ORGID;";
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
    jobNames.add(jobRemoveDeletedPreOpenWorkOrdersJob);

    List<String> oneTimeRecordWisePMv1JobNames = new ArrayList<>();
    // one-time record wise jobs
    oneTimeRecordWisePMv1JobNames.add(jobSchedulePMBackgroundJob);
    oneTimeRecordWisePMv1JobNames.add(jobScheduleNewPM);
    oneTimeRecordWisePMv1JobNames.add(jobOpenScheduledWO);

    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    Map<Long,String> orgDBMap = new HashMap<>();
    List<Long> activeOrgIds = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(orgs)) {
        for (Organization _org : orgs) {
            if (_org.getOrgId() > 0) {

                orgDBMap.put(_org.getId(), _org.getName());
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
    int failedRemoveDeletedPreOpenWorkOrdersJob = -1;

    for(String jobName: jobNames) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(qForPMv1JobCheck);
            pStmt.setString(1, StringUtils.join(activeOrgIds, ","));
            pStmt.setString(2, jobName);
            ResultSet rs = pStmt.executeQuery();
            //response.getWriter().println("pStmt -- "+ pStmt + "\n");
            while (rs.next()){
                int count = rs.getInt("res");
                //response.getWriter().println("count -- "+count);
                //int countDifference = activeOrgIds.size() - count;
                switch (jobName){
                    case "PMNewScheduler":
                        failedNightlySchedulerJobCount = count;
                        break;
                    case "ScheduleWOStatusChange":
                        failedScheduleWOStatusChangeJobCount = count;
                        break;
                    case "RemoveDeletedPreOpenWorkOrdersJob":
                        failedRemoveDeletedPreOpenWorkOrdersJob = count;
                        break;
                }
            }

        }
        catch (Exception e) {
            response.getWriter().println("1. pStmt error -- " + e);
            e.printStackTrace();
        }
    }

    for(String jobName: oneTimeRecordWisePMv1JobNames) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(qForOneTimeRecordWisePMv1JobCheck);
            pStmt.setString(1, StringUtils.join(activeOrgIds, ","));
            pStmt.setString(2, jobName);
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
            response.getWriter().println("2. pStmt error -- " + e);
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
    <tr>
        <td>Failed RemoveDeletedPreOpenWorkOrdersJob Count</td>
        <td><%=failedRemoveDeletedPreOpenWorkOrdersJob %></td>
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

<h4 style="margin-top:40px;">2. Enter ORG ID to check more details.</h4>
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
    String orgEntriesInMeta = "";
    try{
        long currentTime = System.currentTimeMillis();
        PreparedStatement pStmt = conn.prepareStatement(qToFetchMetaOrgEntries);
        pStmt.setInt(1, MonitoringFeature.PM_V1.getVal());
        pStmt.setLong(2, currentTime - 86400000);
        pStmt.setLong(3, currentTime);
        ResultSet rs = pStmt.executeQuery();

        HashSet<Long> orgIds = new HashSet<>();
        while (rs.next()){
            orgIds.add(rs.getLong("ORGID"));
        }

        orgEntriesInMeta = "ORG entries in past 24 hours: " + orgIds;
    }catch (Exception e){
        response.getWriter().println("error -- "+e+"<br>");
        e.printStackTrace();
    }
%>
<p><%=orgEntriesInMeta%></p>
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
            response.getWriter().println("3. pStmt error -- " + e);
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


<textarea readonly style="width: 1000px;height:400px;overflow-y:auto;">
<%
    if(org != null) {

        try {
            PreparedStatement pStmt = conn.prepareStatement(qToFetchLastMeta);
            pStmt.setLong(1, org.getOrgId());
            pStmt.setInt(2, MonitoringFeature.PM_V1.getVal());
            ResultSet rs = pStmt.executeQuery();
            StringBuilder metaStringBuilder = new StringBuilder();
            String meta = null;
            Long ttime = null;
            while(rs.next()) {
                ttime = rs.getLong("TTIME");
                meta = rs.getString("META");
            }
            if(ttime!=null && meta!=null){
                String formatedTime = DateTimeUtil.getFormattedTime(ttime);
                metaStringBuilder.append(formatedTime).append("\n\n\n").append(meta);
            }
            out.println(metaStringBuilder.toString());
        } catch(Exception e) {
            response.getWriter().println("4. pStmt error -- "+e+"<br>");
            e.printStackTrace();
        }

    }

%>
</textarea>
<br>
<br>

<% } // end of  if(org != null) :274 %>

<h4 style="margin-top:40px;">3. Single/Multiple Site PMs Count:</h4>
<%
    try {
        // Single Site PM count
        PreparedStatement pStmt = conn.prepareStatement(qToFetchSingleSitePMCount);
        ResultSet rs = pStmt.executeQuery();
        List<Map<Long, Long>> singleSitePMCount = new ArrayList<>();

        while (rs.next()){
            Map<Long, Long> count = new HashMap<>();
            count.put(rs.getLong("ORGID"), rs.getLong("COUNT(ORGID)"));
            singleSitePMCount.add(count);
        }
%>
<table style=" margin-top:40px;" class="table table-bordered">
    <tr>
        <th style="text-align:center">ORG ID</th>
        <th style="text-align:center">COUNT OF PMs</th>
    </tr>
    <tr style="text-align: center; font-weight: bold; background-color: #e0e0e0">
        <td colspan=3>
            Single Site PMs
        </td>
    </tr>
<%
    for (Map<Long, Long> singleSitePM: singleSitePMCount){
        if(!(singleSitePM.keySet()!= null && singleSitePM.keySet().toArray().length >  0)){
            continue;
        }

        Long orgId = (Long) singleSitePM.keySet().toArray()[0];
        String orgName = orgId +" " + orgDBMap.get(orgId);
        Long pmCount = singleSitePM.get(orgId);
%>
    <tr>
        <td><%=orgName%></td>
        <td><%=pmCount %></td>
    </tr>
<%
    }
%>
    <tr style="text-align: center; font-weight: bold; background-color: #e0e0e0">
        <td colspan=3>
            Multiple Site PMs
        </td>
    </tr>
<%
        // Multiple site PM count
        pStmt = conn.prepareStatement(qToFetchMultipleSitePMCount);
        rs = pStmt.executeQuery();
        List<Map<Long, Long>> multipleSitePMCount = new ArrayList<>();

        while (rs.next()){
            Map<Long, Long> count = new HashMap<>();
            count.put(rs.getLong("ORGID"), rs.getLong("COUNT(ORGID)"));
            multipleSitePMCount.add(count);
        }

        for (Map<Long, Long> multipleSitePM: multipleSitePMCount){
            if(!(multipleSitePM.keySet()!= null && multipleSitePM.keySet().toArray().length >  0)){
                continue;
            }

            Long orgId = (Long) multipleSitePM.keySet().toArray()[0];
            String orgName = orgId +" " + orgDBMap.get(orgId);
            Long pmCount = multipleSitePM.get(orgId);
%>
    <tr>
        <td><%=orgName%></td>
        <td><%=pmCount %></td>
    </tr>
<%
        }


    }catch(Exception e) {
        response.getWriter().println("5. pStmt error -- "+e+"<br>");
        e.printStackTrace();
    }
%>

</table>

</body>
</html>
