package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class WorkflowAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		updateWorkflowRuleChildIds(rule,rule.getOrgId());
		Map<String, Object> ruleProps = FieldUtil.getAsProperties(rule);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getWorkflowRuleModule().getTableName())
													.fields(FieldFactory.getWorkflowRuleFields())
													.addRecord(ruleProps);
		insertBuilder.save();
		
		if(rule.getRuleTypeEnum() == RuleType.READING_RULE) {
			insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getReadingRuleModule().getTableName())
														.fields(FieldFactory.getReadingRuleFields())
														.addRecord(ruleProps);
			insertBuilder.save();
		}
		
		return (long) ruleProps.get("id");
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
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), AccountUtil.getCurrentOrg().getId(), true);
		
		if(rules != null && !rules.isEmpty()) {
			return rules.get(0);
		}
		return null;
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId) throws Exception {
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCustomWhere("Workflow_Rule.ORGID = ?", orgId);
		return getWorkFlowsFromMapList(ruleBuilder.get(), orgId, false);
	}
	
	public static List<WorkflowEventContext> getWorkflowEvents(long orgId, long moduleId) throws Exception {
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowEventFields())
				.table("Workflow_Event")
				.andCustomWhere("Workflow_Event.ORGID = ? AND Workflow_Event.MODULEID = ?", orgId, moduleId);
		return getWorkFlowEventsFromMapList(ruleBuilder.get(), orgId);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId, long moduleId) throws Exception {
		try {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWorkflowRuleFields())
					.table("Workflow_Rule")
					.innerJoin("Workflow_Event")
					.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
					.andCustomWhere("Workflow_Rule.ORGID = ? AND Workflow_Event.MODULEID = ?", orgId, moduleId);
			return getWorkFlowsFromMapList(ruleBuilder.get(), orgId, false);
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	public static List<WorkflowRuleContext> getWorkflowRulesOfType(RuleType type) throws Exception{
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleFields())
				.table("Workflow_Rule")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getWorkflowRuleModule()))
				.andCustomWhere("RULE_TYPE = ?", type.getIntVal());
		return getWorkFlowsFromMapList(builder.get(), AccountUtil.getCurrentOrg().getOrgId(), false);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivity(long orgId, long moduleId, int activityType) throws Exception {
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCustomWhere("Workflow_Rule.ORGID = ? AND Workflow_Event.MODULEID = ? AND ? & Workflow_Event.ACTIVITY_TYPE = ? AND Workflow_Rule.STATUS = true", orgId, moduleId, activityType, activityType)
				.orderBy("EXECUTION_ORDER");
		return getWorkFlowsFromMapList(ruleBuilder.get(), orgId, false);
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
	
	private static Map<Long, Map<String, Object>> getReadingRuleProps(List<Long> ids) throws Exception {
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		
		FacilioModule module = ModuleFactory.getReadingRuleModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(FieldFactory.getReadingRuleFields())
																.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<Map<String, Object>> readingRuleProps = selectRecordBuilder.get();
		
		if (readingRuleProps != null && !readingRuleProps.isEmpty()) {
			for(Map<String, Object> prop : readingRuleProps) {
				propsMap.put((Long) prop.get("id"), prop);
			}
		}
		return propsMap;
	}
	
	private static List<WorkflowRuleContext> getWorkFlowsFromMapList(List<Map<String, Object>> props, long orgId, boolean isEvent) throws Exception {
		if(props != null && props.size() > 0) {
			List<WorkflowRuleContext> workflows = new ArrayList<>();
			
			List<Long> readingRuleIds = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				Integer ruleType = (Integer) prop.get("ruleType");
				if(ruleType != null && RuleType.READING_RULE == RuleType.getRuleType(ruleType)) {
					readingRuleIds.add((Long) prop.get("id"));
				}
			}
			Map<Long, Map<String, Object>> readingRuleProps = getReadingRuleProps(readingRuleIds);
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext workflow = null;
				Integer ruleType = (Integer) prop.get("ruleType");
				if(ruleType != null && RuleType.READING_RULE == RuleType.getRuleType(ruleType)) {
					prop.putAll(readingRuleProps.get((Long) prop.get("id")));
					workflow = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
				}
				else {
					workflow = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
				}
				long criteriaId = workflow.getCriteriaId();
				workflow.setCriteria(CriteriaAPI.getCriteria(orgId, criteriaId));
				
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
