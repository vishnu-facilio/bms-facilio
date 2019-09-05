<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@ page import ="java.util.ArrayList"%>
<%@ page import ="java.util.List"%>
<%
List<Long> orgs = PreventiveMaintenanceAPI.getOrg();
PreventiveMaintenanceAPI.migrateScheduleGeneration(orgs);
%>