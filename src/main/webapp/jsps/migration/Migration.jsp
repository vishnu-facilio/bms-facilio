<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@ page import ="java.util.ArrayList"%>
<%@ page import ="java.util.List"%>
<%
List<Long> orgs = new ArrayList<Long>();
                orgs.add(143L);
                orgs.add(154L);
                orgs.add(168L);
                orgs.add(172L);
                orgs.add(191L);
                orgs.add(203L);
    PreventiveMaintenanceAPI.initScheduledWO(orgs);
%>
