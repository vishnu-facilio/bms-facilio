<%@page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI"%>

<%
    long accountId = Long.parseLong(request.getParameter("orgId"));
    boolean doMigration = Boolean.parseBoolean(request.getParameter("doMig"));
	PreventiveMaintenanceAPI.populateUniqueId(accountId, doMigration);
%>
//this is a testcommit for lambda
//testing if lambda is triggered