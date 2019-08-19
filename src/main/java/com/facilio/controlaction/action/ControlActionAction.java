package com.facilio.controlaction.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class ControlActionAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	long resourceId = -1;
	long fieldId = -1;
	String value;
	
	public ReadingDataMeta getRdm() {
		return rdm;
	}
	public void setRdm(ReadingDataMeta rdm) {
		this.rdm = rdm;
	}

	ReadingDataMeta rdm;
	
	long ruleId;
	
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	ReadingAlarmRuleContext readingAlarmRuleContext;
	public ReadingAlarmRuleContext getReadingAlarmRuleContext() {
		return readingAlarmRuleContext;
	}
	public void setReadingAlarmRuleContext(ReadingAlarmRuleContext readingAlarmRuleContext) {
		this.readingAlarmRuleContext = readingAlarmRuleContext;
	}
	
	WorkflowRuleContext workflowRuleContext;
		
	
	public WorkflowRuleContext getWorkflowRuleContext() {
		return workflowRuleContext;
	}
	public void setWorkflowRuleContext(WorkflowRuleContext workflowRuleContext) {
		this.workflowRuleContext = workflowRuleContext;
	}
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	} 
	
	public String getControllablePoints() throws Exception {
		
		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}
		
		FacilioContext constructListContext = constructListContext();
		
		Chain rdmChain = ReadOnlyChainFactory.getRDMChain();
		
		rdmChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROL_ACTION_CONTROLLABLE_POINTS, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		setResult(FacilioConstants.ContextNames.READING_DATA_META_COUNT, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_COUNT));
		return SUCCESS;
	}
	
	
	public String getControllableAssets() throws Exception {
		
		FacilioContext constructListContext = new FacilioContext();
		
		Chain commandChain = ReadOnlyChainFactory.getControllableAssetsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROLLABLE_RESOURCES, constructListContext.get(ControlActionUtil.CONTROLLABLE_RESOURCES));
		
		return SUCCESS;
	}
	
	public String getControllableFields() throws Exception {
		
		FacilioContext constructListContext = new FacilioContext();
		
		constructListContext.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		
		Chain commandChain = ReadOnlyChainFactory.getControllableFieldsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROLLABLE_FIELDS, constructListContext.get(ControlActionUtil.CONTROLLABLE_FIELDS));
		
		return SUCCESS;
	}
	
	public String getControlActionCommands() throws Exception {
		
		
		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}
		
		FacilioContext constructListContext = constructListContext();
		
		Chain commandChain = ReadOnlyChainFactory.getControlActionCommandsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS, constructListContext.get(ControlActionUtil.CONTROL_ACTION_COMMANDS));
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_COUNT));
		
		return SUCCESS;
	}
	
	public String addControlActionRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		if(readingAlarmRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.READING_ALARM_RULES, Collections.singletonList(readingAlarmRuleContext));
			context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.CONTROL_ACTION_READING_ALARM_RULE);
			
			Chain addReadingAlarmRuleChain = TransactionChainFactory.addReadingAlarmRuleChain();
			addReadingAlarmRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		}
		else if (workflowRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			workflowRuleContext.setRuleType(RuleType.CONTROL_ACTION_SCHEDULED_RULE);
			
			Chain addWorkflowRuleChain = TransactionChainFactory.addWorkflowRuleChain();
			addWorkflowRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		}
		
		return SUCCESS;
	}
	
	public String updateControlActionRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		if(readingAlarmRuleContext != null) {
			context.put(FacilioConstants.ContextNames.READING_ALARM_RULES, Collections.singletonList(readingAlarmRuleContext));
			context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.CONTROL_ACTION_READING_ALARM_RULE);
			
			Chain addReadingAlarmRuleChain = TransactionChainFactory.updateReadingAlarmRuleChain();
			addReadingAlarmRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		}
		else if (workflowRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			workflowRuleContext.setRuleType(RuleType.CONTROL_ACTION_SCHEDULED_RULE);
			
			Chain addWorkflowRuleChain = TransactionChainFactory.updateWorkflowRuleChain();
			addWorkflowRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		}
		
		return SUCCESS;
	}
	
	public String deleteControlActionRule() throws Exception {
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, Collections.singletonList(ruleId));
		
		Chain deleteRule = TransactionChainFactory.getDeleteWorkflowRuleChain();
		deleteRule.execute(context);
		
		setResult("result", context.get(FacilioConstants.ContextNames.RESULT));
		
		return SUCCESS;
	}
	
	public String updateRDM() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, Collections.singletonList(rdm));
		
		Chain addReadingAlarmRuleChain = TransactionChainFactory.updateReadingDataMetaChain();
		addReadingAlarmRuleChain.execute(context);
		
		
		setResult("rdm", rdm);
		
		return SUCCESS;
	}
	
	public String getControlActionRules() throws Exception {
		
		List<WorkflowRuleContext> rules = new ArrayList<WorkflowRuleContext>();
		
		List<WorkflowRuleContext> alarmRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.CONTROL_ACTION_READING_ALARM_RULE, true, true,false);
		if(alarmRules != null) {
			rules.addAll(alarmRules);
		}
		
		List<WorkflowRuleContext> scheduledRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.CONTROL_ACTION_SCHEDULED_RULE, true, true,false);
		if(scheduledRules != null) {
			rules.addAll(scheduledRules);
		}
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES, rules);
		
		return SUCCESS;
	}
	
	public String getControlActionRule() throws Exception {
		
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId, true, true, true);
		
		if(rule.getRuleTypeEnum() == RuleType.CONTROL_ACTION_SCHEDULED_RULE) {
			
			workflowRuleContext = rule;
			setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		}
		else if (rule.getRuleTypeEnum() == RuleType.CONTROL_ACTION_READING_ALARM_RULE) {
			
			readingAlarmRuleContext = (ReadingAlarmRuleContext)rule;
			setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		}
		
		return SUCCESS;
	}
	
	public String setReadingValue() throws Exception {
		
		if(resourceId <= 0 || fieldId <= 0 || value == null) {
			throw new IllegalArgumentException("One or more value is missing");
		}
		
		ResourceContext resourceContext = new ResourceContext();
		resourceContext.setId(resourceId);
		
		ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
		controlActionCommand.setResource(resourceContext);
		controlActionCommand.setFieldId(fieldId);
		controlActionCommand.setValue(value);
		
		FacilioContext context = new FacilioContext();
		
		context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
		context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.CARD);
		
		Chain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
		executeControlActionCommandChain.execute(context);
		
		return SUCCESS;
	}
	
	
}
