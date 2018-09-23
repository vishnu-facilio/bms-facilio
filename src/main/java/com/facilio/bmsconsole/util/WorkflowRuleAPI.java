package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
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
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.view.SLARuleContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowRuleAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		rule.setOrgId(AccountUtil.getCurrentOrg().getId());
		rule.setStatus(true);
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
				addExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), ruleProps);
				addReadingRuleInclusionsExlusions((ReadingRuleContext) rule);
				break;
			case SLA_RULE:
				addExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), ruleProps);
				break;
			case SCHEDULED_RULE:
				validateScheduledRule((ScheduledRuleContext) rule);
				addExtendedProps(ModuleFactory.getScheduledRuleModule(), FieldFactory.getScheduledRuleFields(), ruleProps);
				break;
			default:
				break;
		}
		
		if (rule.getEvent() != null && ActivityType.FIELD_CHANGE.isPresent(rule.getEvent().getActivityType())) {
			addFieldChangeFields(rule);
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
	
	private static void validateWorkflowRule (WorkflowRuleContext rule) {
		if (rule.getEventId() == -1) {
			throw new IllegalArgumentException("Event ID cannot be null during addition for Workflow");
		}
		
		if (rule.getRuleTypeEnum() == null) {
			throw new IllegalArgumentException("Rule Type cannot be null during addition for Workflow");
		}
	}
	
	private static void validateScheduledRule(ScheduledRuleContext rule) {
		if (rule.getDateFieldId() == -1) {
			throw new IllegalArgumentException("Date Field Id cannot be null for Scheduled Rule");
		}
		
		if (rule.getScheduleTypeEnum() == null) {
			throw new IllegalArgumentException("Schedule Type cannot be null for Scheduled Rule");
		}
		
		switch (rule.getScheduleTypeEnum()) {
			case BEFORE:
			case AFTER:
				if (rule.getInterval() == -1) {
					throw new IllegalArgumentException("Interval cannot be null for Scheduled Rule with type BEFORE/ AFTER");
				}
				break;
			case ON:
				break;
		}
	}
	
	private static void addExtendedProps(FacilioModule module, List<FacilioField> fields, Map<String, Object> ruleProps) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(ruleProps);
		insertBuilder.save();
	}
	
	private static void addReadingRuleInclusionsExlusions(ReadingRuleContext rule) throws SQLException, RuntimeException {
		if (rule.getAssetCategoryId() != -1) {
			List<Map<String, Object>> inclusionExclusionList = new ArrayList<>();
			getInclusionExclusionList(rule.getId(), rule.getIncludedResources(), true, inclusionExclusionList);
			getInclusionExclusionList(rule.getId(), rule.getExcludedResources(), false, inclusionExclusionList);
			
			if (!inclusionExclusionList.isEmpty()) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getReadingRuleInclusionsExclusionsModule().getTableName())
															.fields(FieldFactory.getReadingRuleInclusionsExclusionsFields())
															.addRecords(inclusionExclusionList);
				insertBuilder.save();
			}
		}
	}
	
	private static void getInclusionExclusionList(long ruleId, List<Long> resources, boolean isInclude, List<Map<String, Object>> inclusionExclusionList) {
		if (resources != null && !resources.isEmpty()) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			for (Long resourceId : resources) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("orgId", orgId);
				prop.put("ruleId", ruleId);
				prop.put("resourceId", resourceId);
				prop.put("isInclude", isInclude);
				
				inclusionExclusionList.add(prop);
			}
		}
	}
	
	private static final void updateWorkflowRuleChildIds(WorkflowRuleContext workflowRuleContext) throws Exception {
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
	
	public static final WorkflowEventContext getWorkFlowEvent(ActivityType type, long moduleId) throws Exception {
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
		WorkflowRuleContext oldRule = getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateWorkflowRule(rule);
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateWorkflowRule(WorkflowRuleContext rule) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table("Workflow_Rule")
													.fields(FieldFactory.getWorkflowRuleFields())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(rule.getId(), module));
		return updateBuilder.update(ruleProps);
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId) throws Exception {
		return getWorkflowRule(ruleId, false);
	}	
	public static WorkflowRuleContext getWorkflowRule (long ruleId, boolean fetchEvent) throws Exception {
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
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, true);
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
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent, true);
		
		if(fetchAction) {
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;
		return getWorkFlowsFromMapList(ruleBuilder.get(), false, true);
	}
	
	public static List<WorkflowEventContext> getWorkflowEvents(long orgId, long moduleId) throws Exception {
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowEventFields())
				.table("Workflow_Event")
				.andCustomWhere("Workflow_Event.ORGID = ? AND Workflow_Event.MODULEID = ?", orgId, moduleId);
		return getWorkFlowEventsFromMapList(ruleBuilder.get(), orgId);
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false, true);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRulesOfType(RuleType type, boolean fetchEvent, boolean fetchChildren) throws Exception{
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(eventModule.getTableName())
				.on(module.getTableName()+".EVENT_ID = "+ eventModule.getTableName() +".ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("RULE_TYPE = ?", type.getIntVal());
		
		return getWorkFlowsFromMapList(builder.get(), fetchEvent, fetchChildren);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivityAndRuleType(long moduleId, List<ActivityType> activityTypes,Criteria criteria, RuleType... ruleTypes) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Workflow_Event.MODULEID = ? AND Workflow_Rule.STATUS = true", moduleId)
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
		for (ActivityType type : activityTypes) {
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
	}
	
	public static int updateLastValueInReadingRule(long ruleId, long value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setLastValue(value);
		
		return updateReadingRule(rule, ruleId);
	}
	
	private static void deleteChildIdsForWorkflow(WorkflowRuleContext oldRule, WorkflowRuleContext newRule) throws Exception {
		if(newRule.getCriteria() != null && oldRule.getCriteriaId() != -1) {
			CriteriaAPI.deleteCriteria(oldRule.getCriteriaId());
		}
		if(newRule.getWorkflow() != null && oldRule.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(oldRule.getWorkflowId());
		}
	}
	
	public static SLARuleContext updateSLARuleWithChildren(SLARuleContext rule) throws Exception {
		SLARuleContext oldRule = (SLARuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateSLARule(rule, rule.getId());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateSLARule(SLARuleContext slaRule, long ruleId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getSLARuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule slaRuleModule = ModuleFactory.getSLARuleModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(slaRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(slaRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule))
														.andCondition(CriteriaAPI.getIdCondition(ruleId, slaRuleModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(slaRule));
	}
	
	public static ReadingRuleContext updateReadingRuleWithChildren(ReadingRuleContext rule) throws Exception {
		ReadingRuleContext oldRule = (ReadingRuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateReadingRule(rule, rule.getId());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateReadingRule(ReadingRuleContext readingRule, long ruleId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getReadingRuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(readingRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(readingRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule))
														.andCondition(CriteriaAPI.getIdCondition(ruleId, readingRuleModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(readingRule));
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
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), entry.getValue()));
					break;
				case SLA_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSLARuleModule(), FieldFactory.getSLARuleFields(), entry.getValue()));
					break;
				case SCHEDULED_RULE:
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getScheduledRuleModule(), FieldFactory.getScheduledRuleFields(), entry.getValue()));
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
	
	private static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, boolean fetchEvent, boolean fetchChildren) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			List<Long> workflowIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> criteriaIds = fetchChildren ? new ArrayList<>() : null;
			List<Long> fieldChangeRuleIds = fetchEvent ? new ArrayList<>() : null;
			
			Map<RuleType, List<Long>> typeWiseIds = new HashMap<>();
			for(Map<String, Object> prop : props) {
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				List<Long> idList = typeWiseIds.get(ruleType);
				if(idList == null) {
					idList = new ArrayList<>();
					typeWiseIds.put(ruleType, idList);
				}
				idList.add((Long) prop.get("id"));
				
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
					if (ActivityType.FIELD_CHANGE.isPresent(activity)) {
						fieldChangeRuleIds.add((Long) prop.get("id"));
					}
				}
			}
			Map<RuleType, Map<Long, Map<String, Object>>> typeWiseExtendedProps = getTypeWiseExtendedProps(typeWiseIds);
			Map<Long, WorkflowContext> workflowMap = null;
			if (fetchChildren && !workflowIds.isEmpty()) {
				workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds, true);
			}
			
			Map<Long, Criteria> criteriaMap = null;
			if (fetchChildren && !criteriaIds.isEmpty()) {
				criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
			}
			
			Map<Long, List<FieldChangeFieldContext>> ruleFieldsMap = null;
			if (fetchEvent && !fieldChangeRuleIds.isEmpty()) {
				ruleFieldsMap = getFieldChangeFields(fieldChangeRuleIds);
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext rule = null;
				
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				switch(ruleType) {
					
					case PM_READING_RULE:
					case READING_RULE:
					case VALIDATION_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						rule = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
						ReadingRuleContext readingRule = ((ReadingRuleContext)rule);
						readingRule.setReadingField(modBean.getField(((ReadingRuleContext)rule).getReadingFieldId()));
						setMatchedResources(readingRule);
						break;
					case SLA_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						rule = FieldUtil.getAsBeanFromMap(prop, SLARuleContext.class);
						((SLARuleContext)rule).setResource(ResourceAPI.getResource(((SLARuleContext)rule).getResourceId()));
						break;
					case SCHEDULED_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						rule = FieldUtil.getAsBeanFromMap(prop, ScheduledRuleContext.class);
						((ScheduledRuleContext)rule).setDateField(modBean.getField(((ScheduledRuleContext)rule).getDateFieldId()));
						break;
					default:
						rule = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
						break;
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
					
					if (ActivityType.FIELD_CHANGE.isPresent(event.getActivityType())) {
						rule.setFields(ruleFieldsMap.get(rule.getId()));
					}
				}
				workflows.add(rule);
			}
			return workflows;
		}
		return null;
	}
	
	private static void setMatchedResources (ReadingRuleContext readingRule) throws Exception {
		if (readingRule.getAssetCategoryId() == -1) {
			long resourceId = readingRule.getResourceId();
			readingRule.setMatchedResources(Collections.singletonMap(resourceId, ResourceAPI.getExtendedResource(resourceId)));
		}
		else {
			List<AssetContext> categoryAssets = AssetsAPI.getAssetListOfCategory(readingRule.getAssetCategoryId());
			if (categoryAssets != null && !categoryAssets.isEmpty()) {
				fetchInclusionsExclusions(readingRule);
				
				Map<Long, ResourceContext> matchedResources = new HashMap<>();
				for (AssetContext asset : categoryAssets) {
					if ( (readingRule.getIncludedResources() == null 
							|| readingRule.getIncludedResources().isEmpty() 
							|| readingRule.getIncludedResources().contains(asset.getId()))
							&& (readingRule.getExcludedResources() == null 
								|| readingRule.getExcludedResources().isEmpty()
								|| !readingRule.getExcludedResources().contains(asset.getId()))
							) {
						matchedResources.put(asset.getId(), asset);
					}
				}
				readingRule.setMatchedResources(matchedResources);
			}
		}
	}
	
	private static void fetchInclusionsExclusions (ReadingRuleContext readingRule) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleInclusionsExclusionsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleInclusionsExclusionsFields();
		FacilioField ruleId = FieldFactory.getAsMap(fields).get("ruleId");
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(fields)
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(ruleId, String.valueOf(readingRule.getId()), PickListOperators.IS));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> includedResources = new ArrayList<>();
			List<Long> excludedResources = new ArrayList<>();
			
			for (Map<String, Object> prop : props) {
				boolean isInclude = (boolean) prop.get("isInclude");
				if (isInclude) {
					includedResources.add((Long) prop.get("resourceId"));
				}
				else {
					excludedResources.add((Long) prop.get("resourceId"));
				}
			}
			
			if (!includedResources.isEmpty()) {
				readingRule.setIncludedResources(includedResources);
			}
			if (!excludedResources.isEmpty()) {
				readingRule.setExcludedResources(excludedResources);
			}
		}
	}
	
	public static List<ReadingRuleContext> getReadingRules() throws Exception {
		return getReadingRules(null);
	}
	
	public static List<ReadingRuleContext> getReadingRules(Criteria criteria) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getReadingRuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(readingRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(readingRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule));
		
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ReadingRuleContext> readingRules = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ReadingRuleContext readingRule = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
				if (readingRule.getCriteriaId() > 0) {
					readingRule.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), readingRule.getCriteriaId()));
				}
				readingRules.add(readingRule);
			}
			return readingRules;
		}
		return null;
	}
	
	private static List<WorkflowEventContext> getWorkFlowEventsFromMapList(List<Map<String, Object>> props, long orgId) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowEventContext> workflowEvents = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				WorkflowEventContext workflowEvent = FieldUtil.getAsBeanFromMap(prop, WorkflowEventContext.class);
				workflowEvents.add(workflowEvent);
			}
			return workflowEvents;
		}
		return null;
	}
	
	public static void deleteWorkFlowRules(List<Long> workflowIds) throws Exception{
		if (workflowIds != null && !workflowIds.isEmpty()) {
			List<WorkflowRuleContext> rules = getWorkflowRules(workflowIds);
			
			if (rules != null && !rules.isEmpty()) {
				ActionAPI.deleteAllActionsFromWorkflowRules(workflowIds);
				
				FacilioModule module = ModuleFactory.getWorkflowRuleModule();
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getIdCondition(workflowIds, module));
				deleteBuilder.delete();
				
				for (WorkflowRuleContext rule : rules) {
					deleteChildIdsForWorkflow(rule, rule);
				}
			}
		}
	}
	
	public static void deleteWorkflowRule(long workflowId) throws Exception {
		if (workflowId != -1) {
			deleteWorkFlowRules(Collections.singletonList(workflowId));
		}
	}
	
	private static boolean evalFieldChange(WorkflowRuleContext rule, List<UpdateChangeSet> changeSetList) {
		if (rule.getEvent().getActivityTypeEnum() == ActivityType.FIELD_CHANGE) {
			if (changeSetList != null && !changeSetList.isEmpty()) {
				for (FieldChangeFieldContext field : rule.getFields()) {
					for (UpdateChangeSet changeSet : changeSetList) {
						if (field.getFieldId() == changeSet.getFieldId() 
								&& (field.getOldValue() == null || field.getOldValue().toString().equals(changeSet.getOldValue().toString()))
								&& (field.getNewValue() == null || field.getNewValue().toString().equals(changeSet.getNewValue().toString()))
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
	
	public static boolean evaluateWorkflow(WorkflowRuleContext workflowRule, String moduleName, Object record, List<UpdateChangeSet> changeSet, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		Map<String, Object> rulePlaceHolders = workflowRule.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
		boolean fieldChangeFlag = false, miscFlag = false, criteriaFlag = false, workflowFlag = false;
		
		fieldChangeFlag = evalFieldChange(workflowRule, changeSet);
		if (fieldChangeFlag) {
			miscFlag = workflowRule.evaluateMisc(moduleName, record, rulePlaceHolders, (FacilioContext) context);
			if (miscFlag) {
				criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, rulePlaceHolders, (FacilioContext) context);
				if (criteriaFlag) {
					workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, rulePlaceHolders, (FacilioContext) context);
				}
			}
		}
		
		boolean result = fieldChangeFlag && miscFlag && criteriaFlag && workflowFlag;
		if(result) {
			workflowRule.executeWorkflowActions(record, context, rulePlaceHolders);
		}
		return result;
	}
	
}
