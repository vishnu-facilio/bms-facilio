
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

out.println("started123");


List<Long> wfIgnoreIds = new ArrayList<>();		
// with if and iterator
wfIgnoreIds.add(3288l);
wfIgnoreIds.add(3397l);
wfIgnoreIds.add(3406l);
wfIgnoreIds.add(3624l);
wfIgnoreIds.add(3825l);
wfIgnoreIds.add(4691l);
wfIgnoreIds.add(4933l);

// with if in expr or result
wfIgnoreIds.add(4691l);
wfIgnoreIds.add(5536l);
wfIgnoreIds.add(5970l);
wfIgnoreIds.add(5971l);
wfIgnoreIds.add(5972l);
wfIgnoreIds.add(5975l);
wfIgnoreIds.add(5977l);
wfIgnoreIds.add(5978l);
wfIgnoreIds.add(5983l);
wfIgnoreIds.add(5984l);
wfIgnoreIds.add(5986l);
wfIgnoreIds.add(7227l);
wfIgnoreIds.add(7537l);
wfIgnoreIds.add(7599l);
wfIgnoreIds.add(2192l);
wfIgnoreIds.add(2269l);
wfIgnoreIds.add(2283l);
wfIgnoreIds.add(2285l);
wfIgnoreIds.add(2600l);
wfIgnoreIds.add(2601l);
wfIgnoreIds.add(2602l);
wfIgnoreIds.add(2717l);
wfIgnoreIds.add(2718l);
wfIgnoreIds.add(2719l);
wfIgnoreIds.add(2720l);
wfIgnoreIds.add(2721l);
wfIgnoreIds.add(2820l);
wfIgnoreIds.add(2838l);
wfIgnoreIds.add(3124l);
wfIgnoreIds.add(3125l);
wfIgnoreIds.add(3127l);
wfIgnoreIds.add(3128l);
wfIgnoreIds.add(3129l);
wfIgnoreIds.add(3130l);
wfIgnoreIds.add(3131l);
wfIgnoreIds.add(3132l);
wfIgnoreIds.add(3136l);
wfIgnoreIds.add(3137l);
wfIgnoreIds.add(3138l);
wfIgnoreIds.add(3139l);
wfIgnoreIds.add(3140l);
wfIgnoreIds.add(3141l);
wfIgnoreIds.add(3142l);
wfIgnoreIds.add(3167l);
wfIgnoreIds.add(3169l);
wfIgnoreIds.add(3191l);
wfIgnoreIds.add(3192l);
wfIgnoreIds.add(3194l);
wfIgnoreIds.add(3339l);
wfIgnoreIds.add(5289l);
wfIgnoreIds.add(5530l);
wfIgnoreIds.add(5607l);
wfIgnoreIds.add(5701l);
wfIgnoreIds.add(5702l);
wfIgnoreIds.add(5731l);
wfIgnoreIds.add(5753l);
wfIgnoreIds.add(7052l);
wfIgnoreIds.add(7203l);
wfIgnoreIds.add(7205l);
wfIgnoreIds.add(7228l);
wfIgnoreIds.add(7499l);
wfIgnoreIds.add(7500l);
wfIgnoreIds.add(7508l);

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
	
	if(orgid != 75) {
		continue;
	}
	
	AccountUtil.setCurrentAccount(orgid);
	
	out.println("orgid -- "+orgid);

	Condition condition1 = CriteriaAPI.getOrgIdCondition(orgid, ModuleFactory.getWorkflowModule());
	Condition condition2 = CriteriaAPI.getCondition("IS_V2", "isV2Script", Boolean.FALSE.toString(), BooleanOperators.IS);
	Criteria criteria = new Criteria();
	criteria.addAndCondition(condition1);
	criteria.addAndCondition(condition2);

	List<WorkflowContext> wfs = WorkflowUtil.getWorkflowContext(criteria);

	for(WorkflowContext workflow:wfs) {
		if(wfIgnoreIds.contains(workflow.getId())) {
			continue;
		}
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
			if(e.getMessage() != null && e.getMessage().contains("Workflow Contains '.'"))  {
				out.println("workflow With . fields  -- "+workflow.getId());
			}
			if(e.getMessage() != null && e.getMessage().contains("Content is not allowed in prolog"))  {
				out.println("xml parse exception  -- "+workflow.getId());
			}
			else {
				out.println("exception occured  -- "+workflow.getId() +" " +e.getMessage());
			}
		}
	}
}

out.println("done123");
%>
