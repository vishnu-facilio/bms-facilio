package com.facilio.bmsconsole.workflow.rule;

import java.io.Serializable;
import java.sql.SQLTimeoutException;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkflowRuleContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(WorkflowRuleContext.class.getName());
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	private long createdTime = -1l;
	private long modifiedTime = -1l;
	
	public long getModifiedTime() {
		return modifiedTime;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	private boolean terminateExecution = false;
	
	public boolean shouldTerminateChildExecution() {
		return terminateExecution;
	}
	public void setTerminateChildExecution(boolean terminateExecution) {
		this.terminateExecution = terminateExecution;
	}
	
	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}
	
	public String getScheduleJson() throws Exception {
		if(schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}
	public void setScheduleJson(String jsonString) throws Exception {
		if(jsonString != null) {
			JSONParser parser = new JSONParser();
			this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
		}
	}
	
	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	private FacilioField dateField;
	public FacilioField getDateField() {
		return dateField;
	}
	public void setDateField(FacilioField dateField) {
		this.dateField = dateField;
	}
	
	private ScheduledRuleType scheduleType;
	public ScheduledRuleType getScheduleTypeEnum() {
		return scheduleType;
	}
	public void setScheduleType(ScheduledRuleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	public int getScheduleType() {
		if (scheduleType != null) {
			return scheduleType.getValue();
		}
		return -1;
	}
	public void setScheduleType(int scheduleType) {
		this.scheduleType = ScheduledRuleType.valueOf (scheduleType);
	}

	private long lastScheduleRuleExecutedTime = -1;
	public long getLastScheduleRuleExecutedTime() {
		return lastScheduleRuleExecutedTime;
	}
	public void setLastScheduleRuleExecutedTime(long lastScheduleRuleExecutedTime) {
		this.lastScheduleRuleExecutedTime = lastScheduleRuleExecutedTime;
	}
	
	private long interval = -1; //In Seconds
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	private LocalTime time;
	public LocalTime getTimeObj() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getTime() {
		return time == null? null : time.toString();
	}
	public void setTime(String time) {
		if(time != null) {
			this.time = LocalTime.parse(time);
		}
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private WorkflowEventContext event;
	public WorkflowEventContext getEvent() throws Exception {
		if (event == null) {
			event = new WorkflowEventContext();
			event.setModuleId(getModuleId());
			event.setModuleName(getModuleName());
			event.setActivityType(getActivityType());
		}
		return event;
	}
	public void setEvent(WorkflowEventContext event) {
		this.event = event;
		if (event != null) {
			setModuleName(event.getModuleName());
			setModuleId(event.getModuleId());
			setActivityType(event.getActivityType());
		}
	}

	private long moduleId = -1;
	public long getModuleId() throws Exception {
		if(moduleId == -1 && getModule() != null) {
			moduleId = getModule().getModuleId();
		}
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	private String moduleName;
	public String getModuleName() throws Exception {
		if(StringUtils.isEmpty(moduleName) && getModule() != null) {
			moduleName = getModule().getName();
		}
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private FacilioModule module;
	public FacilioModule getModule() throws Exception {
		if(module == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(moduleId > 0) {
				return modBean.getModule(moduleId);
			}
			else if(moduleName != null) {
				return modBean.getModule(moduleName);
			}
			return null;
		}
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}

	private long activity = -1;
	public long getActivityType() {
		return activity;
	}
	public void setActivityType(long activityType) {
		this.activity = activityType;
		this.activityType = EventType.valueOf(activityType);
	}

	private List<BaseTriggerContext> triggers;
	public List<BaseTriggerContext> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<BaseTriggerContext> triggers) {
		this.triggers = triggers;
	}
	public void addTrigger(BaseTriggerContext trigger) {
		if (triggers == null) {
			this.triggers = new ArrayList<>();
		}
		this.triggers.add(trigger);
	}

	private EventType activityType;
	public void setActivityType(EventType activityType) {
		this.activity = activityType.getValue();
		this.activityType = activityType;
	}
	public EventType getActivityTypeEnum() {
		return activityType;
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	private int executionOrder = -1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public boolean isActive() {
		if(status != null) {
			return status.booleanValue();
		}
		return false;
	}
	
	private List<ActionContext> actions;
	
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	public void addAction(ActionContext action) {
		this.actions = this.actions == null ? new ArrayList<ActionContext>() : this.actions;
		this.actions.add(action);
	}
	private String parentRuleName = null;
	
	public String getParentRuleName() {
		return parentRuleName;
	}
	public void setParentRuleName(String parentRuleName) {
		this.parentRuleName = parentRuleName;
	}

	private long parentRuleId = -1;
	public long getParentRuleId() {
		return parentRuleId;
	}
	public void setParentRuleId(long parentRuleId) {
		this.parentRuleId = parentRuleId;
	}
	
	private WorkflowRuleContext parentRule;

	public WorkflowRuleContext getParentRule() {
		return parentRule;
	}
	public void setParentRule(WorkflowRuleContext parentRule) {
		this.parentRule = parentRule;
	}

	private Boolean onSuccess;
	public Boolean getOnSuccess() {
		return onSuccess;
	}
	public boolean isOnSuccess() {
		if (onSuccess != null) {
			return onSuccess.booleanValue();
		}
		return false;
	}
	public void setOnSuccess(boolean onSuccess) {
		this.onSuccess = onSuccess;
	}

	private RuleType ruleType;
	public int getRuleType() {
		if(ruleType != null) {
			return ruleType.getIntVal();
		}
		return -1;
	}
	public void setRuleType(int ruleType) {
		if (ruleType > 0) {
			this.ruleType = RULE_TYPES[ruleType - 1];
		}
		else {
			this.ruleType = null;
		}
	}
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	public RuleType getRuleTypeEnum() {
		return ruleType;
	}
	
	private long versionGroupId = -1;
	public long getVersionGroupId() {
		return versionGroupId;
	}
	public void setVersionGroupId(long versionGroupId) {
		this.versionGroupId = versionGroupId;
	}
	
	int versionNumber = -1;
	
	public int getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	private Boolean latestVersion;
	public Boolean getLatestVersion() {
		return latestVersion;
	}
	public void setLatestVersion(Boolean latestVersion) {
		this.latestVersion = latestVersion;
	}
	public boolean isLatestVersion() {
		if (latestVersion != null) {
			return latestVersion.booleanValue();
		}
		return false;
	}
	
	private long fetchDateVal(Object record) throws Exception {
		Long timeVal = (Long) ((ModuleBaseWithCustomFields) record).getDatum(dateField.getName());
		if (timeVal == null) {
			timeVal = (Long) FieldUtil.getProperty(record, dateField.getName());
		}
		return timeVal == null ? -1 : timeVal;
	}
	
	public boolean evaluateMisc (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		return true;
	}
	
	public boolean evaluateSite (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		if(siteId == -1) {
			return true;
		}
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SITE_FILTER_WORKFLOW_RULE)){
			if (record instanceof ModuleBaseWithCustomFields) {
				return ((ModuleBaseWithCustomFields) record).getSiteId() == siteId;
			}
		} else if (record instanceof ModuleBaseWithCustomFields && moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			return ((ModuleBaseWithCustomFields) record).getSiteId() == siteId;
		}
		return true;
	}
	
	
	public boolean evaluateCriteria (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		if(criteria != null && record != null) {
			criteriaFlag = criteria.computePredicate(placeHolders).evaluate(record);
		}
		return criteriaFlag;
	}
	
	public boolean evaluateWorkflowExpression (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		try {
			boolean workflowFlag = true;
			long recordId = -1;
			if(record !=null && record instanceof ModuleBaseWithCustomFields) {
				ModuleBaseWithCustomFields recordObject = (ModuleBaseWithCustomFields) record;
				recordId = recordObject.getId();
			}
			if (workflow != null) {
					workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(workflow, placeHolders,workflow.getId(),recordId,WorkflowLogType.WORKFLOW_RULE_EVALUATION);
			}
			return workflowFlag;
		}
		catch (ArithmeticException e) {
			LOGGER.error("Arithmetic exception during execution of workflow for rule : "+id, e);
			return false;
		}
	}
	
	public Map<String, Object> constructPlaceHolders(String moduleName, Object record, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		Map<String, Object> rulePlaceHolders = new HashMap<>(recordPlaceHolders);
		CommonCommandUtil.appendModuleNameInKey(null, "rule", FieldUtil.getAsProperties(this), rulePlaceHolders);
		return rulePlaceHolders;
	}

	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = getId();
		long startTime = System.currentTimeMillis();
		if (actions == null) {
			actions = ActionAPI.getActiveActionsFromWorkflowRule(ruleId);
		}
		LOGGER.debug("Time taken to fetch actions for workflowrule id : "+ruleId+" with actions : "+actions+" is "+(System.currentTimeMillis() - startTime));
		if(actions != null) {
			for(ActionContext action : actions) {
				if (this.getId() == 6448) {
					LOGGER.info("Place holders during action : "+placeHolders);
				}
				action.executeAction(placeHolders, context, this, record);
			}
		}
	}
	
	public void executeFalseActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		//TODO Add true or false boolean in actions
	}
	
	private List<FieldChangeFieldContext> fields;
	public List<FieldChangeFieldContext> getFields() {
		return fields;
	}
	public void setFields(List<FieldChangeFieldContext> fields) {
		this.fields = fields;
	}
	
	private static final RuleType[] RULE_TYPES = RuleType.values();
	public static enum RuleType {
		READING_RULE(false, false, false, true, false), // reading
		WORKORDER_AGENT_NOTIFICATION_RULE,
		WORKORDER_REQUESTER_NOTIFICATION_RULE, //3
		
		ALARM_NOTIFICATION_RULE,
		SLA_RULE (true),	// Not in use
		ASSIGNMENT_RULE (true), //6
		
		PM_READING_RULE,
		IMPACT_RULE(false),
		VALIDATION_RULE, //9
		
		ASSET_ACTION_RULE,
		SLA_WORKFLOW_RULE(true),
		SLA_POLICY_RULE(true, false, false, true, false), //12
		
		APPROVAL_RULE(true, true),
		REQUEST_APPROVAL_RULE(true),
		REQUEST_REJECT_RULE(true), //15
		
		CHILD_APPROVAL_RULE(true),
		PM_ALARM_RULE,
		ALARM_TRIGGER_RULE(false,true,true, true, false), //18
		
		ALARM_CLEAR_RULE(false,false,true),
		WORKORDER_CUSTOM_CHANGE,
		APPROVAL_STATE_FLOW(true),    //21

		APPROVAL_STATE_TRANSITION,
		PM_NOTIFICATION_RULE,
		READING_ALARM_RULE,			//24
		
		ALARM_RCA_RULES(false,true,true),
		STATE_TRANSACTION_FIELD_SCHEDULED,
		PM_READING_TRIGGER,			// 27
		
		STATE_RULE(true),
		STATE_FLOW(true),

		BUSSINESS_LOGIC_ASSET_RULE, //30
		REPORT_DOWNTIME_RULE, //31
		CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE, //32
		CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE, //33

		RECORD_SPECIFIC_RULE, //34
		CONTROL_ACTION_READING_ALARM_RULE,
		CONTROL_ACTION_SCHEDULED_RULE,
		MODULE_RULE(false,false,false, false, true), // 37
		MODULE_RULE_NOTIFICATION(false,false,false, false, true),
		
		READING_VIOLATION_RULE, // 39
		CUSTOM_BUTTON,	// 40
		ALARM_WORKFLOW_RULE,
		PM_CUSTOM_TRIGGER_RULE,
		TRANSACTION_RULE, //43

		SCORING_RULE,// 44
		SYSTEM_BUTTON,//45
		SATISFACTION_SURVEY_RULE, //46
		SURVEY_ACTION_RULE //47
		;
		//Always add at the end
		
		
		private boolean stopFurtherExecution = false, versionSupported = false,isChildType = false;
		private boolean childSupport = false, isPostExecute = false;
		private RuleType() {
		}
		
		private RuleType(boolean stopFurtherExecution) {
			this(stopFurtherExecution, false);
		}
		
		private RuleType(boolean stopFurtherExecution, boolean versionSupported) {
			this(stopFurtherExecution, versionSupported, false);
		}
		
		private RuleType(boolean stopFurtherExecution, boolean versionSupported,boolean isChildType) {
			this(stopFurtherExecution, versionSupported, isChildType, false, false);
		}

		RuleType(boolean stopFurtherExecution, boolean versionSupported,boolean isChildType, boolean childSupport, boolean isPostExecute) {
			this.stopFurtherExecution = stopFurtherExecution;
			this.versionSupported = versionSupported;
			this.isChildType = isChildType;
			this.childSupport = childSupport;
			this.isPostExecute = isPostExecute;
		}
		
		public boolean isChildType() {
			return isChildType;
		}

		public void setChildType(boolean isChildType) {
			this.isChildType = isChildType;
		}

		public int getIntVal() {
			return ordinal()+1;
		}

		public boolean isChildSupport() {
			return childSupport;
		}
		public boolean stopFurtherRuleExecution() {
			return stopFurtherExecution;
		}
		public boolean versionSupported() {
			return versionSupported;
		}
		
		public boolean isPostExecute() {
			return isPostExecute;
		}

		public static RuleType valueOf(int val) {
			try {
				return RULE_TYPES[val - 1];
			}
			catch(ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}
	
	public boolean reportBreakdown;

	public boolean isReportBreakdown() {
		return reportBreakdown;
	}

	public void setReportBreakdown(boolean reportBreakdown) {
		this.reportBreakdown = reportBreakdown;
	}

	public static enum ScheduledRuleType {
		BEFORE,
		ON,
		AFTER
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ScheduledRuleType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Workflow Rule ["+id+", "+name+"]";
		
	}
	
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	

	public boolean executeRuleAndChildren (WorkflowRuleContext workflowRule, FacilioModule module, Object record, List<UpdateChangeSet> changeSet, Map<String, Object> recordPlaceHolders, FacilioContext context,boolean propagateError, FacilioField parentRuleField, FacilioField onSuccessField, Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap, boolean isParallelRuleExecution, List<EventType> eventTypes, RuleType... ruleTypes) throws Exception {
		try {		
			long workflowStartTime = System.currentTimeMillis();
			workflowRule.setTerminateChildExecution(false);
			boolean result = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, module.getName(), record, changeSet, recordPlaceHolders, context);
			LOGGER.debug(MessageFormat.format("Time take to execute workflow {0} and actions: is {1} ",this.getId(), (System.currentTimeMillis() - workflowStartTime)));
			LOGGER.debug(MessageFormat.format("Result of rule : {0} for record : {1} is {2}",workflowRule.getId(),record,result));

//			if((AccountUtil.getCurrentOrg().getId() == 231l && (workflowRule.getId() == 36242l || workflowRule.getId() == 36243l)) || (AccountUtil.getCurrentOrg().getId() == 78l)) {
//				LOGGER.info("Time take to execute workflow and actions: " + (System.currentTimeMillis() - workflowStartTime));
//				LOGGER.info("Select Query Count till execution of workflow and actions" + AccountUtil.getCurrentAccount().getSelectQueries() + " Timetaken "+AccountUtil.getCurrentAccount().getSelectQueriesTime());
//			}
			
			boolean stopFurtherExecution = false;	
			if (result) {
				if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
					stopFurtherExecution = true;
				}
			}
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 339l) {
//				LOGGER.info("Time taken to execute rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" is "+(System.currentTimeMillis() - workflowStartTime));			
			}
			long startTimeToFetchChildRules = System.currentTimeMillis();
			LOGGER.debug("Time taken to execute rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" is "+(System.currentTimeMillis() - workflowStartTime));
			if(workflowRule.getRuleTypeEnum().isChildSupport() && !workflowRule.shouldTerminateChildExecution()) {
				String workflowRuleKey = WorkflowRuleAPI.constructParentWorkflowRuleKey(workflowRule.getId(),result);
				List<WorkflowRuleContext> currentWorkflows = workflowRuleCacheMap.get(workflowRuleKey);	
				if(currentWorkflows == null) {
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(parentRuleField, String.valueOf(workflowRule.getId()), NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition(onSuccessField, String.valueOf(result), BooleanOperators.IS));
					currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(workflowRule.getModule(), eventTypes, criteria, ruleTypes);
					if(currentWorkflows == null) {
						workflowRuleCacheMap.put(workflowRuleKey, Collections.EMPTY_LIST);
					}
					else {
						workflowRuleCacheMap.put(workflowRuleKey, currentWorkflows);
					}
				}	
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 339l) {
//					LOGGER.info("Time taken to fetch child rule alone for rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" is "+(System.currentTimeMillis() - startTimeToFetchChildRules));			
				}
				WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(currentWorkflows, module, record, changeSet, recordPlaceHolders, context, propagateError, workflowRuleCacheMap, isParallelRuleExecution, eventTypes, ruleTypes);
			}

			if ((AccountUtil.getCurrentOrg().getId() == 339l) || (AccountUtil.getCurrentOrg().getId() == 78l)) {
//				LOGGER.info("Time taken including childrule execution -- for rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" is "+(System.currentTimeMillis() - workflowStartTime));	
//				LOGGER.info("Select Query Count including childrule execution  -- " + AccountUtil.getCurrentAccount().getSelectQueries() + " Timetaken till childrule execution "+AccountUtil.getCurrentAccount().getSelectQueriesTime());
			}
			LOGGER.debug("Time taken to execute rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" including child rule execution is "+(System.currentTimeMillis() - workflowStartTime));
			return stopFurtherExecution;
		}
		catch (Exception e) {
			StringBuilder builder = new StringBuilder("Error during execution of rule : ");
			builder.append(workflowRule.getId());
			if (record instanceof ModuleBaseWithCustomFields) {
				builder.append(" for Record : ")
						.append(((ModuleBaseWithCustomFields)record).getId())
						.append(" of module : ")
						.append(module.getName());
			}
			LOGGER.error(builder.toString(), e);
			
			if (propagateError || (e instanceof SQLTimeoutException && e.getMessage().contains("Transaction timed out and so cannot get new connection"))) {
				throw e;
			}
		}
		return false;
	}

	@JsonIgnore
	@JSON(serialize = false)
	public String getSchedulerJobName() {
		return "ScheduledRuleExecution";
	}
	
	@JsonIgnore
	@JSON(serialize = false)
	public String getScheduleRuleJobName() {
		return "ScheduleRuleCreateJob";
	}
}
