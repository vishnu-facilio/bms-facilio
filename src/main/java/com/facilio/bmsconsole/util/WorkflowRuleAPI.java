package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

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
				addExtendedProps(ModuleFactory.getReadingAlarmRuleModule(), FieldFactory.getReadingAlarmRuleFields(), ruleProps);
				break;
			case SLA_RULE:
				addExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), ruleProps);
				break;
			case APPROVAL_RULE:
			case CHILD_APPROVAL_RULE:
				ApprovalRulesAPI.validateApprovalRule((ApprovalRuleContext) rule);
				ApprovalRulesAPI.updateChildRuleIds((ApprovalRuleContext) rule);
				addExtendedProps(ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields(), FieldUtil.getAsProperties(rule));
				ApprovalRulesAPI.addApprovers(rule.getId(), ((ApprovalRuleContext) rule).getApprovers());
				break;
			case STATE_RULE:
				addExtendedProps(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), ruleProps);
				ApprovalRulesAPI.addApprovers(rule.getId(), ((StateflowTransitionContext) rule).getApprovers());
				ApprovalRulesAPI.addValidations(rule.getId(), ((StateflowTransitionContext) rule).getValidations());
				StateFlowRulesAPI.addStateFlowTransitionChildren((StateflowTransitionContext) rule);
				break;
			case STATE_FLOW:
				addExtendedProps(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), ruleProps);
				break;
			default:
				break;
		}
		
		if (rule.getEvent() != null) {
			if (EventType.FIELD_CHANGE.isPresent(rule.getEvent().getActivityType())) {
				addFieldChangeFields(rule);
			}
			else if (EventType.SCHEDULED.isPresent(rule.getEvent().getActivityType())) {
				ScheduledRuleAPI.addScheduledRuleJob(rule);
			}
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
		if (rule.getEventId() == -1 && !rule.getRuleTypeEnum().isChildType()) {
			throw new IllegalArgumentException("Event ID cannot be null during addition for Workflow");
		}
		
		if (rule.getEvent() != null && EventType.SCHEDULED.isPresent(rule.getEvent().getActivityType())) {
			ScheduledRuleAPI.validateScheduledRule(rule, false);
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
			long workflowId = WorkflowUtil.addWorkflow(workflowRuleContext.getWorkflow());
			workflowRuleContext.setWorkflowId(workflowId);
		}
		
		if(workflowRuleContext.getEventId() == -1 && workflowRuleContext.getEvent() != null) {
			workflowRuleContext.setEventId(addOrGetWorkflowEvent(workflowRuleContext.getEvent()));
		}
	}
	
	public static final WorkflowEventContext getWorkFlowEvent(EventType type, long moduleId) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getWorkflowEventFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("MODULEID = ? AND ACTIVITY_TYPE = ?", moduleId, type.getValue());
		
		List<Map<String, Object>> eventProps = selectBuilder.get();
		if (eventProps != null && !eventProps.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(eventProps.get(0), WorkflowEventContext.class);
		}
		return null;
	}
	
	public static final long addOrGetWorkflowEvent(WorkflowEventContext event) throws Exception {
		if(event.getActivityTypeEnum() == null) {
			throw new IllegalArgumentException("Activity type cannot be null during addition of Workflow Event");
		}
		if(event.getModuleId() == -1 && event.getModuleName() != null && !event.getModuleName().isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(event.getModuleName());
			event.setModuleId(module.getModuleId());
		}
		
		if(event.getModuleId() == -1 && event.getModule() != null) {
			event.setModuleId(event.getModule().getModuleId());
		}
		
		if(event.getModuleId() == -1) {
			throw new IllegalArgumentException("Module cannot be null while adding Workflow event");
		}
		
		WorkflowEventContext existingEvent = getWorkFlowEvent(event.getActivityTypeEnum(), event.getModuleId());
		if (existingEvent != null) {
			return existingEvent.getId();
		}
		event.setOrgId(AccountUtil.getCurrentOrg().getId());
		FacilioModule module = ModuleFactory.getWorkflowEventModule();
		Map<String, Object> eventProps = FieldUtil.getAsProperties(event);
		GenericInsertRecordBuilder eventBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getWorkflowEventFields())
														.table(module.getTableName())
														.addRecord(eventProps);
		
		eventBuilder.save();
		return (long) eventProps.get("id");
	}
	
	public static WorkflowRuleContext updateWorkflowRuleWithChildren(WorkflowRuleContext rule) throws Exception {
		WorkflowRuleContext oldRule = getWorkflowRule(rule.getId(), true);
		updateWorkflowRuleChildIds(rule);
		updateWorkflowRule(rule);
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if(EventType.SCHEDULED.isPresent(oldRule.getEvent().getActivityType())) {
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
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule))
														.andCondition(CriteriaAPI.getIdCondition(extendedRule.getId(), extendedModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(extendedRule));
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId) throws Exception {
		return getWorkflowRule(ruleId, false);
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId, boolean fetchEvent) throws Exception {
		return getWorkflowRule(ruleId, fetchEvent, true, true);
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId, boolean fetchEvent, boolean fetchChildren, boolean fetchExtended) throws Exception {
		if (ruleId <= 0) {
			return null;
		}
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(ruleId, module));
		
		if (fetchEvent) {
			fields.addAll(FieldFactory.getWorkflowEventFields());
			FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
			ruleBuilder.innerJoin(eventModule.getTableName())
						.on(module.getTableName()+".EVENT_ID = "+eventModule.getTableName()+".ID");
		}
		ruleBuilder.select(fields);
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, fetchChildren, fetchExtended);
		if(rules != null && !rules.isEmpty()) {
			return rules.get(0);
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getAllWorkflowRuleContextOfType (WorkflowRuleContext.RuleType ruleType,boolean fetchEvent,boolean fetchAction) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", ruleType.getIntVal()+"", StringOperators.IS))
													.andCondition(CriteriaAPI.getCondition("STATUS", "status", 1+"", StringOperators.IS));
		
		if (fetchEvent) {
			fields.addAll(FieldFactory.getWorkflowEventFields());
			FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
			ruleBuilder.innerJoin(eventModule.getTableName())
						.on(module.getTableName()+".EVENT_ID = "+eventModule.getTableName()+".ID");
		}
		
		ruleBuilder.select(fields);
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, true, true);
		
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
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		return getWorkFlowsFromMapList(ruleBuilder.get(), false, true, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids, boolean fetchEvent) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields(); 
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(fields)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;
		if (fetchEvent) {
			fields.addAll(FieldFactory.getWorkflowEventFields());
			FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
			ruleBuilder.innerJoin(eventModule.getTableName())
						.on(module.getTableName()+".EVENT_ID = "+eventModule.getTableName()+".ID");
		}
		
		return getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, true, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids) throws Exception {
		return getWorkflowRules(ids, false);
	}
	
	public static Map<Long, WorkflowRuleContext> getWorkflowRulesAsMap (List<Long> ids, boolean fetchEvent, boolean fetchChildren, boolean fetchExtended) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, fetchChildren, fetchExtended);
		if (rules != null && !rules.isEmpty()) {
			return rules.stream()
						.collect(Collectors.toMap(WorkflowRuleContext::getId, Function.identity()))
						;
		}
		return null;
	}
	
	public static List<WorkflowEventContext> getWorkflowEvents(long orgId, long moduleId) throws Exception {
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowEventFields())
				.table("Workflow_Event")
				.andCustomWhere("Workflow_Event.ORGID = ? AND Workflow_Event.MODULEID = ?", orgId, moduleId);
		return getWorkFlowEventsFromMapList(ruleBuilder.get());
	}
	
	public static WorkflowEventContext getWorkflowEvent(long id) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowEventFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				;
		
		List<WorkflowEventContext> events = getWorkFlowEventsFromMapList(ruleBuilder.get());
		if (events != null && !events.isEmpty()) {
			return events.get(0);
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long moduleId) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleFields())
				.table("Workflow_Rule")
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Workflow_Event.MODULEID = ?", moduleId);
		return getWorkFlowsFromMapList(ruleBuilder.get(), false, true, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRulesOfType(RuleType type, boolean fetchEvent, boolean fetchChildren) throws Exception{
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleTypeField = fieldMap.get("ruleType");
		FacilioField latestVersionField = fieldMap.get("latestVersion");
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(eventModule.getTableName())
				.on(module.getTableName()+".EVENT_ID = "+ eventModule.getTableName() +".ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(ruleTypeField, String.valueOf(type.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(latestVersionField, String.valueOf(true), BooleanOperators.IS))
				;
		
		return getWorkFlowsFromMapList(builder.get(), fetchEvent, fetchChildren, true);
	}
	
	
	public static List<WorkflowRuleContext> getWorkflowRules(RuleType type, boolean fetchEvent, boolean fetchChildren, Criteria criteria, String searchQuery, JSONObject pagination) throws Exception{
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleTypeField = fieldMap.get("ruleType");
		FacilioField latestVersionField = fieldMap.get("latestVersion");
		FacilioField ruleNameField = FieldFactory.getAsMap(fields).get("name");
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(eventModule.getTableName())
				.on(module.getTableName()+".EVENT_ID = "+ eventModule.getTableName() +".ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
		
		return getWorkFlowsFromMapList(builder.get(), fetchEvent, fetchChildren, true);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivityAndRuleType(FacilioModule module, List<EventType> activityTypes,Criteria criteria, RuleType... ruleTypes) throws Exception {
		FacilioModule ruleModule = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(ruleModule.getTableName())
				.select(fields)
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ruleModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.orderBy("EXECUTION_ORDER");
		
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
			activityTypeWhere.append("? & Workflow_Event.ACTIVITY_TYPE = ?");
			values.add(type.getValue());
			values.add(type.getValue());
		}
		ruleBuilder.andCustomWhere(activityTypeWhere.toString(), values.toArray());
		List<Map<String, Object>> props = ruleBuilder.get();
		if(AccountUtil.getCurrentOrg().getId() == 88l && module.getName().equals("alarm")) {
			LOGGER.error("wokrlfow rule propssss --- "+props);
			LOGGER.error("wokrlfow rule querry --- "+ruleBuilder);
		}
		return getWorkFlowsFromMapList(props, true, true, true);
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
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), entry.getValue()));
					break;
				case SLA_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), entry.getValue()));
					break;
				case READING_ALARM_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingAlarmRuleModule(), FieldFactory.getReadingAlarmRuleFields(), entry.getValue()));
					break;
				case APPROVAL_RULE:
				case CHILD_APPROVAL_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields(), entry.getValue()));
					break;
				case STATE_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), entry.getValue()));
					break;
				case STATE_FLOW:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), entry.getValue()));
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
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	
	protected static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, boolean fetchEvent, boolean fetchChildren, boolean fetchExtended) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			List<Long> workflowIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> criteriaIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> fieldChangeRuleIds = fetchEvent ? new ArrayList<>() : null;
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
				
				if (fetchEvent) {
					int activity = (int) prop.get("activityType");
					if (EventType.FIELD_CHANGE.isPresent(activity)) {
						fieldChangeRuleIds.add((Long) prop.get("id"));
					}
				}
			}
			Map<RuleType, Map<Long, Map<String, Object>>> typeWiseExtendedProps = fetchExtended ? getTypeWiseExtendedProps(typeWiseIds) : null;
			Map<Long, WorkflowContext> workflowMap = fetchChildren && !workflowIds.isEmpty() ? WorkflowUtil.getWorkflowsAsMap(workflowIds, true) : null;
			Map<Long, Criteria> criteriaMap = fetchChildren && !criteriaIds.isEmpty() ? CriteriaAPI.getCriteriaAsMap(criteriaIds) : null;
			Map<Long, List<FieldChangeFieldContext>> ruleFieldsMap = fetchEvent && !fieldChangeRuleIds.isEmpty() ? getFieldChangeFields(fieldChangeRuleIds) : null;
			
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
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = ReadingRuleAPI.constructReadingRuleFromProps(prop, modBean, fetchChildren);
							break;
						case SLA_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = SLARuleAPI.constructSLARuleFromProps(prop, modBean);
							break;
						case APPROVAL_RULE:
						case CHILD_APPROVAL_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = ApprovalRulesAPI.constructApprovalRuleFromProps(prop, modBean);
							break;
						case READING_ALARM_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = constructReadingAlarmRuleFromProps(prop, modBean);
							break;
						case STATE_RULE:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = StateFlowRulesAPI.constructStateRuleFromProps(prop, modBean);
							break;
						case STATE_FLOW:
							prop.putAll(typeWiseExtendedProps.get(ruleType).get(prop.get("id")));
							rule = FieldUtil.getAsBeanFromMap(prop, StateFlowRuleContext.class);
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
				
				if(fetchEvent) {
					WorkflowEventContext event = FieldUtil.getAsBeanFromMap(prop, WorkflowEventContext.class);
					event.setId(rule.getEventId());
					event.setModule(modBean.getModule(event.getModuleId()));
					rule.setEvent(event);
					
					if (EventType.FIELD_CHANGE.isPresent(event.getActivityType())) {
						rule.setFields(ruleFieldsMap.get(rule.getId()));
					}
					else if (EventType.SCHEDULED.isPresent(event.getActivityType())) {
						rule.setDateField(modBean.getField(rule.getDateFieldId()));
					}
				}
				workflows.add(rule);
			}
			return workflows;
		}
		return null;
	}
	
	protected static ReadingAlarmRuleContext constructReadingAlarmRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		ReadingAlarmRuleContext readingRule = FieldUtil.getAsBeanFromMap(prop, ReadingAlarmRuleContext.class);
		return readingRule;
	}
	
	private static List<WorkflowEventContext> getWorkFlowEventsFromMapList(List<Map<String, Object>> props) throws Exception {
		if(props != null && props.size() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<WorkflowEventContext> workflowEvents = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				WorkflowEventContext workflowEvent = FieldUtil.getAsBeanFromMap(prop, WorkflowEventContext.class);
				workflowEvent.setModule(modBean.getModule(workflowEvent.getModuleId()));
				workflowEvents.add(workflowEvent);
			}
			return workflowEvents;
		}
		return null;
	}
	
	public static void deleteWorkFlowRules(List<Long> workflowIds) throws Exception {
		if (workflowIds != null && !workflowIds.isEmpty()) {
			List<WorkflowRuleContext> rules = getWorkflowRules(workflowIds, true);
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
					if (EventType.SCHEDULED.isPresent(rule.getEvent().getActivityType())) {
						ScheduledRuleAPI.deleteScheduledRuleJob(rule);
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
							ApprovalRulesAPI.deleteStateTransitionChildren((StateflowTransitionContext) rule);
							StateFlowRulesAPI.deleteStateFlowTransitionChildren((StateflowTransitionContext) rule);
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
		if (rule.getEvent().getActivityTypeEnum() == EventType.FIELD_CHANGE) {
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
	
	private static boolean executeRuleAndChildren (WorkflowRuleContext workflowRule, FacilioModule module, Object record, List<UpdateChangeSet> changeSet, Iterator itr, Map<String, Object> recordPlaceHolders, FacilioContext context,boolean propagateError, FacilioField parentRuleField, FacilioField onSuccessField, List<EventType> eventTypes, RuleType... ruleTypes) throws Exception {
		try {
			long workflowStartTime = System.currentTimeMillis();
			workflowRule.setTerminateExecution(false);
			boolean result = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, module.getName(), record, changeSet, recordPlaceHolders, context);
			
			if (AccountUtil.getCurrentOrg().getId() == 186 && workflowRule.getId() == 6448) {
				LOGGER.info("Result of rule : "+workflowRule.getId()+" for record : "+record+" is "+result);
			}
			if (AccountUtil.getCurrentOrg().getId() == 134l && workflowRule instanceof ReadingRuleContext && ((ReadingRuleContext)workflowRule).getRuleGroupId() == 7186l) {
				LOGGER.error("Result of rule : "+workflowRule.getId()+" for record : "+record+" is "+result);
			}
			if (AccountUtil.getCurrentOrg().getId() == 88l && workflowRule.getId() == 7762l) {
				LOGGER.error("Result of rule : "+workflowRule.getId()+" for record : "+record+" is "+result);
			}
			
			boolean stopFurtherExecution = workflowRule.isTerminateExecution();
			
			if (result) {
				if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
					itr.remove();
					stopFurtherExecution = true;
				}
			}
			
			LOGGER.debug("Time taken to execute rule : "+workflowRule.getName()+" with id : "+workflowRule.getId()+" for module : "+module.getName()+" is "+(System.currentTimeMillis() - workflowStartTime));
			if(!stopFurtherExecution) {
				Criteria currentCriteria = new Criteria();
				currentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRuleField, String.valueOf(workflowRule.getId()), NumberOperators.EQUALS));
				currentCriteria.addAndCondition(CriteriaAPI.getCondition(onSuccessField, String.valueOf(result), BooleanOperators.IS));
				
				List<WorkflowRuleContext> currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(workflowRule.getEvent().getModule(), eventTypes, currentCriteria, ruleTypes);
				executeWorkflowsAndGetChildRuleCriteria(currentWorkflows, module, record, changeSet, itr, recordPlaceHolders, context, propagateError, eventTypes, ruleTypes);
				
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
			CommonCommandUtil.emailException("RULE EXECUTION FAILED - "+AccountUtil.getCurrentOrg().getId(),builder.toString(), e);
			LOGGER.error(builder.toString(), e);
			
			if (propagateError) {
				throw e;
			}
		}
		return false;
	}
	
	public static void executeWorkflowsAndGetChildRuleCriteria(List<WorkflowRuleContext> workflowRules, FacilioModule module, Object record, List<UpdateChangeSet> changeSet, Iterator itr, Map<String, Object> recordPlaceHolders, FacilioContext context,boolean propagateError, List<EventType> eventTypes, RuleType... ruleTypes) throws Exception {
		if(workflowRules != null && !workflowRules.isEmpty()) {
			Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
			FacilioField parentRule = fields.get("parentRuleId");
			FacilioField onSuccess = fields.get("onSuccess");
			
			for(WorkflowRuleContext workflowRule : workflowRules) {
				boolean stopFurtherExecution = executeRuleAndChildren(workflowRule, module, record, changeSet, itr, recordPlaceHolders, context, propagateError, parentRule, onSuccess, eventTypes, ruleTypes);
				if(stopFurtherExecution) {
					break;
				}
			}
		}
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
				
				List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, false, true, true);
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

	public static void executeScheduledRule (WorkflowRuleContext rule, long executionTime, FacilioContext context) throws Exception {
		WorkflowEventContext event = rule.getEvent();
		FacilioModule module = event.getModule();
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);
		recordPlaceHolders.put("executionTime", executionTime);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, executionTime);
		WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(rule), module, null, null, null, recordPlaceHolders, (FacilioContext)context,true, Collections.singletonList(rule.getEvent().getActivityTypeEnum()));
	}
	
}
