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
<%@ page import="com.facilio.bmsconsole.util.WorkOrderAPI" %>
<%@page import="com.facilio.tasker.job.JobContext"%>

<%@page import="com.facilio.workflows.util.WorkflowUtil"%>
<%@page import="com.facilio.workflows.context.*"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>
<%

long orgid = 78l; 

	AccountUtil.setCurrentAccount(orgid);
	
	Condition conditionContext =  CriteriaAPI.getOrgIdCondition(orgid, ModuleFactory.getWorkflowModule());
	
	Condition vidCondition = CriteriaAPI.getCondition("IS_V2", "isV2Script", Boolean.FALSE.toString(), BooleanOperators.IS);
	
	Criteria cri = new Criteria();
	
	cri.setPattern("1 and 2");
	
	Map<String, Condition> conditionMap = new HashMap<>();
	conditionMap.put("1", conditionContext);
	conditionMap.put("2", vidCondition);
	
	cri.setConditions(conditionMap);

	List<WorkflowContext> wfs = WorkflowUtil.getWorkflowContext(cri);
	
	for(WorkflowContext wf : wfs) {
		try {
			
			wf = WorkflowUtil.getWorkflowContextFromString(wf.getWorkflowString(),wf);
			WorkflowUtil.parseExpression(wf);
			wf = WorkflowUtil.convertOldWorkflowToNew(wf);
			wf.setIsV2Script(true);
			
			out.println(wf.getWorkflowV2String());
			out.println("wf validate res -- "+wf.validateWorkflow());
			
		}
		catch(Exception e) {
			out.println("problem with wf of id - "+wf.getId()+" --- "+e.getMessage());
		}
	}
    
%>