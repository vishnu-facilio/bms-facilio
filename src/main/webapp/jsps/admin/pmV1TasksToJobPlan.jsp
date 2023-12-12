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
<%@ page import="com.facilio.pmv1ToPmv2Migration.PMv1TasksToJobPlanMigration" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.beans.ModuleCRUDBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>

<%--
  Created by IntelliJ IDEA.
  User: kavinrajus
  Date: 30/08/23
  Time: 8:48 pm
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%!

%>

<%
    Organization org = null;
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
<h2>Migrate PMv1 Jobs to JobPlan <a id="sc"class="btn btn-default bttn" href="pmV1TasksToJobPlan" role="button">Refresh</a></h2>
<h4 style="margin-top:40px;">1. Enter PMv1 ID to migrate the tasks to JobPlan.</h4>
<%
    String orgid = request.getParameter("orgid");
    String pmv1Id = request.getParameter("pmv1Id");
    String targetOrgId = request.getParameter("targetOrgId");
    StringBuilder errorMessageBuilder = new StringBuilder();

    if (orgid != null && !orgid.equals("")) {
        long currentOrgId = AccountUtil.getCurrentOrg().getId();

        if (currentOrgId != Long.parseLong(orgid)){
            errorMessageBuilder.append("Not logged in account... Please use the currently logged in ORG ID...");
        }else {
            OrgBean orgBean = AccountUtil.getOrgBean();
            org = orgBean.getOrg(Long.parseLong(orgid));
            AccountUtil.setCurrentAccount(Long.parseLong(orgid));

            if (pmv1Id != null) {
                List<String> pmIdsStringList = Arrays.asList(pmv1Id.split(","));
                List<Long> pmV1Ids = new ArrayList<>();
                for (String pmIdStr : pmIdsStringList) {
                    try {
                        pmV1Ids.add(Long.valueOf(pmIdStr.trim()));
                    } catch (Exception e) {
                        errorMessageBuilder.append(e).append("\n\n");
                    }

                }
                try {
                    FacilioChain chain = FacilioChain.getTransactionChain();
                    chain.getContext().put("pmV1Ids", pmV1Ids);
                    chain.getContext().put("targetOrgId",Long.parseLong(targetOrgId));
                    chain.addCommand(new PMv1TasksToJobPlanMigration(pmV1Ids));
                    chain.execute();
                } catch (Exception e) {
                    errorMessageBuilder.append(e).append("/n/n");
                }
            }
            AccountUtil.cleanCurrentAccount();
        }
    }
%>
<form action="" method="GET">
    <div style="margin-top:15px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
        <p>OrgId:</p>
        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
        <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
    </div>
    <div style="margin-top:15px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
        <p>PMv1 ID:</p>
        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
        <input id="pmv1Id" type="text" value="<%= pmv1Id %>" class="form-control" name="pmv1Id" />
    </div>
    <div style="margin-top:15px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
            <p>Target ORGID:</p>
            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
            <input id="targetOrgId" type="text" value="<%= targetOrgId %>" class="form-control" name="targetOrgId" />
        </div>
    <div style="margin-top:30px;">
        <button  id="show" type="submit"  >Submit</button>
        <br>
        <p><%=org%></p>
        <p><%=pmv1Id%></p>
    </div>
</form>
<p><%=errorMessageBuilder.toString()%></p>

</body>
</html>