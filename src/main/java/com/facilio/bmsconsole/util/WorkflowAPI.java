package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class WorkflowAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		updateWorkflowRuleChildIds(rule,rule.getOrgId());
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getWorkflowRuleModule().getTableName())
													.fields(FieldFactory.getWorkflowRuleFields())
													.addRecord(ruleProps);
		insertBuilder.save();
		
		switch(rule.getRuleTypeEnum()) {
			case READING_RULE:
			case PM_READING_RULE:
				addExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), ruleProps);
				break;
			default:
				break;
		}
		
		return (long) ruleProps.get("id");
	}
	
	private static void addExtendedProps(FacilioModule module, List<FacilioField> fields, Map<String, Object> ruleProps) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(ruleProps);
		insertBuilder.save();
	}
	
	public static final void updateWorkflowRuleChildIds(WorkflowRuleContext workflowRuleContext, long orgId) throws Exception {
		if(workflowRuleContext.getCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(workflowRuleContext.getCriteria(),orgId);
			workflowRuleContext.setCriteriaId(criteriaId);
		}
	}
	public static int updateWorkflowRule(long orgId, WorkflowRuleContext rule, long id) throws Exception {
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table("Workflow_Rule")
													.fields(FieldFactory.getWorkflowRuleFields())
													.andCustomWhere("ORGID = ? AND ID = ?", orgId, id);
		return updateBuilder.update(ruleProps);
	}
	
	public static WorkflowRuleContext getWorkflowRule (long ruleId) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowEventFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.innerJoin(eventModule.getTableName())
													.on(module.getTableName()+".EVENT_ID = "+eventModule.getTableName()+".ID")
													.select(fields)
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(ruleId, module));
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), true);
		
		if(rules != null && !rules.isEmpty()) {
			return rules.get(0);
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules() throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		return getWorkFlowsFromMapList(ruleBuilder.get(), false);
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRulesOfType(RuleType type) throws Exception{
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleFields())
				.table("Workflow_Rule")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getWorkflowRuleModule()))
				.andCustomWhere("RULE_TYPE = ?", type.getIntVal());
		return getWorkFlowsFromMapList(builder.get(), false);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivityAndRuleType(long moduleId, List<ActivityType> activityTypes, RuleType... ruleTypes) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getWorkflowRuleFields())
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Workflow_Event.MODULEID = ? AND Workflow_Rule.STATUS = true", moduleId)
				.orderBy("EXECUTION_ORDER");
		
		if(ruleTypes != null && ruleTypes.length > 0) {
			List<Integer> ids = new ArrayList<>();
			for(RuleType type : ruleTypes) {
				ids.add(type.getIntVal());
			}
			Condition ruleTypeCondition = new Condition();
			ruleTypeCondition.setColumnName("RULE_TYPE");
			ruleTypeCondition.setOperator(NumberOperators.EQUALS);
			ruleTypeCondition.setValue(StringUtils.join(ids, ","));
			ruleBuilder.andCondition(ruleTypeCondition);
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false);
	}
	
	public static int updateLastValueInReadingRule(long ruleId, long value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setLastValue(value);
		
		return updateReadingRule(rule, ruleId);
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
					typeWiseProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields(), entry.getValue()));
					break;
				default:
					break;
			}
		}
		return typeWiseProps;
	}
	
	private static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, boolean isEvent) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			
			Map<RuleType, List<Long>> typeWiseIds = new HashMap<>();
			for(Map<String, Object> prop : props) {
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				List<Long> idList = typeWiseIds.get(ruleType);
				if(idList == null) {
					idList = new ArrayList<>();
					typeWiseIds.put(ruleType, idList);
				}
				idList.add((Long) prop.get("id"));
			}
			Map<RuleType, Map<Long, Map<String, Object>>> typeWiseExtendedProps = getTypeWiseExtendedProps(typeWiseIds);
			
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext workflow = null;
				
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				switch(ruleType) {
					
					case PM_READING_RULE:
					case READING_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						workflow = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
						break;
					default:
						workflow = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
						break;
				}
				
				long criteriaId = workflow.getCriteriaId();
				workflow.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId));
				if(isEvent) {
					WorkflowEventContext event = FieldUtil.getAsBeanFromMap(prop, WorkflowEventContext.class);
					event.setId(workflow.getEventId());
					workflow.setEvent(event);
				}
				workflows.add(workflow);
			}
			return workflows;
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
}
