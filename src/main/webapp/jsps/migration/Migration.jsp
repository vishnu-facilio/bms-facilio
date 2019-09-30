
<%@page import="com.facilio.iam.accounts.util.IAMOrgUtil"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
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
<%@page import="java.util.HashMap" %>
<%@page import="java.io.File" %>
<%@page import="com.facilio.workflows.util.WorkflowUtil"%>
<%@page import="com.facilio.bmsconsole.util.ReadingsAPI"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>
<%@page import="com.facilio.bmsconsole.commands.AddDefaultWoStateflowCommand"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.modules.FacilioModule" %>
<%@page import="com.facilio.bmsconsole.util.StateFlowRulesAPI" %>
<%@page import="com.facilio.fw.BeanFactory" %>
<%@page import="com.facilio.bmsconsole.util.TicketAPI" %>
<%@page import="com.facilio.modules.FacilioStatus" %>
<%@page import="com.facilio.db.util.SQLScriptRunner" %>
<%@page import="com.facilio.db.builder.DBUtil" %>
<%@page import="com.facilio.constants.FacilioConstants.ContextNames" %>
<%@page import="com.facilio.db.util.DBConf" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext" %>
<%

out.println("Starting wo stateflow migration\n");

File INSERT_STATEFLOW_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/db/" + DBConf.getInstance().getDBName() + "/defaultWOStateflow.sql").getFile());

FacilioField field = new FacilioField();
field.setName("orgId");
field.setDisplayName("Org Id");
field.setDataType(FieldType.NUMBER);
field.setColumnName("ORGID");


List<Organization> orgs = IAMOrgUtil.getOrgs();
for(Organization org :orgs) {
	long assignedStateId = -1;

	Long orgId = (Long) org.getId();
	AccountUtil.setCurrentAccount(orgId);
	
	Map<String, String> paramValues = new HashMap<>(); 
	paramValues.put("orgId", String.valueOf(orgId));

	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
	FacilioModule module = modBean.getModule(ContextNames.WORK_ORDER);
	
	if(module == null) {
		continue;
	}
	
	StateFlowRuleContext stateflowContext = StateFlowRulesAPI.getDefaultStateFlow(module);

	if(stateflowContext.getDiagramJson() != null) {
		continue;
	}
	
	out.println("\nOrgId: " + String.valueOf(orgId));
	
	paramValues.put("stateflowId", String.valueOf(stateflowContext.getId()));

	List<WorkflowRuleContext> transitions = StateFlowRulesAPI.getAllStateTransitionList(stateflowContext);
	List<FacilioStatus> states = TicketAPI.getAllStatus(module, true);

	for (FacilioStatus state :states) {
		if(state.getStatus().equals("Assigned")) {
			assignedStateId = state.getId();
		}
		paramValues.put(state.getStatus(), String.valueOf(state.getId()));
	}
	
	for(WorkflowRuleContext t :transitions) {
		StateflowTransitionContext transition = (StateflowTransitionContext) t;

		if(transition.getName().equals("Close") && transition.getFromStateId() == assignedStateId) {
			paramValues.put("Close-1", String.valueOf(t.getId()));
		} else {
			paramValues.put(t.getName(), String.valueOf(t.getId()));	
		}
	}


	System.out.println(paramValues);
	SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_STATEFLOW_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
	scriptRunner.runScript();
}

out.println("\n\ncomplete");
%>
