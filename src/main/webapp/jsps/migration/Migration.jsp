<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>

<%
    List<Long> orgs = Arrays.asList(63L);
    PreventiveMaintenanceAPI.initScheduledWO(orgs);
%>