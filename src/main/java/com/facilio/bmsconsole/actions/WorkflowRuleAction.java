package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.FormulaAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionType;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.AssignmentTemplate;
import com.facilio.bmsconsole.workflow.SLATemplate;
import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;
import com.facilio.bmsconsole.modules.FacilioField;

public class WorkflowRuleAction extends ActionSupport {
	
	public String execute() throws Exception 
	{
	    return SUCCESS;
	}
	WorkflowRuleContext workflowRuleContext;
	public WorkflowRuleContext getWorkflowRuleContext() {
		return workflowRuleContext;
	}

	public void setWorkflowRuleContext(WorkflowRuleContext workflowRuleContext) {
		this.workflowRuleContext = workflowRuleContext;
	}

	public String assignmentRules() throws Exception 
	{
		setRules(WorkflowAPI.getWorkflowRules());
	    return "assignmentRules";
	}
	
	public String newAssignmentRule() throws Exception 
	{
//		List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(orgId, true);
//		assignmentGroupList = new HashMap<>();
//		if (groups != null && groups.size() > 0) {
//			for(Group group : groups) {
//				assignmentGroupList.put(group.getGroupId(), group.getName());
//			}
//		}
//		
//		assignedToList = UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.USER.getValue());
		return "newAssignmentRule";
	}
	private JSONObject payload;
	public JSONObject getPayload() {
		return payload;
	}
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	public String addReadingThresholdRulesWithFormula() throws Exception {
		workflowRuleContext = this.workflowRuleContext;
		FormulaAPI.addFormula(workflowRuleContext.getCriteria().getFormulaContext());
		
		
		workflowRuleContext.getCriteria().setFormulaId(workflowRuleContext.getCriteria().getFormulaContext().getId());
		
		WorkflowAPI.addWorkflowRule(workflowRuleContext);
		return SUCCESS;
	}
	public String runThroughFilters() throws Exception {
		
		String moduleName = (String) payload.get("module");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule facilioModule = modBean.getModule(moduleName);
		String hour = (String) payload.get("hour");
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(modBean.getAllFields(moduleName))
				.table(facilioModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", String.valueOf(hour), DateOperators.LAST_N_HOURS));
		
		List<Map<String, Object>> records = selectBuilder.get();
		
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		facilioContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		facilioContext.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		
		Chain runThroughFilters = FacilioChainFactory.runThroughFilters();
		runThroughFilters.execute(facilioContext);
		
		return "s";
	}
	
	private long assetId;
	
	public long getAssetId() {
		return assetId;
	}

	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	private ReadingRuleContext readingRule;
	public ReadingRuleContext getReadingRule() {
		return readingRule;
	}
	public void setReadingRule(ReadingRuleContext readingRule) {
		this.readingRule = readingRule;
	}

	public String addReadingRule() throws Exception {
		readingRule.setRuleType(WorkflowRuleContext.RuleType.READING_RULE);
		return commonAddReadingRule();
	}
	
	public String addPMReadingRule() throws Exception {
		readingRule.setRuleType(WorkflowRuleContext.RuleType.PM_READING_RULE);
		return commonAddReadingRule();
	}
	
	private String commonAddReadingRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		//readingRule.setResourceId(getAssetId());
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, readingRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, actions);
		Chain addRule = FacilioChainFactory.getAddReadingRuleChain();
		addRule.execute(facilioContext);
		
		return SUCCESS;
	}
	
	public String addAssignmentRule() throws Exception
	{
		FacilioContext facilioContext = new FacilioContext();
		workflowRuleContext.setRuleType(RuleType.ASSIGNMENT_RULE);
		//ActionContext assignmentAction = actions.get(0);
		ActionContext assignmentAction = new ActionContext();
		assignmentAction.setActionType(ActionType.ASSIGNMENT_ACTION);
		assignmentTemplate.setName(workflowRuleContext.getName()+"_AssignmentTemplate");
		assignmentAction.setTemplate(assignmentTemplate);
		List<ActionContext> assignActions = new ArrayList<>();
		assignActions.add(assignmentAction);
		//actions.add(assignmentAction);
		//workflowRuleContext.setActions(actions);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, assignActions);
		Chain addRule = FacilioChainFactory.addAssignmentRuleChain();
		addRule.execute(facilioContext);
		return SUCCESS;
	}
	
	public Criteria getSLACriteria() throws Exception
	{
		Criteria obj = new Criteria();
		//obj.setPattern("1");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField assignmentgroup = modBean.getField("assignmentGroup", FacilioConstants.ContextNames.WORK_ORDER);	
		Condition condition1 = new Condition();
		condition1.setField(assignmentgroup);		
		condition1.setValue("1,2");
		condition1.setOperatorId(36);
		obj.addAndCondition(condition1);
		
		FacilioField space = modBean.getField("resource", FacilioConstants.ContextNames.WORK_ORDER);		
		Condition condition2 = new Condition();
		condition2.setField(space);		
		condition2.setValue("1,2");
		condition2.setOperatorId(36);
		obj.addAndCondition(condition2);
		
		FacilioField category = modBean.getField("category", FacilioConstants.ContextNames.WORK_ORDER);		
		Condition condition3 = new Condition();
		condition3.setField(space);		
		condition3.setValue("1");
		condition3.setOperatorId(36);
		obj.addAndCondition(condition3);
		
		return obj;
	}
	

	public String addSLARule() throws Exception
	{
		System.out.println(">>>>>>>> workflowRuleContext : "+workflowRuleContext);
		System.out.println(">>>>>>>slaTemplate : "+slaTemplate);
		FacilioContext facilioContext = new FacilioContext();
		workflowRuleContext.setRuleType(RuleType.SLA_RULE);
		//workflowRuleContext.setCriteria(getSLACriteria());
		ActionContext slaAction = new ActionContext();
		slaAction.setActionType(ActionType.SLA_ACTION);
		//slaTemplate.setSlaPolicyJson("{1:200000,2:600000}"); //Hardcoding Duration here. This will be configured from the UI
		slaTemplate.setName(workflowRuleContext.getName()+"_SLATemplate");
		slaAction.setTemplate(slaTemplate);
		List<ActionContext> slaActions = new ArrayList<>();
		slaActions.add(slaAction);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, slaActions);
		Chain addRule = FacilioChainFactory.addSLARuleChain();
		addRule.execute(facilioContext);
		return SUCCESS;
	}
	
	SLATemplate slaTemplate;
	
	public SLATemplate getSlaTemplate() {
		return slaTemplate;
	}

	public void setSlaTemplate(SLATemplate slaTemplate) {
		this.slaTemplate = slaTemplate;
	}
	AssignmentTemplate assignmentTemplate;
	
	
	public AssignmentTemplate getAssignmentTemplate() {
		return assignmentTemplate;
	}

	public void setAssignmentTemplate(AssignmentTemplate assignmentTemplate) {
		this.assignmentTemplate = assignmentTemplate;
	}

	public String deleteWorkflowRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		
		Chain deleteRule = FacilioChainFactory.getDeleteWorkflowRuleChain();
		deleteRule.execute(context);
		
		result = context.get(FacilioConstants.ContextNames.RESULT).toString();
		
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}

	public String getReadingRulesMap() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_FIELDS, id);
		
		Chain getReadingRules = FacilioChainFactory.getReadingRulesOfFieldsChain();
		getReadingRules.execute(context);
		
		readingRules = (Map<Long, ReadingRuleContext>) context.get(FacilioConstants.ContextNames.READING_RULE_LIST);
		
		return SUCCESS;
	}
	
	private Map<Long, ReadingRuleContext> readingRules;
	public Map<Long, ReadingRuleContext> getReadingRules() {
		return readingRules;
	}
	public void setReadingRules(Map<Long, ReadingRuleContext> readingRules) {
		this.readingRules = readingRules;
	}
	
	public String getWorkflowRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE, this.module);
		Chain workflowRulesChain = FacilioChainFactory.getWorkflowRulesChain();
		workflowRulesChain.execute(context);
		
		setRules((List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST));
		
		return SUCCESS;
	}
	public String addWorkflowRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, actions);
		
		Chain addRule = FacilioChainFactory.getAddWorkflowRuleChain();
		addRule.execute(context);
		
		return SUCCESS;
	}
	
	public String businessRules() throws Exception {
		
	    return SUCCESS;
	}
	
	private ActionForm form = new ActionForm();
	public ActionForm getForm() {
		return form;
	}
	
	public void setForm(ActionForm form) {
		this.form = form;
	}
	
	WorkflowRuleContext rule;
	public WorkflowRuleContext getRule() {
		return rule;
	}
	
	public void setRule(WorkflowRuleContext rule) {
		this.rule = rule;
	}
	
	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return this.actions;
	}
	
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	
	private List<WorkflowRuleContext> rules = null;
	public List<WorkflowRuleContext> getRules() {
		return rules;
	}
	public void setRules(List<WorkflowRuleContext> rules) {
		this.rules = rules;
	}
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String module;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
}
