package com.facilio.controlaction.action;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

public class ControlActionAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	long resourceId = -1;
	long fieldId = -1;
	String value;
	long controlGroupId = -1l;
	ControlGroupContext controlGroup;
	long assetCategoryId;
	
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}
	public long getControlGroupId() {
		return controlGroupId;
	}
	public void setControlGroupId(long controlGroupId) {
		this.controlGroupId = controlGroupId;
	}
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
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getReadingDataMetaModule().getName());
		
		Chain rdmChain = ReadOnlyChainFactory.getRDMChain();
		
		rdmChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROL_ACTION_CONTROLLABLE_POINTS, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		setResult(FacilioConstants.ContextNames.READING_DATA_META_COUNT, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_COUNT));
		return SUCCESS;
	}
	
	public String getControlGroups() throws Exception {
		
		FacilioContext constructListContext = constructListContext();
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getControlGroupModule().getName());
		
		Chain rdmChain = ReadOnlyChainFactory.fetchControlGroupsChain();
		
		rdmChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, constructListContext.get(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS));
		setResult(ControlActionUtil.CONTROL_ACTION_GROUP_COUNT, constructListContext.get(ControlActionUtil.CONTROL_ACTION_GROUP_COUNT));
		return SUCCESS;
	}
	
	public String getControlGroupMeta() throws Exception {
		
		if(controlGroupId > 0) {
			List<ControlGroupContext> controls = ControlActionUtil.getControlActionGroups(Collections.singletonList(controlGroupId));
			
			setResult(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT, controls.get(0));
		}
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
		constructListContext.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategoryId);
		
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
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Chain commandChain = ReadOnlyChainFactory.getControlActionCommandsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS, constructListContext.get(ControlActionUtil.CONTROL_ACTION_COMMANDS));
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT, constructListContext.get(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT));
		
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
	
	public String addControlGroup() throws Exception {
		
		if(controlGroup != null) {
			
			FacilioChain addControlGroupChain = TransactionChainFactory.addControlGroupChain();
			
			FacilioContext context= addControlGroupChain.getContext();
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, Collections.singletonList(controlGroup));
			
			addControlGroupChain.execute();
			
			setResult(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, controlGroup);
		}
		
		return SUCCESS;
	}
	
	public String updateControlGroup() throws Exception {
		
		if(controlGroup != null) {
			
			FacilioChain addControlGroupChain = TransactionChainFactory.updateControlGroupChain();
			
			FacilioContext context= addControlGroupChain.getContext();
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, Collections.singletonList(controlGroup));
			
			addControlGroupChain.execute();
			
			setResult(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, controlGroup);
		}
		
		return SUCCESS;
	}
	
	public String deleteControlGroup() throws Exception {
		
		if(controlGroup != null) {
			
			FacilioChain addReadingAlarmRuleChain = TransactionChainFactory.deleteControlGroupChain();
			
			FacilioContext context= addReadingAlarmRuleChain.getContext();
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, Collections.singletonList(controlGroup));
			
			addReadingAlarmRuleChain.execute();
			
			setResult(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, controlGroup);
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
	
	String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private JSONObject meta;
	public JSONObject getMeta() {
		return meta;
	}
	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}
	
	public String metaData() throws Exception {
		
		JSONObject meta = new JSONObject();
		if(moduleName.equals(ModuleFactory.getReadingDataMetaModule().getName())) {
			
			meta.put("module", ModuleFactory.getReadingDataMetaModule());
			meta.put("fields", FieldFactory.getReadingDataMetaFields());
			
			JSONObject operators = new JSONObject();
			for (FieldType ftype : FieldType.values()) {
				operators.put(ftype.name(), ftype.getOperators());
			}
			
			meta.put("operators", operators);
			
			JSONObject reportOperators = new JSONObject();
			reportOperators.put("DateAggregateOperator", AggregateOperator.DateAggregateOperator.values());
			reportOperators.put("NumberAggregateOperator", AggregateOperator.NumberAggregateOperator.values());
			reportOperators.put("StringAggregateOperator", AggregateOperator.StringAggregateOperator.values());
			reportOperators.put("SpaceAggregateOperator", AggregateOperator.SpaceAggregateOperator.values());
			reportOperators.put("EnergyPurposeAggregateOperator", AggregateOperator.EnergyPurposeAggregateOperator.values());
			meta.put("reportOperators", reportOperators);
			
			setMeta(meta);
			
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
		
		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}
		
		FacilioContext constructListContext = constructListContext();
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME,ModuleFactory.getWorkflowRuleModule().getName());
		
		Chain commandChain = ReadOnlyChainFactory.getControlActionRulesChain();
		
		commandChain.execute(constructListContext);
		
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULES));
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES_COUNT, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULES_COUNT));
		
		return SUCCESS;
		
	}
	
	public String getControlActionRule() throws Exception {
		
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId, true, true);
		
		if(rule != null) {
			List<ActionContext> actionList = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
			rule.setActions(actionList);
		}
		
		if(rule.getRuleTypeEnum() == RuleType.CONTROL_ACTION_SCHEDULED_RULE || rule.getRuleTypeEnum() == RuleType.RECORD_SPECIFIC_RULE) {
			
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
		
//		if(resourceId <= 0 || fieldId <= 0 || value == null) {
//			throw new IllegalArgumentException("One or more value is missing");
//		}
		
		if(controlGroupId > 0) {
			
			FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForControlGroupChain();
			
			FacilioContext context = executeControlActionCommandChain.getContext();
			
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_ID, controlGroupId);
			context.put(ControlActionUtil.VALUE, value);
			context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.CARD);
			
			executeControlActionCommandChain.execute();
		}
		else {
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
		}
		
		return SUCCESS;
	}
	
	
}
