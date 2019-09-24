Mig to run a Job
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="java.util.Map"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.facilio.db.criteria.operators.BooleanOperators"%>
<%@page import="java.util.List"%>
<%@page import="com.facilio.db.criteria.Criteria"%>
<%@page import="com.facilio.db.criteria.Condition"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.workflows.context.WorkflowContext"%>
<%@page import="com.facilio.cards.util.CardType"%>
<%@page import="com.facilio.cards.util.CardUtil"%>
<%@page import="com.facilio.bmsconsole.jobs.*"%>
<%@page import="com.facilio.tasker.job.JobContext"%>

<%@page import="com.facilio.workflows.util.WorkflowUtil"%>
<%@page import="com.facilio.bmsconsole.util.ReadingsAPI"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>

<%

out.println("started");


List<Long> wfIgnoreIds = new ArrayList<>();
wfIgnoreIds.add(3288l);
wfIgnoreIds.add(3397l);
wfIgnoreIds.add(3406l);
wfIgnoreIds.add(3624l);
wfIgnoreIds.add(3825l);
wfIgnoreIds.add(4691l);
wfIgnoreIds.add(4933l);

FacilioField field = new FacilioField();
field.setName("orgId");
field.setDisplayName("Org Id");
field.setDataType(FieldType.NUMBER);
field.setColumnName("ORGID");


GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
List<FacilioField> fields = new ArrayList<>();
fields.add(field);
select.select(fields);
select.table("Organizations");

List<Map<String, Object>> orgids = select.get();

List<Map<String, Object>> props = new ArrayList<>();
for(Map<String, Object> org :orgids) {
	Long orgid = (Long) org.get("orgId");
	
	AccountUtil.setCurrentAccount(orgid);
	
	out.println("orgid -- "+orgid);

	Condition condition1 = CriteriaAPI.getOrgIdCondition(orgid, ModuleFactory.getWorkflowModule());
	Condition condition2 = CriteriaAPI.getCondition("IS_V2", "isV2Script", Boolean.FALSE.toString(), BooleanOperators.IS);
	Criteria criteria = new Criteria();
	criteria.addAndCondition(condition1);
	criteria.addAndCondition(condition2);

	List<WorkflowContext> wfs = WorkflowUtil.getWorkflowContext(criteria);

	for(WorkflowContext workflow:wfs) {
		try {
			out.println("current wf -- "+workflow.getId());
			workflow = WorkflowUtil.convertOldWorkflowToNew(workflow.getId());
			
			boolean res = workflow.validateWorkflow();
			
			if(!res) {
				out.println(workflow.getId()+" error occurs -----"+ workflow.getErrorListener().getErrorsAsString());
			}
			else {
				out.println(workflow.getId()+" -- passed");
			}
		}
		catch(Exception e) {
			out.println("exception occured  -- "+workflow.getId());
		}
	}
}

out.println("done123");
%>
