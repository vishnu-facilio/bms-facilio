<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.aws.util.AwsUtil" %>

<%
    long orgId = AccountUtil.getCurrentOrg().getOrgId();
    if (orgId == 183) {
        PreventiveMaintenanceAPI.initScheduledWO();
    }
%>