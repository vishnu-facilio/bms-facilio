package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Context;

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
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.view.SLARuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowRuleAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws Exception {
		rule.setOrgId(AccountUtil.getCurrentOrg().getId());
		rule.setStatus(true);
		updateWorkflowRuleChildIds(rule);
		
		if (rule.getEventId() == -1) {
			throw new IllegalArgumentException("Event ID cannot be null during addition for Workflow");
		}
		
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
			default:
				break;
		}
		
		return rule.getId();
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
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent);
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
		List<WorkflowRuleContext> rules = getWorkFlowsFromMapList(ruleBuilder.get(), fetchEvent);
		
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false);
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;
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
		
		return getWorkFlowsFromMapList(builder.get(), true);
	}
	
	public static List<WorkflowRuleContext> getActiveWorkflowRulesFromActivityAndRuleType(long moduleId, List<ActivityType> activityTypes,Criteria criteria, RuleType... ruleTypes) throws Exception {
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
		return getWorkFlowsFromMapList(ruleBuilder.get(), false);
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
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> prop : props) {
				WorkflowRuleContext workflow = null;
				
				RuleType ruleType = RuleType.valueOf((int) prop.get("ruleType"));
				switch(ruleType) {
					
					case PM_READING_RULE:
					case READING_RULE:
					case VALIDATION_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						workflow = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
						ReadingRuleContext readingRule = ((ReadingRuleContext)workflow);
						readingRule.setReadingField(modBean.getField(((ReadingRuleContext)workflow).getReadingFieldId()));
						setMatchedResources(readingRule);
						break;
					case SLA_RULE:
						prop.putAll(typeWiseExtendedProps.get(ruleType).get((Long) prop.get("id")));
						workflow = FieldUtil.getAsBeanFromMap(prop, SLARuleContext.class);
						((SLARuleContext)workflow).setResource(ResourceAPI.getResource(((SLARuleContext)workflow).getResourceId()));
						break;
					default:
						workflow = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleContext.class);
						break;
				}
				
				long criteriaId = workflow.getCriteriaId();
				if (criteriaId != -1) {
					workflow.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId));
				}
				
				long workflowId = workflow.getWorkflowId();
				if (workflowId != -1) {
					workflow.setWorkflow(WorkflowUtil.getWorkflowContext(workflowId, true));
				}
				
				if(isEvent) {
					WorkflowEventContext event = FieldUtil.getAsBeanFromMap(prop, WorkflowEventContext.class);
					event.setId(workflow.getEventId());
					event.setModule(modBean.getModule(event.getModuleId()));
					workflow.setEvent(event);
				}
				workflows.add(workflow);
			}
			return workflows;
		}
		return null;
	}
	
	private static void setMatchedResources (ReadingRuleContext readingRule) throws Exception {
		if (readingRule.getAssetCategoryId() == -1) {
			long resourceId = readingRule.getResourceId();
			readingRule.setMatchedResources(Collections.singletonMap(resourceId, ResourceAPI.getResource(resourceId)));
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
	
	public static boolean evaluateWorkflow(WorkflowRuleContext workflowRule, String moduleName, Object record, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		Map<String, Object> rulePlaceHolders = workflowRule.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
		boolean miscFlag = false, criteriaFlag = false, workflowFlag = false;
		miscFlag = workflowRule.evaluateMisc(moduleName, record, rulePlaceHolders, (FacilioContext) context);
		if (miscFlag) {
			criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, rulePlaceHolders, (FacilioContext) context);
			if (criteriaFlag) {
				workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, rulePlaceHolders, (FacilioContext) context);
			}
		}
		
		boolean result = criteriaFlag && workflowFlag && miscFlag;
		if(result) {
			executeWorkflowActions(workflowRule, record, context, rulePlaceHolders);
		}
		return result;
	}
	
	private static void executeWorkflowActions(WorkflowRuleContext rule, Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = rule.getId();
		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), ruleId);
		if(actions != null) {
			for(ActionContext action : actions)
			{
				action.executeAction(placeHolders, context, rule, record);
			}
		}
	}
}
