package com.facilio.controlaction.action;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
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
import com.facilio.controlaction.context.ControllableAssetCategoryContext.ControllableCategory;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.modules.BmsAggregateOperators;
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
	long spaceId = -1;
	long floorId = -1;
	List<Long> spaceIncludeIds;
	List<Long> spaceExcludeIds;
	
	ControlActionMode controlActionMode;
	ControllableCategory controllableCategory;
	ControllablePoints controllablePoint;
	
	List<Integer> categoryIncludeIds;
	List<Integer> categoryExcludeIds;
	
	public List<Integer> getCategoryIncludeIds() {
		return categoryIncludeIds;
	}

	public void setCategoryIncludeIds(List<Integer> categoryIncludeIds) {
		this.categoryIncludeIds = categoryIncludeIds;
	}

	public List<Integer> getCategoryExcludeIds() {
		return categoryExcludeIds;
	}

	public void setCategoryExcludeIds(List<Integer> categoryExcludeIds) {
		this.categoryExcludeIds = categoryExcludeIds;
	}

	public ControllablePoints getControllablePoint() {
		return controllablePoint;
	}

	public void setControllablePoint(int controllablePoint) {
		this.controllablePoint = ControllablePoints.getControllableCategoryMap().get(controllablePoint);
	}

	public void setControllableCategory(int controllableCategory) {
		this.controllableCategory = ControllableCategory.getControllableCategoryMap().get(controllableCategory);
	}
	
	public ControllableCategory getControllableCategory() {
		return controllableCategory;
	}

	public int getControlActionMode() {
		if(controlActionMode != null) {
			return controlActionMode.getIntVal();
		}
		return -1;
	}

	public void setControlActionMode(int controlActionMode) {
		if(controlActionMode > 0) {
			this.controlActionMode = ControlActionMode.valueOf(controlActionMode);
		}
	}
	
	
	public List<Long> getSpaceExcludeIds() {
		return spaceExcludeIds;
	}
	public void setSpaceExcludeIds(List<Long> spaceExcludeIds) {
		this.spaceExcludeIds = spaceExcludeIds;
	}
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	public List<Long> getSpaceIncludeIds() {
		return spaceIncludeIds;
	}
	public void setSpaceIncludeIds(List<Long> spaceIncludeIds) {
		this.spaceIncludeIds = spaceIncludeIds;
	}
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
		
		FacilioChain rdmChain = ReadOnlyChainFactory.getRDMChain();
		FacilioContext constructListContext = rdmChain.getContext();
		constructListContext(constructListContext);
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getReadingDataMetaModule().getName());
		
		
		rdmChain.execute();
		
		setResult(ControlActionUtil.CONTROL_ACTION_CONTROLLABLE_POINTS, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		setResult(FacilioConstants.ContextNames.READING_DATA_META_COUNT, constructListContext.get(FacilioConstants.ContextNames.READING_DATA_META_COUNT));
		return SUCCESS;
	}
	
	public String getControlGroups() throws Exception {
		
		FacilioChain rdmChain = ReadOnlyChainFactory.fetchControlGroupsChain();
		FacilioContext constructListContext = rdmChain.getContext();
		constructListContext(constructListContext);
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getControlGroupModule().getName());
		
		
		rdmChain.execute();
		
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
		
		FacilioChain commandChain = ReadOnlyChainFactory.getControllableAssetsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROLLABLE_RESOURCES, constructListContext.get(ControlActionUtil.CONTROLLABLE_RESOURCES));
		
		return SUCCESS;
	}
	
	public String getControllableFields() throws Exception {
		
		FacilioContext constructListContext = new FacilioContext();
		
		constructListContext.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		constructListContext.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategoryId);
		
		FacilioChain commandChain = ReadOnlyChainFactory.getControllableFieldsChain();
		
		commandChain.execute(constructListContext);
		
		setResult(ControlActionUtil.CONTROLLABLE_FIELDS, constructListContext.get(ControlActionUtil.CONTROLLABLE_FIELDS));
		
		return SUCCESS;
	}

	public String getControlActionCommands() throws Exception {

		FacilioChain commandChain = ReadOnlyChainFactory.getControlActionCommandsChain();
		FacilioContext constructListContext = commandChain.getContext();
		constructListContext(constructListContext);

		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);


		commandChain.execute();

		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS, constructListContext.get(ControlActionUtil.CONTROL_ACTION_COMMANDS));
		setResult(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT, constructListContext.get(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT));

		return SUCCESS;
	}

	public String addControlActionRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		if(readingAlarmRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.READING_ALARM_RULES, Collections.singletonList(readingAlarmRuleContext));
			context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.CONTROL_ACTION_READING_ALARM_RULE);
			
			FacilioChain addReadingAlarmRuleChain = TransactionChainFactory.addReadingAlarmRuleChain();
			addReadingAlarmRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		}
		else if (workflowRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			workflowRuleContext.setRuleType(RuleType.CONTROL_ACTION_SCHEDULED_RULE);
			
			FacilioChain addWorkflowRuleChain = TransactionChainFactory.addWorkflowRuleChain();
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
			
			FacilioChain addReadingAlarmRuleChain = TransactionChainFactory.updateReadingAlarmRuleChain();
			addReadingAlarmRuleChain.execute(context);
			
			setResult(FacilioConstants.ContextNames.READING_ALARM_RULE, readingAlarmRuleContext);
		}
		else if (workflowRuleContext != null) {
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			workflowRuleContext.setRuleType(RuleType.CONTROL_ACTION_SCHEDULED_RULE);
			
			FacilioChain addWorkflowRuleChain = TransactionChainFactory.updateWorkflowRuleChain();
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
			reportOperators.put("DateAggregateOperator", BmsAggregateOperators.DateAggregateOperator.values());
			reportOperators.put("NumberAggregateOperator", BmsAggregateOperators.NumberAggregateOperator.values());
			reportOperators.put("StringAggregateOperator", BmsAggregateOperators.StringAggregateOperator.values());
			reportOperators.put("SpaceAggregateOperator", BmsAggregateOperators.SpaceAggregateOperator.values());
			reportOperators.put("EnergyPurposeAggregateOperator", BmsAggregateOperators.EnergyPurposeAggregateOperator.values());
			meta.put("reportOperators", reportOperators);
			
			setMeta(meta);
			
		}
		
		return SUCCESS;
	}
	
	public String deleteControlActionRule() throws Exception {
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, Collections.singletonList(ruleId));
		
		FacilioChain deleteRule = TransactionChainFactory.getDeleteWorkflowRuleChain();
		deleteRule.execute(context);
		
		setResult("result", context.get(FacilioConstants.ContextNames.RESULT));
		
		return SUCCESS;
	}
	
	public String updateRDM() throws Exception {
		
		FacilioChain addReadingAlarmRuleChain = TransactionChainFactory.updateReadingDataMetaChain();
		
		FacilioContext context = addReadingAlarmRuleChain.getContext();
		
		context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, Collections.singletonList(rdm));
		
		addReadingAlarmRuleChain.execute();
		
		setResult("rdm", rdm);
		
		return SUCCESS;
	}
	
	public String updateControlModeForBaseSpaces() throws Exception {
		
		if(controlActionMode == null) {
			throw new IllegalArgumentException("controlActionMode cannot be null here");
		}
		if(spaceId > 0) {
			
			FacilioChain getControllableCategoryChain = TransactionChainFactory.updateControllableTypeForSpace();
			
			FacilioContext context = getControllableCategoryChain.getContext();
			
			context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
			context.put(ControlActionUtil.CONTROL_MODE, controlActionMode);
			
			getControllableCategoryChain.execute();
			
		}
		else if (floorId > 0 || spaceIncludeIds != null) {
			
			FacilioChain getControllableCategoryChain = TransactionChainFactory.updateControllableTypeForFloor();
			
			FacilioContext context = getControllableCategoryChain.getContext();
			
			context.put(FacilioConstants.ContextNames.FLOOR_ID, floorId);
			context.put(ControlActionUtil.SPACE_INCLUDE_LIST, spaceIncludeIds);
			context.put(ControlActionUtil.SPACE_EXCLUDE_LIST, spaceExcludeIds);
			context.put(ControlActionUtil.CONTROL_MODE, controlActionMode);
			
			getControllableCategoryChain.execute();
			
		}
		
		return SUCCESS;
		
	}
	
	public String getControlActionRules() throws Exception {
		
		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}
		
		FacilioChain commandChain = ReadOnlyChainFactory.getControlActionRulesChain();
		FacilioContext constructListContext = commandChain.getContext();
		constructListContext(constructListContext);
		
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME,ModuleFactory.getWorkflowRuleModule().getName());
		
		
		commandChain.execute();
		
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULES));
		setResult(FacilioConstants.ContextNames.WORKFLOW_RULES_COUNT, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULES_COUNT));
		
		return SUCCESS;
		
	}
	
	public String getControllableCategoryList() {
		
		setResult(ControlActionUtil.CONTROLLABLE_CATEGORIES, ControllableCategory.getAllCategories());
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
	
	
	public String getControllableCategories() throws Exception {
		
		if(spaceId > 0) {
			FacilioChain getControllableCategoryChain = ReadOnlyChainFactory.getControllableCategoryFromSpaceIdChain();
			
			FacilioContext context = getControllableCategoryChain.getContext();
			
			context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
			
			context.put(ControlActionUtil.CATEGORY_INCLUDE_LIST, categoryIncludeIds);
			context.put(ControlActionUtil.CATEGORY_EXCLUDE_LIST, categoryExcludeIds);
			
			getControllableCategoryChain.execute();
			
			setResult(ControlActionUtil.CONTROLLABLE_CATEGORIES, context.get(ControlActionUtil.CONTROLLABLE_CATEGORIES));
		}
		else if (floorId > 0 || spaceIncludeIds != null) {
			
			FacilioChain getControllableCategoryChain = ReadOnlyChainFactory.getControllableCategoryChain();
			
			FacilioContext context = getControllableCategoryChain.getContext();
			
			context.put(FacilioConstants.ContextNames.FLOOR_ID, floorId);
			context.put(ControlActionUtil.SPACE_INCLUDE_LIST, spaceIncludeIds);
			context.put(ControlActionUtil.SPACE_EXCLUDE_LIST, spaceExcludeIds);
			
			context.put(ControlActionUtil.CATEGORY_INCLUDE_LIST, categoryIncludeIds);
			context.put(ControlActionUtil.CATEGORY_EXCLUDE_LIST, categoryExcludeIds);
			
			getControllableCategoryChain.execute();
			
			setResult(ControlActionUtil.SPACE_CONTROLLABLE_CATEGORIES_MAP, context.get(ControlActionUtil.SPACE_CONTROLLABLE_CATEGORIES_MAP));
		}
		
		return SUCCESS;
		
	}
	
	
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	public String setReadingValueForSpace() throws Exception {
		
		if(spaceId > 0) {
			
			FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForSpaceChain();
			
			FacilioContext context = executeControlActionCommandChain.getContext();
			
			context.put(ControlActionUtil.CONTROLLABLE_CATEGORY, controllableCategory);
			context.put(ControlActionUtil.CONTROLLABLE_POINT, controllablePoint);
			context.put(ControlActionUtil.VALUE, value);
			context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
			context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.MANUAL);
			
			executeControlActionCommandChain.execute();
		}
		else if(floorId > 0) {
			
			FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForFloorChain();
			
			FacilioContext context = executeControlActionCommandChain.getContext();
			
			context.put(ControlActionUtil.CONTROLLABLE_CATEGORY, controllableCategory);
			context.put(ControlActionUtil.CONTROLLABLE_POINT, controllablePoint);
			context.put(ControlActionUtil.VALUE, value);
			context.put(FacilioConstants.ContextNames.FLOOR_ID, floorId);
			context.put(ControlActionUtil.SPACE_INCLUDE_LIST, spaceIncludeIds);
			context.put(ControlActionUtil.SPACE_EXCLUDE_LIST, spaceExcludeIds);
			context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.MANUAL);
			
			executeControlActionCommandChain.execute();
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
			
			FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
			
			FacilioContext context = executeControlActionCommandChain.getContext();
			
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
			context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.CARD);
			
			executeControlActionCommandChain.execute();
		}
		
		return SUCCESS;
	}
	
	
}
