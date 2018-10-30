package com.facilio.bmsconsole.workflow.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

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
	private long parentRuleId = -1;
	public long getParentRuleId() {
		return parentRuleId;
	}
	public void setParentRuleId(long parentRuleId) {
		this.parentRuleId = parentRuleId;
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
	
	public boolean evaluateMisc (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		if (record instanceof ModuleBaseWithCustomFields && siteId != -1) {
			return ((ModuleBaseWithCustomFields) record).getSiteId() == siteId;
		}
		return true;
	}
	
	public boolean evaluateCriteria (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		if(criteria != null) {
			criteriaFlag = criteria.computePredicate(placeHolders).evaluate(record);
		}
		return criteriaFlag;
	}
	
	public boolean evaluateWorkflowExpression (String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		try {
			boolean workflowFlag = true;
			if (workflow != null) {
				workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(workflow.getWorkflowString(), placeHolders);
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
		if (this.getId() == 3335) {
			LOGGER.info("List of actions : "+actions);
		}
		if(actions != null) {
			for(ActionContext action : actions) {
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
		WORKORDER_REQUESTER_NOTIFICATION_RULE,
		
		ALARM_NOTIFICATION_RULE,
		SLA_RULE (true),
		ASSIGNMENT_RULE (true),
		
		PM_READING_RULE,
		CUSTOM_ALARM_NOTIFICATION_RULE,
		VALIDATION_RULE,
		
		ASSET_ACTION_RULE,
		CUSTOM_WORKORDER_NOTIFICATION_RULE,
		SCHEDULED_RULE,
		
		APPROVAL_RULE(true),
		CHILD_APPROVAL_RULE(true),
		REQUEST_APPROVAL_RULE(true),
		REQUEST_REJECT_RULE(true)
		;
		
		private boolean stopFurtherExecution = false;
		private RuleType() {
			// TODO Auto-generated constructor stub
		}
		
		private RuleType(boolean stopFurtherExecution) {
			// TODO Auto-generated constructor stub
			this.stopFurtherExecution = stopFurtherExecution;
		}
		
		public int getIntVal() {
			return ordinal()+1;
		}
		
		public boolean stopFurtherRuleExecution() {
			return stopFurtherExecution;
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Workflow Rule ["+id+", "+name+"]";
		
	}
}
