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
<%@ page import="com.facilio.bmsconsole.context.ApplicationContext" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgid = request.getParameter("orgid");
    Organization org = null;

    List<ApplicationContext> allApps = new ArrayList<>();
    if (orgid != null) {
        long orgId = Long.parseLong(orgid);
        OrgBean orgBean = AccountUtil.getOrgBean();
        org = orgBean.getOrg(Long.parseLong(orgid));
        allApps  = AccountUtil.getOrgBean(orgId).getAllApplications(orgId);
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>

<body>
<form method="GET">
    <h1>Application Status</h1>
    <div style="margin-top:40px;align-items: center;display: flex" class="input-group col-lg-8 col-md-8 col-sm-8	">
        <span class="table-head">Org ID</span>
        <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="org-id-input" name="orgid" />
        <button class="button-10" id="next" type="submit">Next</button>
    </div>
</form>
<% if (orgid!=null) { %>

<form method="POST" ACTION="setappstatus">

    <input type = "hidden" name = "orgid" value="<%= orgid %>" />
    <table style="margin-top:20px;width: 200px"  class="table table-bordered">
        <tr>
            <th>Application</th>
            <th>Status</th>
        </tr>

        <%
            for(ApplicationContext app  :allApps)
            {
        %>
        <tr>
            <td>
                <label style="min-width: 200px"><%=app.getName()%></label>
            </td>
            <td style="text-align:center;">
                <input type = "checkbox" <% if (app.isActive()) { %> checked <%  }%> name="selected" value="<%=app.getId()%>" />
                <br>
            </td>
        </tr>
        <%  } %>

    </table>

    <%
        if(ApplicationApi.isAdminControlAllowed()) {
    %>
    <button type="submit" class="button-10" style="width: 80px" id="save" type="setappstatus">Save</button>
    <%
        }
    %>
</form>
<% } %>


<style>
    .button-10 {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 6px 14px;
        font-family: -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif;
        border-radius: 6px;
        border: none;

        color: #fff;
        background: linear-gradient(180deg, #4B91F7 0%, #367AF6 100%);
        background-origin: border-box;
        box-shadow: 0px 0.5px 1.5px rgba(54, 122, 246, 0.25), inset 0px 0.8px 0px -0.25px rgba(255, 255, 255, 0.2);
        user-select: none;
        -webkit-user-select: none;
        touch-action: manipulation;
    }

    .button-10:focus {
        box-shadow: inset 0px 0.8px 0px -0.25px rgba(255, 255, 255, 0.2), 0px 0.5px 1.5px rgba(54, 122, 246, 0.25), 0px 0px 0px 3.5px rgba(58, 108, 217, 0.5);
        outline: 0;
    }
    .table-head {
        width: 60px;
        font-weight: 500;
        float: left;
        font-size: 14px;
    }
    .org-id-input {
        max-width: 400px;
        float: left;
        height: 20px;
        padding: 0px;
    }
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