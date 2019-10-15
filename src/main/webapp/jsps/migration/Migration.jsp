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
<%@page import="com.facilio.bmsconsole.util.ReadingsAPI"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>
<%


GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
List<FacilioField> fields = new ArrayList<>();

FacilioField field = new FacilioField();
field.setName("orgId");
field.setDisplayName("Org Id");
field.setDataType(FieldType.NUMBER);
field.setColumnName("ORGID");
fields.add(field);
select.select(fields);
select.table("Organizations");

List<Map<String, Object>> orgids = select.get();

List<Map<String, Object>> props = new ArrayList<>();
for(Map<String, Object> org :orgids) {
	Long orgid = (Long) org.get("orgId");
	AccountUtil.setCurrentAccount(orgid);
	PreventiveMaintenanceAPI.migrateJobs(orgid);
}

%>
