package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.templates.AssignmentTemplate;
import com.facilio.bmsconsole.templates.SLATemplate;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.SLARuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class WorkflowRuleAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DashboardAction.class.getName());
	public String execute() throws Exception 
	{
	    return SUCCESS;
	}
	WorkflowRuleContext rule;
	public WorkflowRuleContext getRule() {
		return rule;
	}
	public void setRule(WorkflowRuleContext rule) {
		this.rule = rule;
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
		facilioContext.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.CREATE);
		
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
		commonAddReadingRule();
		setResult("rule", readingRule);
		return SUCCESS;
	}
	
	public String addNewReadingRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();

		facilioContext.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
		Chain addRule = TransactionChainFactory.addAlarmRuleChain();
		addRule.execute(facilioContext);
		
		setResult("rule", alarmRule);
		return SUCCESS;
	}
	
	AlarmRuleContext alarmRule;
	
	public AlarmRuleContext getAlarmRule() {
		return alarmRule;
	}
	public void setAlarmRule(AlarmRuleContext alarmRule) {
		this.alarmRule = alarmRule;
	}
	public String addPMReadingRule() throws Exception {
		readingRule.setRuleType(WorkflowRuleContext.RuleType.PM_READING_RULE);
		return commonAddReadingRule();
	}
	
	private String commonAddReadingRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();

		readingRule.setActions(actions);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, readingRule);
		Chain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(facilioContext);
		
		return SUCCESS;
	}
	
	public String updateReadingRule() throws Exception {
		readingRule.setActions(actions);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, readingRule);
		
		Chain updateRule = TransactionChainFactory.updateWorkflowRuleChain();
		updateRule.execute(context);
		
		readingRule = (ReadingRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		setResult("rule", readingRule);
		
		return SUCCESS;
	}
	
	public String updateNewReadingRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();

		facilioContext.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
		Chain addRule = TransactionChainFactory.updateAlarmRuleChain();
		addRule.execute(facilioContext);
		
		alarmRule = (AlarmRuleContext) facilioContext.get(FacilioConstants.ContextNames.ALARM_RULE);
		setResult("rule", alarmRule);
		return SUCCESS;
	}
	
	public String addAssignmentRule() throws Exception
	{
		FacilioContext facilioContext = new FacilioContext();
		rule.setRuleType(RuleType.ASSIGNMENT_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, constructAssignmentAction(true));
		Chain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", rule);
		return SUCCESS;
	}
	
	public String updateAssignmentRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		rule.setRuleType(RuleType.ASSIGNMENT_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, constructAssignmentAction(false));
		Chain updateRule = TransactionChainFactory.updateWorkflowRuleChain();
		updateRule.execute(facilioContext);
		setResult("rule", rule);
		return SUCCESS;
	}
	
	private List<ActionContext> constructAssignmentAction(boolean isAdd) {
		if (assignmentTemplate != null) {
			ActionContext assignmentAction = new ActionContext();
			assignmentAction.setActionType(ActionType.ASSIGNMENT_ACTION);
			assignmentTemplate.setName(rule.getName()+"_AssignmentTemplate");
			assignmentAction.setTemplate(assignmentTemplate);
			List<ActionContext> assignActions = Collections.singletonList(assignmentAction);
			return assignActions;
		}
		else if (isAdd) {
			throw new IllegalArgumentException("Assignment Template cannot be null during addition of Assignment rule");
		}
		return null;
	}
	
	private SLARuleContext slaRule;
	public SLARuleContext getSlaRule() {
		return slaRule;
	}
	public void setSlaRule(SLARuleContext slaRule) {
		this.slaRule = slaRule;
	}

	public String addSLARule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		slaRule.setRuleType(RuleType.SLA_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, constructSLAAction(true));
		Chain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", slaRule);
		return SUCCESS;
	}
	
	public String updateSLARule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		slaRule.setRuleType(RuleType.SLA_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, constructSLAAction(false));
		Chain updateRule = TransactionChainFactory.updateWorkflowRuleChain();
		updateRule.execute(facilioContext);
		setResult("rule", slaRule);
		return SUCCESS;
	}
	
	private List<ActionContext> constructSLAAction(boolean isAdd) {
		if (slaTemplate != null) {
			ActionContext slaAction = new ActionContext();
			slaAction.setActionType(ActionType.SLA_ACTION);
			slaTemplate.setName(slaRule.getName()+"_SLATemplate");
			slaAction.setTemplate(slaTemplate);
			List<ActionContext> slaActions = Collections.singletonList(slaAction);
			return slaActions;
		}
		else if (isAdd) {
			throw new IllegalArgumentException("SLA Template cannot be null during addition of SLA rule");
		}
		return null;
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
		
		setResult("result", context.get(FacilioConstants.ContextNames.RESULT));
		
		return SUCCESS;
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
	
	public String fetchWorkflowRulesOfType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, ruleType);
		
		Chain workflowRuleType = FacilioChainFactory.getWorkflowRuleOfTypeChain();
		workflowRuleType.execute(context);
		workflowRuleList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		setResult("rules", workflowRuleList);
		return SUCCESS;
	}
	
	public String newFetchWorkflowRulesOfType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, ruleType);
		context.put(FacilioConstants.ContextNames.WORKFLOW_FETCH_EVENT, false);
		context.put(FacilioConstants.ContextNames.WORKFLOW_FETCH_CHILDREN, false);
		
		Chain workflowRuleType = ReadOnlyChainFactory.fetchWorkflowRulesOfTypeChain();
		workflowRuleType.execute(context);
		workflowRuleList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		setResult("rules", workflowRuleList);
		return SUCCESS;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public String fetchWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, ruleId);
		
		Chain fetchWorkflowChain = ReadOnlyChainFactory.fetchWorkflowRuleWithActionsChain();
		fetchWorkflowChain.execute(context);
		rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		setResult("rule", rule);
		
		return SUCCESS;
	}
	
	public String fetchWorkflowRuleNew() throws Exception {
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, ruleId);
		Chain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
		fetchAlarmChain.execute(context);
		alarmRule =  (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		setResult("alarmRule", alarmRule);
		
		setResult(FacilioConstants.ContextNames.ALARM_RULE_ACTIVE_ALARM, context.get(FacilioConstants.ContextNames.ALARM_RULE_ACTIVE_ALARM));
		setResult(FacilioConstants.ContextNames.ALARM_RULE_THIS_WEEK, context.get(FacilioConstants.ContextNames.ALARM_RULE_THIS_WEEK));
		setResult(FacilioConstants.ContextNames.ALARM_RULE_TOP_5_ASSETS, context.get(FacilioConstants.ContextNames.ALARM_RULE_TOP_5_ASSETS));
		setResult(FacilioConstants.ContextNames.ALARM_RULE_WO_SUMMARY, context.get(FacilioConstants.ContextNames.ALARM_RULE_WO_SUMMARY));
		
		return SUCCESS;
	}
	
	private List<WorkflowRuleContext> workflowRuleList;
	public List<WorkflowRuleContext> getWorkflowRuleList() {
		return workflowRuleList;
	}
	public void setWorkflowRuleList(List<WorkflowRuleContext> workflowRuleList) {
		this.workflowRuleList = workflowRuleList;
	}
	
	private RuleType ruleType;
	public int getRuleType() {
		if(ruleType != null) {
			return ruleType.getIntVal();
		}
		return -1;
	}
	public void setRuleType(int ruleType) {
		this.ruleType = RuleType.valueOf(ruleType);
	}
	
	public String addWorkflowRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
		
		Chain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(context);
		setResult("rule", rule);
		return SUCCESS;
	}
	
	public String updateWorkflowRule() throws Exception {
		rule.setActions(actions);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		
		Chain updateRule = TransactionChainFactory.updateWorkflowRuleChain();
		updateRule.execute(context);
		
		rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		setResult("rule", rule);
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
	
	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return this.actions;
	}
	
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
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
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private Boolean status;
	public Boolean getStatus() {
		if (status == null) {
			return false;
		}
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public String changeWorkflowStatus() throws Exception {
		WorkflowRuleContext workFlow = new WorkflowRuleContext();
		workFlow.setStatus(status);
		workFlow.setId(ruleId);
		WorkflowRuleAPI.updateWorkflowRule(workFlow);
		setResult("result", "success");
		return SUCCESS;
	}
	public String v2RulesList() throws Exception {
		FacilioContext context = new FacilioContext();
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getIsCount() != null) {
 			context.put(FacilioConstants.ContextNames.RULE_COUNT, getIsCount());
 		}

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "workflowrule.name");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		
		if (getPage() != 0) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		context.put(FacilioConstants.ContextNames.ID, ruleId);
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, ruleType);
		context.put(FacilioConstants.ContextNames.WORKFLOW_FETCH_EVENT, false);
		context.put(FacilioConstants.ContextNames.WORKFLOW_FETCH_CHILDREN, false);
		
		Chain workflowRuleType = ReadOnlyChainFactory.fetchWorkflowRules();
		workflowRuleType.execute(context);
		workflowRuleList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		if (getIsCount() != null) {
			setCount((long) context.get(FacilioConstants.ContextNames.RULE_COUNT));
		}
		setResult("count", getCount());
		setResult("rules", workflowRuleList);
		return SUCCESS;

	}
	
	public String v2rulesCount () throws Exception {
		v2RulesList();
		long listCount = getCount();
		setResult("count", listCount);
		return SUCCESS;
	}
	
	private String isCount;
	
	public String getIsCount() {
		return isCount;
	}
	public void setIsCount(String isCount) {
		this.isCount = isCount;
	}
	private long count ;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
