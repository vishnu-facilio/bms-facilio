<%@page import="com.facilio.db.criteria.operators.BooleanOperators"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.facilio.db.criteria.Criteria"%>
<%@page import="com.facilio.workflowv2.autogens.WorkflowV2Parser.CriteriaContext"%>
<%@page import="com.facilio.db.criteria.Condition"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="java.util.List"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@page import="com.facilio.tasker.job.JobContext"%>

<%@page import="com.facilio.workflows.util.WorkflowUtil"%>
<%@page import="com.facilio.workflows.context.*"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>
<%
	long accountId = Long.parseLong(request.getParameter("orgId"));
    boolean doMigration = Boolean.parseBoolean(request.getParameter("doMig"));
    long pmId = Long.parseLong(request.getParameter("pmId"));
	PreventiveMaintenanceAPI.findMissedTriggerSelection(accountId, doMigration, pmId);
%>