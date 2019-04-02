package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public boolean isTerminateExecution() {
		return terminateExecution;
	}
	public void setTerminateExecution(boolean terminateExecution) {
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
	
	private long eventId = -1;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	private WorkflowEventContext event;
	public WorkflowEventContext getEvent() {
		return event;
	}
	public void setEvent(WorkflowEventContext event) {
		this.event = event;
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
		this.ruleType = RULE_TYPES[ruleType - 1];
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
			timeVal = (Long) PropertyUtils.getProperty(record, dateField.getName());
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
		else if (record instanceof ModuleBaseWithCustomFields && moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
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
			if (workflow != null) {
				workflow.setLogNeeded(true);
				workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(workflow, placeHolders);
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
		if (actions == null) {
			actions = ActionAPI.getActiveActionsFromWorkflowRule(ruleId);
		}
		if (AccountUtil.getCurrentOrg().getId() == 151) {
			LOGGER.info("List of actions for "+this.getId()+" : "+actions);
		}
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
		READING_RULE,
		WORKORDER_AGENT_NOTIFICATION_RULE,
		WORKORDER_REQUESTER_NOTIFICATION_RULE, //3
		
		ALARM_NOTIFICATION_RULE,
		SLA_RULE (true),
		ASSIGNMENT_RULE (true), //6
		
		PM_READING_RULE,
		CUSTOM_ALARM_NOTIFICATION_RULE,
		VALIDATION_RULE, //9
		
		ASSET_ACTION_RULE,
		CUSTOM_WORKORDER_NOTIFICATION_RULE,
		SCHEDULED_RULE, //12 //Do not use
		
		APPROVAL_RULE(true, true),
		REQUEST_APPROVAL_RULE(true),
		REQUEST_REJECT_RULE(true), //15
		
		CHILD_APPROVAL_RULE(true),
		PM_ALARM_RULE,
		ALARM_TRIGGER_RULE(false,true,true), //18
		
		ALARM_CLEAR_RULE(false,false,true),
		WORKORDER_CUSTOM_CHANGE,
		BUSSINESS_LOGIC_ALARM_RULE,    //21

		BUSSINESS_LOGIC_WORKORDER_RULE,
		PM_NOTIFICATION_RULE,
		READING_ALARM_RULE,			//24
		
		ALARM_RCA_RULES(false,true,true),
		ASSET_NOTIFICATION_RULE,
		PM_READING_TRIGGER			// 27
		;
		//Always add at the end
		
		
		private boolean stopFurtherExecution = false, versionSupported = false,isChildType = false;
		private RuleType() {
			// TODO Auto-generated constructor stub
		}
		
		private RuleType(boolean stopFurtherExecution) {
			// TODO Auto-generated constructor stub
			this.stopFurtherExecution = stopFurtherExecution;
		}
		
		private RuleType(boolean stopFurtherExecution, boolean versionSupported) {
			// TODO Auto-generated constructor stub
			this.stopFurtherExecution = stopFurtherExecution;
			this.versionSupported = versionSupported;
		}
		
		private RuleType(boolean stopFurtherExecution, boolean versionSupported,boolean isChildType) {
			this.stopFurtherExecution = stopFurtherExecution;
			this.versionSupported = versionSupported;
			this.isChildType = isChildType;
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
		
		public boolean stopFurtherRuleExecution() {
			return stopFurtherExecution;
		}
		public boolean versionSupported() {
			return versionSupported;
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
}
