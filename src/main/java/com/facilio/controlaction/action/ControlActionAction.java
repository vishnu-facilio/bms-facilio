package com.facilio.controlaction.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.actions.WorkflowRuleAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
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
	
	ReadingAlarmRuleContext readingAlarmRuleContext;
	public ReadingAlarmRuleContext getReadingAlarmRuleContext() {
		return readingAlarmRuleContext;
	}
	public void setReadingAlarmRuleContext(ReadingAlarmRuleContext readingAlarmRuleContext) {
		this.readingAlarmRuleContext = readingAlarmRuleContext;
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
		
		setResult(ControlActionUtil.CONTROL_ACTION_CONTROLLABLE_POINTS, ReadingsAPI.getControllableRDMs());
		return SUCCESS;
	}
	
	public String getControlActionCommands() throws Exception {
		
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS, ControlActionUtil.getCommands());
		return SUCCESS;
	}
	
	public String addReadingAlarmRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.READING_ALARM_RULES, Collections.singletonList(readingAlarmRuleContext));
		context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.CONTROL_ACTION_READING_ALARM_RULE);
		
		Chain addReadingAlarmRuleChain = TransactionChainFactory.addReadingAlarmRuleChain();
		addReadingAlarmRuleChain.execute(context);
		
		setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		
		return SUCCESS;
	}
	
	public String addScheduledRule() throws Exception {
		
		return SUCCESS;
	}
	
	public String getControlActionRules() throws Exception {
		
		List<WorkflowRuleContext> rules = new ArrayList<WorkflowRuleContext>();
		
		List<WorkflowRuleContext> alarmRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.CONTROL_ACTION_READING_ALARM_RULE, true, true);
		if(alarmRules != null) {
			rules.addAll(alarmRules);
		}
		
		List<WorkflowRuleContext> scheduledRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.CONTROL_ACTION_SCHEDULED_RULE, true, true);
		if(scheduledRules != null) {
			rules.addAll(scheduledRules);
		}
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES, rules);
		
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
