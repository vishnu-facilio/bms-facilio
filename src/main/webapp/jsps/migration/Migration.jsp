<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@ page import ="java.util.ArrayList"%>
<%@ page import ="java.util.List"%>
<%
List<Long> orgs = new ArrayList<Long>();
                orgs.add(99L);
    PreventiveMaintenanceAPI.initScheduledWO(orgs);
%>