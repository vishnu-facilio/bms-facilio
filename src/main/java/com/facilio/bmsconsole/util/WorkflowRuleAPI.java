package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowContext.WorkflowUIMode;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowGlobalParamUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowRuleAPI {
	private static final Logger LOGGER = LogManager.getLogger(WorkflowRuleAPI.class.getName());
	
	public static Map<String, Object> getOrgPlaceHolders() throws Exception {
		Map<String, Object> placeHolders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
		placeHolders.put("org", AccountUtil.getCurrentOrg());
		placeHolders.put("user", AccountUtil.getCurrentUser());
		
		return placeHolders;
	}
	
	public static Map<String, Object> getRecordPlaceHolders(String moduleName, Object record, Map<String, Object> currentPlaceholders) throws Exception {
		Map<String, Object> recordPlaceHolders = currentPlaceholders == null ? new HashMap<>() : new HashMap<>(currentPlaceholders);
		CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), recordPlaceHolders);
		recordPlaceHolders.put(moduleName, record);
		return recordPlaceHolders;
	}
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		rule.setOrgId(AccountUtil.getCurrentOrg().getId());
		rule.setStatus(true);
		rule.setLatestVersion(true);
		rule.setCreatedTime(DateTimeUtil.getCurrenTime());
		rule.setModifiedTime(rule.getCreatedTime());
		updateWorkflowRuleChildIds(rule);
		
		validateWorkflowRule(rule);
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getWorkflowRuleModule().getTableName())
													.fields(FieldFactory.getWorkflowRuleFields())
													.addRecord(ruleProps);
		insertBuilder.save();
		rule.setId((long) ruleProps.get("id"));
		switch(rule.getRuleTypeEnum()) {
			case READING_RULE:
			case PM_READING_RULE:
			case VALIDATION_RULE:
			case ALARM_TRIGGER_RULE:
			case ALARM_CLEAR_RULE:
			case ALARM_RCA_RULES:
			case PM_READING_TRIGGER:
			case READING_VIOLATION_RULE:
				if (((ReadingRuleContext) rule).getClearAlarm() == null) {
					ruleProps.put("clearAlarm", true);
				}
				
				if (((ReadingRuleContext) rule).getReadingRuleTypeEnum() == null) {
					ruleProps.put("readingRuleType", ReadingRuleContext.ReadingRuleType.THRESHOLD_RULE.getValue());
				}
				
				if(ruleProps.get("ruleGroupId") == null || (Long) ruleProps.get("ruleGroupId") <= 0) {
					ruleProps.put("ruleGroupId", rule.getId());
					((ReadingRuleContext) rule).setRuleGroupId(rule.getId());
				}
				addExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), ruleProps);
				ReadingRuleAPI.addReadingRuleInclusionsExlusions((ReadingRuleContext) rule);
				ReadingRuleAPI.addReadingRuleMetrics((ReadingRuleContext) rule);
				break;
			case READING_ALARM_RULE:
			case CONTROL_ACTION_READING_ALARM_RULE:
			case REPORT_DOWNTIME_RULE:
				addExtendedProps(ModuleFactory.getReadingAlarmRuleModule(), FieldFactory.getReadingAlarmRuleFields(), ruleProps);
				break;
			case SLA_RULE:
				addExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), ruleProps);
				break;
			case SLA_POLICY_RULE:
//				addExtendedProps(ModuleFactory.getSLAPolicyRuleModule(), FieldFactory.getSLAPolicyRuleFields(), ruleProps);
//				SLAWorkflowAPI.addEscalations((SLAPolicyContext) rule);
				break;
			case SLA_WORKFLOW_RULE:
//				addExtendedProps(ModuleFactory.getSLAWorkflowRuleModule(), FieldFactory.getSLAWorkflowRuleFields(), ruleProps);
				SLAWorkflowAPI.addSLACommitmentDuration((SLAWorkflowCommitmentRuleContext) rule);
				break;
			case APPROVAL_RULE:
			case CHILD_APPROVAL_RULE:
				ApprovalRulesAPI.validateApprovalRule((ApprovalRuleContext) rule);
				ApprovalRulesAPI.updateChildRuleIds((ApprovalRuleContext) rule);
				addExtendedProps(ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields(), FieldUtil.getAsProperties(rule));
				ApprovalRulesAPI.addApprover(rule.getId(), ((ApprovalRuleContext) rule).getApprovers());
				break;
			case STATE_RULE:
			case APPROVAL_STATE_TRANSITION:
				ApprovalRulesAPI.addApproverRuleChildren((ApproverWorkflowRuleContext) rule);
				AbstractStateTransitionRuleContext stateflowTransition = (AbstractStateTransitionRuleContext) rule;
				if (stateflowTransition.getDialogTypeEnum() != null && stateflowTransition.getDialogTypeEnum() == AbstractStateTransitionRuleContext.DialogType.MODULE) {
					StateFlowRulesAPI.addOrUpdateFormDetails(stateflowTransition);
				}
				addExtendedProps(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), ruleProps);
				break;
			case STATE_FLOW:
			case APPROVAL_STATE_FLOW:
				addExtendedProps(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), ruleProps);
				break;
			case CUSTOM_BUTTON:
				addExtendedProps(ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields(), ruleProps);
				break;
			case ALARM_WORKFLOW_RULE:
				addExtendedProps(ModuleFactory.getAlarmWorkflowRuleModule(), FieldFactory.getAlarmWorkflowRuleFields(), ruleProps);
				break;
			default:
				break;
		}
		
		if (EventType.FIELD_CHANGE.isPresent(rule.getActivityType())) {
			addFieldChangeFields(rule);
		}
		else if (EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleType() != RuleType.RECORD_SPECIFIC_RULE.getIntVal()) {
			ScheduledRuleAPI.addScheduledRuleJob(rule);
		}

		return rule.getId();
	}
	
	private static void addFieldChangeFields(WorkflowRuleContext rule) throws Exception {
		if (rule.getFields() == null || rule.getFields().isEmpty()) {
			throw new IllegalArgumentException("Atleast one field has to be added for Workflow Rule with Field Change activity");
		}
		
		FacilioModule module = ModuleFactory.getWorkflowFieldChangeFieldsModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getWorkflowFieldChangeFields());
		
		for (FieldChangeFieldContext field : rule.getFields()) {
			field.setRuleId(rule.getId());
			field.setOrgId(AccountUtil.getCurrentOrg().getId());
			insertBuilder.addRecord(FieldUtil.getAsProperties(field));
		}
		insertBuilder.save();
	}
	
	private static void validateWorkflowRule (WorkflowRuleContext rule) throws Exception {
		if (rule.getRuleTypeEnum() == null) {
			throw new IllegalArgumentException("Rule Type cannot be null during addition for Workflow");
		}
		if ((rule.getActivityType() == -1 || rule.getModule() == null) && !rule.getRuleTypeEnum().isChildType()) {
			throw new IllegalArgumentException("Event ID cannot be null during addition for Workflow");
		}
		
		if ((rule.getActivityType() != -1 && rule.getModule() != null) && rule.getRuleType() != RuleType.RECORD_SPECIFIC_RULE.getIntVal() && EventType.SCHEDULED.isPresent(rule.getActivityType())) {
			ScheduledRuleAPI.validateScheduledRule(rule, false);
		}
		if((rule.getActivityType() != -1 && rule.getModule() != null) && rule.getRuleType() == RuleType.RECORD_SPECIFIC_RULE.getIntVal() && EventType.SCHEDULED.isPresent(rule.getActivityType())) {
			SingleRecordRuleAPI.validateRecordSpecificScheduledRule(rule, false);
		}
	}
	
	private static void addExtendedProps(FacilioModule module, List<FacilioField> fields, Map<String, Object> ruleProps) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(ruleProps);
		insertBuilder.save();
	}
	
	protected static final void updateWorkflowRuleChildIds(WorkflowRuleContext workflowRuleContext) throws Exception {
		if(workflowRuleContext.getCriteria() != null) {
			workflowRuleContext.getCriteria().validatePattern();
			long criteriaId = CriteriaAPI.addCriteria(workflowRuleContext.getCriteria(),AccountUtil.getCurrentOrg().getId());
			workflowRuleContext.setCriteriaId(criteriaId);
		}
		if(workflowRuleContext.getWorkflow() != null) {
			
			if(AccountUtil.getCurrentOrg().getId() == 1) {
				if(workflowRuleContext.getWorkflow().getWorkflowUIMode() != WorkflowUIMode.XML.getValue()) {

					fillExtraParamsForWorkflowV2(workflowRuleContext.getWorkflow()); 		// ^^^^^^^^^ TO BE REVERTED ON ERROR ^^^^^^^^
				}
			}
			
			long workflowId = WorkflowUtil.addWorkflow(workflowRuleContext.getWorkflow());
			workflowRuleContext.setWorkflowId(workflowId);
		}
	}
	
	private static void fillExtraParamsForWorkflowV2(WorkflowContext workflow) {
		
		if(workflow.getParameters() != null && !workflow.getParameters().isEmpty()) {
			
			int size = workflow.getParameters().size();
			for(int i=0;i<size;i++) {
				ParameterContext param = workflow.getParameters().get(i);
				if(param.getName().equals(WorkflowGlobalParamUtil.PREVIOUS_VALUE)) {
					workflow.getParameters().remove(i);
					break;
				}
			}
		}
		
		
		workflow.setIsV2Script(Boolean.TRUE);
		workflow.setReturnType(WorkflowFieldType.BOOLEAN.getIntValue());
	}
	
	public static WorkflowRuleContext updateWorkflowRuleWithChildren(WorkflowRuleContext rule) throws Exception {
		WorkflowRuleContext oldRule = getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateWorkflowRule(rule);
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if(EventType.SCHEDULED.isPresent(oldRule.getActivityType()) && rule.getRuleTypeEnum() != RuleType.RECORD_SPECIFIC_RULE) {
			if (rule.getTimeObj() != null) {
				ScheduledRuleAPI.validateScheduledRule(rule, true);
				ScheduledRuleAPI.updateScheduledRuleJob(rule);
			}
			
			if (rule.getStatus() != null) {
				if (rule.isActive()) {
					if (rule.getTimeObj() == null) {
						ScheduledRuleAPI.updateScheduledRuleJob(oldRule);
					}
				}
				else {
					ScheduledRuleAPI.deleteScheduledRuleJob(rule);
				}
			}
		}
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateWorkflowRule(WorkflowRuleContext rule) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		rule.setModifiedTime(DateTimeUtil.getCurrenTime());
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getWorkflowRuleFields())
													.andCondition(CriteriaAPI.getIdCondition(rule.getId(), module));
		return updateBuilder.update(ruleProps);
	}
	
	public static int updateExtendedRule(WorkflowRuleContext extendedRule, FacilioModule extendedModule, List<FacilioField> extendedFields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(extendedFields);
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(extendedModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(extendedModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getIdCondition(extendedRule.getId(), extendedModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(extendedRule));
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId) throws Exception {
		return getWorkflowRule(ruleId, true, true);
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId, boolean fetchChildren, boolean fetchExtended) throws Exception {
		if (ruleId <= 0) {
			return null;
		}
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getIdCondition(ruleId, module));

		ruleBuilder.select(fields);
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchChildren, fetchExtended);
		if(rules != null && !rules.isEmpty()) {
			return rules.get(0);
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getAllWorkflowRuleContextOfType (WorkflowRuleContext.RuleType ruleType,boolean fetchAction,boolean fetchActiveRulesOnly) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", ruleType.getIntVal()+"", StringOperators.IS));
		
		if(fetchActiveRulesOnly) {
			ruleBuilder.andCondition(CriteriaAPI.getCondition("STATUS", "status", 1+"", StringOperators.IS));
		}

		ruleBuilder.select(fields);
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
		
		if(fetchAction && rules != null) {
			for(WorkflowRuleContext rule :rules) {
				List<ActionContext> actionList = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
				rule.setActions(actionList);
			}
		}
		
		if(rules != null && !rules.isEmpty()) {
			return rules;
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules() throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(fields);
	
		//to fetch the rules without parent id
		ruleBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), CommonOperators.IS_EMPTY));

		return getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
	}

	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids, boolean fetchChildren, boolean fetchExtended) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;

		return getWorkFlowsFromMapList(ruleBuilder.get(), fetchChildren, fetchExtended);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids) throws Exception {
		return getWorkflowRules(ids, true, true);
	}

	public static Map<Long, WorkflowRuleContext> getWorkflowRulesAsMap (List<Long> ids, boolean fetchChildren, boolean fetchExtended) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchChildren, fetchExtended);
		if (rules != null && !rules.isEmpty()) {
			return rules.stream()
						.collect(Collectors.toMap(WorkflowRuleContext::getId, Function.identity()))
						;
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long moduleId) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleFields())
				.table("Workflow_Rule")
				.andCustomWhere("Workflow_Rule.MODULEID = ?", moduleId);
		return getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRulesOfType(RuleType type, boolean fetchChildren) throws Exception{
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleTypeField = fieldMap.get("ruleType");
		FacilioField latestVersionField = fieldMap.get("latestVersion");
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(ruleTypeField, String.valueOf(type.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(latestVersionField, String.valueOf(true), BooleanOperators.IS))
				;
		
		return getWorkFlowsFromMapList(builder.get(), fetchChildren, true);
	}

	public static List<WorkflowRuleContext> getExtendedWorkflowRules(FacilioModule module, List<FacilioField> fields, Criteria criteria, String searchQuery, JSONObject pagination, Class clazz) throws Exception {
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		FacilioModule workflowRuleModule = ModuleFactory.getWorkflowRuleModule();
		FacilioField ruleNameField = FieldFactory.getAsMap(fields).get("name");

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(workflowRuleModule.getTableName())
				.select(fields)
				.innerJoin(module.getTableName()).on("Workflow_Rule.ID = " + module.getTableName() + ".ID")
				;
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			builder.offset(offset);
			builder.limit(perPage);
		}
		if (searchQuery!= null) {
			builder.andCondition(CriteriaAPI.getCondition(ruleNameField, searchQuery, StringOperators.CONTAINS));
		}
		if(criteria != null && !criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}

		List<Map<String, Object>> list = builder.get();
		if (clazz == null) {
			clazz = WorkflowRuleContext.class;
		}
		return FieldUtil.getAsBeanListFromMapList(list, clazz);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(RuleType type, boolean fetchChildren, Criteria criteria, String searchQuery, JSONObject pagination) throws Exception{
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleTypeField = fieldMap.get("ruleType");
		FacilioField latestVersionField = fieldMap.get("latestVersion");
		FacilioField ruleNameField = FieldFactory.getAsMap(fields).get("name");
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(ruleTypeField, String.valueOf(type.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(latestVersionField, String.valueOf(true), BooleanOperators.IS))
				;
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		if (searchQuery!= null) {
			builder.andCondition(CriteriaAPI.getCondition(ruleNameField, searchQuery, StringOperators.CONTAINS));
		}
		if(criteria != null && !criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
	
		return getWorkFlowsFromMapList(builder.get(), fetchChildren, true);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivityAndRuleType(FacilioModule module, List<EventType> activityTypes,Criteria criteria, RuleType... ruleTypes) throws Exception {
		FacilioModule ruleModule = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(ruleModule.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.orderBy("EXECUTION_ORDER");
		
		ruleBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), CommonOperators.IS_EMPTY));
		
		if(ruleTypes != null && ruleTypes.length > 0) {
			StringJoiner ids = new StringJoiner(",");
			for(RuleType type : ruleTypes) {
				ids.add(String.valueOf(type.getIntVal()));
			}
			Condition ruleTypeCondition = new Condition();
			ruleTypeCondition.setColumnName("RULE_TYPE");
			ruleTypeCondition.setOperator(NumberOperators.EQUALS);
			ruleTypeCondition.setValue(ids.toString());
			ruleBuilder.andCondition(ruleTypeCondition);
		}
		
		if (criteria != null) {
			ruleBuilder.andCriteria(criteria);
		}
		
		StringBuilder activityTypeWhere = new StringBuilder();
		List<Integer> values = new ArrayList<>();
		boolean first = true;
		for (EventType type : activityTypes) {
			if(first) {
				first = false;
			}
			else {
				activityTypeWhere.append(" OR ");
			}
			activityTypeWhere.append("? & Workflow_Rule.ACTIVITY_TYPE = ?");
			values.add(type.getValue());
			values.add(type.getValue());
		}
		ruleBuilder.andCustomWhere(activityTypeWhere.toString(), values.toArray());
		ruleBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), CommonOperators.IS_EMPTY));
		List<Map<String, Object>> props = ruleBuilder.get();
		return getWorkFlowsFromMapList(props, true, true);
	}
	
	protected static void deleteChildIdsForWorkflow(WorkflowRuleContext oldRule, WorkflowRuleContext newRule) throws Exception {
		if(newRule.getCriteria() != null && oldRule.getCriteriaId() != -1) {
			CriteriaAPI.deleteCriteria(oldRule.getCriteriaId());
		}
		if(newRule.getWorkflow() != null && oldRule.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(oldRule.getWorkflowId());
		}
	}
	
	private static Map<Long, Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, List<Long> ids) throws Exception {
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(fields)
																.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<Map<String, Object>> extendedProps = selectRecordBuilder.get();
		
		if (extendedProps != null && !extendedProps.isEmpty()) {
			for(Map<String, Object> prop : extendedProps) {
				propsMap.put((Long) prop.get("id"), prop);
			}
		}
		return propsMap;
	}
	
	private static Map<RuleType, Map<Long, Map<String, Object>>> getTypeWiseExtendedProps(Map<RuleType, List<Long>> typeWiseIds) throws Exception {
		Map<RuleType, Map<Long, Map<String, Object>>> typeWiseProps = new HashMap<>();
		for(Map.Entry<RuleType, List<Long>> entry : typeWiseIds.entrySet()) {
			switch (entry.getKey()) {
				case READING_RULE:
				case PM_READING_RULE:
				case VALIDATION_RULE:
				case ALARM_TRIGGER_RULE:
				case ALARM_CLEAR_RULE:
				case ALARM_RCA_RULES:
				case PM_READING_TRIGGER:
				case READING_VIOLATION_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), entry.getValue()));
					break;
				case SLA_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), entry.getValue()));
					break;
				case SLA_POLICY_RULE:
//					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSLAPolicyRuleModule(), FieldFactory.getSLAPolicyRuleFields(), entry.getValue()));
					break;
				case SLA_WORKFLOW_RULE:
//					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSLAWorkflowRuleModule(), FieldFactory.getSLAWorkflowRuleFields(), entry.getValue()));
					break;
				case READING_ALARM_RULE:
				case CONTROL_ACTION_READING_ALARM_RULE:
				case REPORT_DOWNTIME_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingAlarmRuleModule(), FieldFactory.getReadingAlarmRuleFields(), entry.getValue()));
					break;
				case APPROVAL_RULE:
				case CHILD_APPROVAL_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields(), entry.getValue()));
					break;
				case STATE_RULE:
				case APPROVAL_STATE_TRANSITION:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), entry.getValue()));
					break;
				case STATE_FLOW:
				case APPROVAL_STATE_FLOW:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), entry.getValue()));
					break;
				case CUSTOM_BUTTON:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields(), entry.getValue()));
					break;
				case ALARM_WORKFLOW_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getAlarmWorkflowRuleModule(), FieldFactory.getAlarmWorkflowRuleFields(), entry.getValue()));
					break;
				default:
					break;
			}
		}
		return typeWiseProps;
	}
	
	private static Map<Long, List<FieldChangeFieldContext>> getFieldChangeFields(List<Long> ruleIds) throws Exception {
		if (ruleIds != null && !ruleIds.isEmpty()) {
			FacilioModule module = ModuleFactory.getWorkflowFieldChangeFieldsModule();
			List<FacilioField> fields = FieldFactory.getWorkflowFieldChangeFields();
			FacilioField ruleField = FieldFactory.getAsMap(fields).get("ruleId");
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.table(module.getTableName())
															.select(fields)
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getCondition(ruleField, ruleIds, PickListOperators.IS))
															;
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				Map<Long, List<FieldChangeFieldContext>> fieldMap = new HashMap<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Map<String, Object> prop : props) {
					FieldChangeFieldContext field = FieldUtil.getAsBeanFromMap(prop, FieldChangeFieldContext.class);
					field.setField(modBean.getField(field.getFieldId()));
					List<FieldChangeFieldContext> fieldList = fieldMap.get(field.getRuleId());
					if (fieldList == null) {
						fieldList = new ArrayList<>();
						fieldMap.put(field.getRuleId(), fieldList);
					}
					fieldList.add(field);
				}
				return fieldMap; 
			}
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, boolean fetchChildren, boolean fetchExtended) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			List<Long> workflowIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> criteriaIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> fieldChangeRuleIds = new ArrayList<>();
			Map<RuleType, List<Long>> typeWiseIds = fetchExtended ? new HashMap<>() : null;
			
			for(Map<String, Object> prop : props) {
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				
				if (fetchExtended) {
					List<Long> idList = typeWiseIds.get(ruleType);
					if(idList == null) {
						idList = new ArrayList<>();
						typeWiseIds.put(ruleType, idList);
					}
					idList.add((Long) prop.get("id"));
				}
				
				if (fetchChildren) {
					Long workflowId = (Long) prop.get("workflowId");
					if (workflowId != null) {
						workflowIds.add(workflowId);
					}
					Long criteriaId = (Long) prop.get("criteriaId");
					if (criteriaId != null) {
						criteriaIds.add(criteriaId);
					}
				}

				int activity = (int) prop.get("activityType");
				if (EventType.FIELD_CHANGE.isPresent(activity)) {
					fieldChangeRuleIds.add((Long) prop.get("id"));
				}
			}
			Map<RuleType, Map<Long, Map<String, Object>>> typeWiseExtendedProps = fetchExtended ? getTypeWiseExtendedProps(typeWiseIds) : null;
			Map<Long, WorkflowContext> workflowMap = fetchChildren && !workflowIds.isEmpty() ? WorkflowUtil.getWorkflowsAsMap(workflowIds, true) : null;
			Map<Long, Criteria> criteriaMap = fetchChildren && !criteriaIds.isEmpty() ? CriteriaAPI.getCriteriaAsMap(criteriaIds) : null;
			Map<Long, List<FieldChangeFieldContext>> ruleFieldsMap = getFieldChangeFields(fieldChangeRuleIds);

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext rule = null;
				
				if (fetchExtended) {
					RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
					switch(ruleType) {
						case PM_READING_RULE:
						case READING_RULE:
						case VALIDATION_RULE:
						case ALARM_TRIGGER_RULE:
						case ALARM_CLEAR_RULE:
						case ALARM_RCA_RULES:
						case PM_READING_TRIGGER:
						case READING_VIOLATION_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = ReadingRuleAPI.constructReadingRuleFromProps(prop, modBean, fetchChildren);
							break;
						case SLA_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = SLARuleAPI.constructSLARuleFromProps(prop, modBean);
							break;
						case SLA_POLICY_RULE:
							rule = FieldUtil.getAsBeanFromMap(prop, SLAPolicyContext.class);
							((SLAPolicyContext) rule).setEscalations(SLAWorkflowAPI.getSLAPolicyEntityEscalations(rule.getId()));
							break;
						case SLA_WORKFLOW_RULE:
							rule = FieldUtil.getAsBeanFromMap(prop, SLAWorkflowCommitmentRuleContext.class);
							((SLAWorkflowCommitmentRuleContext) rule).setSlaEntities(SLAWorkflowAPI.getSLAEntitiesForCommitment(rule.getId()));
							break;
						case APPROVAL_RULE:
						case CHILD_APPROVAL_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = ApprovalRulesAPI.constructApprovalRuleFromProps(prop, modBean);
							break;
						case READING_ALARM_RULE:
						case CONTROL_ACTION_READING_ALARM_RULE:
						case REPORT_DOWNTIME_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = constructReadingAlarmRuleFromProps(prop, modBean);
							break;
						case STATE_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, StateflowTransitionContext.class);
							break;
						case APPROVAL_STATE_TRANSITION:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, ApprovalStateTransitionRuleContext.class);
							break;
						case STATE_FLOW:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, StateFlowRuleContext.class);
							break;
						case APPROVAL_STATE_FLOW:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, ApprovalStateFlowRuleContext.class);
							break;
						case CUSTOM_BUTTON:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, CustomButtonRuleContext.class);
							break;
						case ALARM_WORKFLOW_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, AlarmWorkflowRuleContext.class);
							break;
						default:
							rule = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
							break;
					}
				}
				else {
					rule = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
				}
				
				long criteriaId = rule.getCriteriaId();
				if (fetchChildren && criteriaId != -1) {
					rule.setCriteria(criteriaMap.get(criteriaId));
				}
				
				long workflowId = rule.getWorkflowId();
				if (fetchChildren && workflowId != -1) {
					rule.setWorkflow(workflowMap.get(workflowId));
				}

				if (EventType.FIELD_CHANGE.isPresent(rule.getActivityType())) {
					rule.setFields(ruleFieldsMap.get(rule.getId()));
				}
				else if (EventType.SCHEDULED.isPresent(rule.getActivityType())) {
					rule.setDateField(modBean.getField(rule.getDateFieldId()));
				}
				workflows.add(rule);
			}

			StateFlowRulesAPI.constructStateRule(workflows);
			return workflows;
		}
		return null;
	}
	
	protected static ReadingAlarmRuleContext constructReadingAlarmRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		ReadingAlarmRuleContext readingRule = FieldUtil.getAsBeanFromMap(prop, ReadingAlarmRuleContext.class);
		return readingRule;
	}
	
	public static String constructParentWorkflowRuleKey(long parentRuleId, Boolean onSuccessParentKey) {
		onSuccessParentKey = (onSuccessParentKey == null) ? Boolean.FALSE : onSuccessParentKey;
		return onSuccessParentKey ? (parentRuleId + "_" + 1) : (parentRuleId + "_" + 0);			
	}
	
	public static void deleteWorkFlowRules(List<Long> workflowIds) throws Exception {
		if (workflowIds != null && !workflowIds.isEmpty()) {
			List<WorkflowRuleContext> rules = getWorkflowRules(workflowIds);
			List<Long> deleteIds = new ArrayList<Long>();
			List<Long> updateIds = new ArrayList<Long>();;
			FacilioModule module = ModuleFactory.getWorkflowRuleModule();

			if (rules != null && !rules.isEmpty()) {
				for(WorkflowRuleContext rule: rules ) {
					if (rule.isLatestVersion() && rule.getRuleTypeEnum().versionSupported()) {
						updateIds.add(rule.getId());
					}
					else {
						deleteIds.add(rule.getId());
					}
					if (EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleType() != RuleType.RECORD_SPECIFIC_RULE.getIntVal() ) {
						ScheduledRuleAPI.deleteScheduledRuleJob(rule);
					}
					else if(EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleType() == RuleType.RECORD_SPECIFIC_RULE.getIntVal()) {
						SingleRecordRuleAPI.deleteRecordSpecificRuleJob(rule);
					}
					else if (EventType.SCHEDULED_READING_RULE.isPresent(rule.getActivityType())) {
						FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
					}
				}
			}
			if (deleteIds.size() > 0) {
				ActionAPI.deleteAllActionsFromWorkflowRules(workflowIds);
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getIdCondition(workflowIds, module));
				deleteBuilder.delete();
				
				for (WorkflowRuleContext rule : rules) {
					switch (rule.getRuleTypeEnum()) {
						case APPROVAL_RULE:
						case CHILD_APPROVAL_RULE:
							ApprovalRulesAPI.deleteApprovalRuleChildIds((ApprovalRuleContext) rule);
							break;
						case STATE_RULE:
						case APPROVAL_STATE_TRANSITION:
						case CUSTOM_BUTTON:
							ApprovalRulesAPI.deleteApproverRuleChildren((ApproverWorkflowRuleContext) rule);
							StateFlowRulesAPI.deleteFormRuleContext((FormInterface) rule);
							break;
						default:
							break;
					}
					
					deleteChildIdsForWorkflow(rule, rule);
				}
			}
			if (updateIds.size() > 0) {
				Map<String, Object> ruleProps = new HashMap<>();
				ruleProps.put("latestVersion", false);
				ruleProps.put("status", false);
				ruleProps.put("modifiedTime", DateTimeUtil.getCurrenTime());
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(FieldFactory.getWorkflowRuleFields())
															.andCondition(CriteriaAPI.getIdCondition(updateIds, module));
				updateBuilder.update(ruleProps);
			}
		}
	}
	
	public static void deleteWorkflowRule(long workflowId) throws Exception {
		if (workflowId != -1) {
			deleteWorkFlowRules(Collections.singletonList(workflowId));
		}
	}
	
	private static boolean evalFieldChange(WorkflowRuleContext rule, List<UpdateChangeSet> changeSetList) {
		if (rule.getActivityTypeEnum() == EventType.FIELD_CHANGE) {
			if (changeSetList != null && !changeSetList.isEmpty()) {
				for (FieldChangeFieldContext field : rule.getFields()) {
					for (UpdateChangeSet changeSet : changeSetList) {
						if (field.getFieldId() == changeSet.getFieldId() 
								&& (field.getOldValue() == null || ( changeSet.getOldValue() != null && field.getOldValue().toString().equals(changeSet.getOldValue().toString())) )
								&& (field.getNewValue() == null || ( changeSet.getNewValue() != null &&  field.getNewValue().toString().equals(changeSet.getNewValue().toString())) )
								) {
							return true;
						}
					}
				}
			}
			return false;
		}
		return true;
	}
	
	public static boolean evaluateWorkflowAndExecuteActions(WorkflowRuleContext workflowRule, String moduleName, Object record, List<UpdateChangeSet> changeSet, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		return evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, changeSet, recordPlaceHolders, context, true);
	}
	
	public static boolean evaluateWorkflowAndExecuteActions(WorkflowRuleContext workflowRule, String moduleName, Object record, List<UpdateChangeSet> changeSet, Map<String, Object> recordPlaceHolders, FacilioContext context, boolean shouldExecute) throws Exception {
		Map<String, Object> rulePlaceHolders = workflowRule.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
		boolean fieldChangeFlag = false, miscFlag = false, criteriaFlag = false, workflowFlag = false , siteId = false;

		siteId = workflowRule.evaluateSite(moduleName, record, rulePlaceHolders, context);
		if (siteId) {
			fieldChangeFlag = evalFieldChange(workflowRule, changeSet);
			if (fieldChangeFlag) {
				miscFlag = workflowRule.evaluateMisc(moduleName, record, rulePlaceHolders, context);
				if (miscFlag) {
					criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, rulePlaceHolders, context);
					if (criteriaFlag) {
						workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, rulePlaceHolders, context);
					}
				}
			}
		}
		if (AccountUtil.getCurrentOrg().getId() == 286l && (workflowRule.getId() == 26740l)) {
			LOGGER.error("Result of rule : "+workflowRule.getId()+" for record : "+record+" is \nSite ID : "+siteId+"\nField Change : "+fieldChangeFlag+"\nMisc Flag : "+miscFlag+"\nCriteria Flag : "+criteriaFlag+"\nWorkflow Flag : "+workflowFlag);
		}
		if (AccountUtil.getCurrentOrg().getId() == 155l && (workflowRule.getId() == 32177l)) {
			LOGGER.error("Result of rule : "+workflowRule.getId()+" for record : "+record+" is \nSite ID : "+siteId+"\nField Change : "+fieldChangeFlag+"\nMisc Flag : "+miscFlag+"\nCriteria Flag : "+criteriaFlag+"\nWorkflow Flag : "+workflowFlag);
		}
		if (AccountUtil.getCurrentOrg().getId() == 186 && workflowRule.getId() == 6448) {
			LOGGER.info("Result of rule : "+workflowRule.getId()+" for record : "+record+" is \nSite ID : "+siteId+"\nField Change : "+fieldChangeFlag+"\nMisc Flag : "+miscFlag+"\nCriteria Flag : "+criteriaFlag+"\nWorkflow Flag : "+workflowFlag);
		}
		
		if (AccountUtil.getCurrentOrg().getId() == 134l && (workflowRule.getId() == 4235l || workflowRule.getId() == 6793l)) {
			LOGGER.error("Result of rule : "+workflowRule.getId()+" for record : "+record+" is \nSite ID : "+siteId+"\nField Change : "+fieldChangeFlag+"\nMisc Flag : "+miscFlag+"\nCriteria Flag : "+criteriaFlag+"\nWorkflow Flag : "+workflowFlag);
		}
		if (AccountUtil.getCurrentOrg().getId() == 88 && workflowRule.getId() == 7762l) {
			LOGGER.info("Result of rule : "+workflowRule.getId()+" for record : "+record+" is \nSite ID : "+siteId+"\nField Change : "+fieldChangeFlag+"\nMisc Flag : "+miscFlag+"\nCriteria Flag : "+criteriaFlag+"\nWorkflow Flag : "+workflowFlag);
		}
		
		boolean result = fieldChangeFlag && miscFlag && criteriaFlag && workflowFlag && siteId ;
		if (shouldExecute) {
			if(result) {
				workflowRule.executeTrueActions(record, context, rulePlaceHolders);
			}
			else {
				workflowRule.executeFalseActions(record, context, rulePlaceHolders);
			}
		}
		return result;
	}
	

	public static void executeWorkflowsAndGetChildRuleCriteria(List<WorkflowRuleContext> workflowRules, FacilioModule module, Object record, List<UpdateChangeSet> changeSet, Map<String, Object> recordPlaceHolders, FacilioContext context,boolean propagateError, Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap, boolean isParallelRuleExecution, List<EventType> eventTypes, RuleType... ruleTypes) throws Exception {

		if(workflowRules != null && !workflowRules.isEmpty()) {
			Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
			FacilioField parentRule = fields.get("parentRuleId");
			FacilioField onSuccess = fields.get("onSuccess");

			for(WorkflowRuleContext workflowRule : workflowRules) {
				boolean stopFurtherExecution = workflowRule.executeRuleAndChildren(workflowRule, module, record, changeSet, recordPlaceHolders, context, propagateError, parentRule, onSuccess, workflowRuleCacheMap, isParallelRuleExecution, eventTypes, ruleTypes);
				if(stopFurtherExecution) {
					break;
				}
			}
		}
	}
	
	public static WorkflowRuleContext getWorkflowRuleByRuletype(long parentRuleId,RuleType ruletype) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
		select.table(ModuleFactory.getWorkflowRuleModule().getTableName()).select(fields)
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleType"),String.valueOf(ruletype.getIntVal()) ,NumberOperators.EQUALS))
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleId"), String.valueOf(parentRuleId), NumberOperators.EQUALS));
		//to fetch the rules without parent id
		select.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), CommonOperators.IS_EMPTY));

		List<Map<String, Object>> props = select.get();
		List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, true, true);
		return (workflowRuleContexts!=null) ? workflowRuleContexts.get(0) : null;
	}
	public static List<ReadingAlarmRuleContext> getReadingAlarmRulesFromReadingRuleGroupId(long readingGroupId) throws Exception {
		
		if(readingGroupId > 0) {
			List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
			fields.addAll(FieldFactory.getReadingAlarmRuleFields());
			
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
			select.table(ModuleFactory.getWorkflowRuleModule().getTableName())
			.innerJoin(ModuleFactory.getReadingAlarmRuleModule().getTableName())
			.on(ModuleFactory.getWorkflowRuleModule().getTableName()+".ID = "+ModuleFactory.getReadingAlarmRuleModule().getTableName()+".ID")
			.select(fields)
			.andCustomWhere("READING_RULE_GROUP_ID = ?", readingGroupId);
			
			List<Map<String, Object>> props = select.get();
			
			if(props!= null && !props.isEmpty()) {
				
				List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, true, true);
				List<ReadingAlarmRuleContext> readingAlarmRuleContexts = new ArrayList<>();
				for(WorkflowRuleContext workflowRuleContext :workflowRuleContexts) {
					workflowRuleContext.setActions(ActionAPI.getActiveActionsFromWorkflowRule(workflowRuleContext.getId()));
					readingAlarmRuleContexts.add((ReadingAlarmRuleContext)workflowRuleContext);
				}
				return readingAlarmRuleContexts;
			}
		}
		return null;
	}
	public static List<ReadingAlarmRuleContext> getReadingAlarmRules(long readingGroupId) throws Exception {

		if(readingGroupId > 0) {
			List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
			fields.addAll(FieldFactory.getReadingAlarmRuleFields());

			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
			select.table(ModuleFactory.getWorkflowRuleModule().getTableName())
					.innerJoin(ModuleFactory.getReadingAlarmRuleModule().getTableName())
					.on(ModuleFactory.getWorkflowRuleModule().getTableName()+".ID = "+ModuleFactory.getReadingAlarmRuleModule().getTableName()+".ID")
					.select(fields)
					.andCustomWhere("READING_RULE_GROUP_ID = ?", readingGroupId);

			List<Map<String, Object>> props = select.get();

			if(props!= null && !props.isEmpty()) {

				List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, false, true);
				List<ReadingAlarmRuleContext> readingAlarmRuleContexts = new ArrayList<>();
				for(WorkflowRuleContext workflowRuleContext :workflowRuleContexts) {
					readingAlarmRuleContexts.add((ReadingAlarmRuleContext) workflowRuleContext);
				}
				return readingAlarmRuleContexts;
			}
		}
		return null;
	}

	public static void executeScheduledRule (WorkflowRuleContext rule, long executionTime, FacilioContext context) throws Exception {
		FacilioModule module = rule.getModule();
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);
		recordPlaceHolders.put("executionTime", executionTime);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, executionTime);
		Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
		WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(rule), module, null, null, recordPlaceHolders, context,true, workflowRuleCacheMap, false, Collections.singletonList(rule.getActivityTypeEnum()));
	}

	public static void updateExecutionOrder(WorkflowRuleContext rule) throws Exception {
		FacilioField field = FieldFactory.getField("executionOrder", "max(EXECUTION_ORDER)", FieldType.NUMBER);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.select(Collections.singletonList(field))
				.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(rule.getRuleType()), NumberOperators.EQUALS));
		Map<String, Object> map = builder.fetchFirst();
		int executionOrder = 1;
		if (MapUtils.isNotEmpty(map)) {
			Integer order = (Integer) map.get("executionOrder");
			if (order == null) {
				executionOrder = 0;
			}
			executionOrder ++;
		}
		rule.setExecutionOrder(executionOrder);
	}

}
