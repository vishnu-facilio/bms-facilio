package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;

public class ReadingRuleAPI extends WorkflowRuleAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(ReadingRuleAPI.class.getName());
	
	protected static void addReadingRuleInclusionsExlusions(ReadingRuleContext rule) throws SQLException, RuntimeException {
		if (rule.getAssetCategoryId() != -1) {
			List<Map<String, Object>> inclusionExclusionList = new ArrayList<>();
			getInclusionExclusionList(rule.getRuleGroupId(), rule.getIncludedResources(), true, inclusionExclusionList);
			getInclusionExclusionList(rule.getRuleGroupId(), rule.getExcludedResources(), false, inclusionExclusionList);
			
			if (!inclusionExclusionList.isEmpty()) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getReadingRuleInclusionsExclusionsModule().getTableName())
															.fields(FieldFactory.getReadingRuleInclusionsExclusionsFields())
															.addRecords(inclusionExclusionList);
				insertBuilder.save();
			}
		}
	}
	
	private static void getInclusionExclusionList(long ruleGroupId, List<Long> resources, boolean isInclude, List<Map<String, Object>> inclusionExclusionList) {
		if (resources != null && !resources.isEmpty()) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			for (Long resourceId : resources) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("orgId", orgId);
				prop.put("ruleGroupId", ruleGroupId);
				prop.put("resourceId", resourceId);
				prop.put("isInclude", isInclude);
				
				inclusionExclusionList.add(prop);
			}
		}
	}
	
	public static int updateLastValueInReadingRule(long ruleId, long value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setLastValue(value);
		rule.setId(ruleId);
		
		return updateExtendedRule(rule, ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields());
	}
	
	public static ReadingRuleContext updateReadingRuleWithChildren(ReadingRuleContext rule) throws Exception {
		ReadingRuleContext oldRule = (ReadingRuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateExtendedRule(rule, ModuleFactory.getReadingRuleModule(), FieldFactory.getReadingRuleFields());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static void setMatchedResources (ReadingRuleContext readingRule) throws Exception {
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
		FacilioField ruleId = FieldFactory.getAsMap(fields).get("ruleGroupId");
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(fields)
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(ruleId, String.valueOf(readingRule.getRuleGroupId()), PickListOperators.IS));
		
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
	
	protected static ReadingRuleContext constructReadingRuleFromProps(Map<String, Object> prop, ModuleBean modBean, boolean fetchChildren) throws Exception {
		ReadingRuleContext readingRule = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
		readingRule.setReadingField(modBean.getField(readingRule.getReadingFieldId()));
		setMatchedResources(readingRule);
		
		if (fetchChildren) {
			fetchAlarmMeta(readingRule);
		}
		
		return readingRule;
	}
	
	private static void fetchAlarmMeta (ReadingRuleContext rule) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleAlarmMetaFields();
		FacilioField ruleGroupField = FieldFactory.getAsMap(fields).get("ruleGroupId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(ruleGroupField, String.valueOf(rule.getRuleGroupId()), PickListOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				ReadingRuleAlarmMeta alarmMeta = FieldUtil.getAsBeanFromMap(prop, ReadingRuleAlarmMeta.class);
				alarmMetaMap.put(alarmMeta.getResourceId(), alarmMeta);
			}
			rule.setAlarmMetaMap(alarmMetaMap);
		}
	}
	
	public static Map<Long, ReadingRuleAlarmMeta> fetchAlarmMeta (long resourceId,long fieldId) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleAlarmMetaFields();
		
		FacilioField resourceIdField = FieldFactory.getAsMap(fields).get("resourceId");
		FacilioField fieldIdField = FieldFactory.getAsMap(fields).get("readingFieldId");
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(fieldId), NumberOperators.EQUALS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				ReadingRuleAlarmMeta alarmMeta = FieldUtil.getAsBeanFromMap(prop, ReadingRuleAlarmMeta.class);
				alarmMetaMap.put(alarmMeta.getAlarmId(),alarmMeta);
			}
		}
		return alarmMetaMap;
	}
	
	public static ReadingRuleAlarmMeta constructAlarmMeta (long alarmId, long resourceId, ReadingRuleContext rule) {
		ReadingRuleAlarmMeta meta = new ReadingRuleAlarmMeta();
		meta.setOrgId(AccountUtil.getCurrentOrg().getId());
		meta.setAlarmId(alarmId);
		meta.setRuleGroupId(rule.getRuleGroupId());
		meta.setResourceId(resourceId);
		meta.setReadingFieldId(rule.getReadingFieldId());
		meta.setClear(false);
		
		return meta;
	}
	
	public static ReadingRuleAlarmMeta addAlarmMeta (long alarmId, long resourceId, ReadingRuleContext rule) throws Exception {
		ReadingRuleAlarmMeta meta = constructAlarmMeta(alarmId, resourceId, rule);
		
//		if (AccountUtil.getCurrentOrg().getId() == 135) {
			LOGGER.debug("Adding alarm meta "+meta);
//		}
		Map<String, Object> prop = FieldUtil.getAsProperties(meta);
		new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReadingRuleAlarmMetaModule().getTableName())
					.fields(FieldFactory.getReadingRuleAlarmMetaFields())
					.insert(prop)
					;
		meta.setId((long) prop.get("id"));
		return meta;
	}
	
	public static void markAlarmMetaAsNotClear (long id, long alarmId) throws SQLException {
		
		FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("alarmId", alarmId);
		prop.put("clear", false);
		
		new GenericUpdateRecordBuilder()
			.table(module.getTableName())
			.fields(FieldFactory.getReadingRuleAlarmMetaFields())
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
			.andCondition(CriteriaAPI.getIdCondition(id, module))
			.update(prop)
			;
	}
	
	public static void markAlarmMetaAsClear (long alarmId) throws SQLException {
		FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleAlarmMetaFields();
		FacilioField alarmIdField = FieldFactory.getAsMap(fields).get("alarmId");
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("clear", true);
		
		new GenericUpdateRecordBuilder()
			.table(module.getTableName())
			.fields(fields)
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
			.andCondition(CriteriaAPI.getCondition(alarmIdField, String.valueOf(alarmId), PickListOperators.IS))
			.update(prop)
			;
	}
	public static List<ReadingRuleContext> getReadingRules(long ruleGroupId) throws Exception {
		if (ruleGroupId <= 0) {
			return null;
		}
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		FacilioModule workflowRuleModule = ModuleFactory.getWorkflowRuleModule();
		fields.addAll(FieldFactory.getReadingRuleFields());
		
		FacilioField ruleGroupIdField = null; 
		
		for(FacilioField field :fields) {
			if(field.getName().equals("ruleGroupId")) {
				ruleGroupIdField = field;
				break;
			}
		}
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(workflowRuleModule.getTableName())
													.innerJoin(readingRuleModule.getTableName())
													.on(workflowRuleModule.getTableName()+".ID = "+readingRuleModule.getTableName()+".ID")
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowRuleModule))
													.andCondition(CriteriaAPI.getCondition(ruleGroupIdField, Collections.singleton(ruleGroupId), NumberOperators.EQUALS));
		ruleBuilder.select(fields);
		List<Map<String, Object>> props = ruleBuilder.get();
		List<ReadingRuleContext> readingRuleContexts = null;
		if(props != null && !props.isEmpty()) {
			readingRuleContexts = new ArrayList<>();
			
			List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, false, true, true);
			for(WorkflowRuleContext workflowRuleContext :workflowRuleContexts) {
				readingRuleContexts.add((ReadingRuleContext)workflowRuleContext);
			}
		}
		return readingRuleContexts;
	}
	public static void addClearEvent(Object record, Context context, Map<String, Object> placeHolders,ReadingContext reading,ReadingRuleContext readingRuleContext) throws Exception {
		
		if(reading == null) {
			reading = (ReadingContext) record;
		}
		Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
		boolean isHistorical = true;
		if (alarmMetaMap == null) {
			alarmMetaMap = readingRuleContext.getAlarmMetaMap();
			isHistorical = false;
		}
		
		ReadingRuleAlarmMeta alarmMeta = alarmMetaMap != null ? alarmMetaMap.get(reading.getParentId()) : null;
		if (isHistorical) {
			LOGGER.info("Alarm meta for rule : "+readingRuleContext.getId()+" for resource : "+reading.getParentId()+" at time : "+reading.getTtime()+"::"+alarmMeta);
		}
		if (alarmMeta != null && !alarmMeta.isClear()) {
			alarmMeta.setClear(true);
			AlarmContext alarm = AlarmAPI.getAlarm(alarmMeta.getAlarmId());
			
			JSONObject json = AlarmAPI.constructClearEvent(alarm, "System auto cleared Alarm because associated rule executed false for the associated resource", reading.getTtime());
			json.put("readingDataId", reading.getId());
			json.put("readingVal", reading.getReading(readingRuleContext.getReadingField().getName()));
			
			if (isHistorical) {
				LOGGER.info("Clearing alarm for rule : "+readingRuleContext.getId()+" for resource : "+reading.getParentId());
			}
			
			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
		}
	}
	
	public static void getRuleConditionText(ReadingRuleContext rule) throws Exception {
		StringBuilder msgBuilder = new StringBuilder();
		switch (rule.getThresholdTypeEnum()) {
		case SIMPLE:
			appendSimpleMsg(msgBuilder, rule);
			appendOccurences(msgBuilder, rule);
			break;
		case AGGREGATION:
			appendSimpleMsg(msgBuilder, rule);
			break;
//		case BASE_LINE:
//			appendBaseLineMsg(msgBuilder, operator, rule);
//			break;
//		case FLAPPING:
//			appendFlappingMsg(msgBuilder, rule);
//			break;
//		case ADVANCED:
//			appendAdvancedMsg(msgBuilder, rule, reading);
//			break;
//		case FUNCTION:
//			appendFunctionMsg(msgBuilder, rule, reading);
//			break;
	}
	}
	
	private static void appendOccurences(StringBuilder msgBuilder, ReadingRuleContext rule) {
		WorkflowContext workflow = rule.getWorkflow();
		if (workflow != null) {
			ExpressionContext expression = (ExpressionContext) workflow.getExpressions().get(0);
			if (expression.getAggregateCondition() != null && !expression.getAggregateCondition().isEmpty()) {
				msgBuilder.append(" for "+rule.getPercentage()+ " time(s)");
			}
		}
	}

	private static void appendSimpleMsg(StringBuilder msgBuilder, ReadingRuleContext rule) throws Exception {
		
		String fieldName = null;
		
		if(rule.getAggregation() != null) {
			fieldName = rule.getAggregation() + "(" + rule.getReadingField().getDisplayName() +")";
		}
		else {
			fieldName = rule.getReadingField().getDisplayName();
		}
		msgBuilder.append(fieldName);
		
		if(rule.getDateRange() > 0) {
			msgBuilder.append(" for "+rule.getDateRange()+ " hour(s)");
		}
		
		NumberOperators operator = (NumberOperators) Operator.OPERATOR_MAP.get(rule.getOperatorId());
		msgBuilder.append(" "+operator.getOperator());
		
		String value = null;
		if (rule.getWorkflow() != null) {
			
			ExpressionContext expr = (ExpressionContext) rule.getWorkflow().getExpressions().get(0);

			if (expr.getAggregateCondition() != null && !expr.getAggregateCondition().isEmpty()) {
				Condition aggrCondition = expr.getAggregateCondition().get(0);
				value = aggrCondition.getValue();
			}
		}
		if (value == null) {
			value = rule.getPercentage();
		}
		
		if ("${previousValue}".equals(value)) {
			msgBuilder.append(" previous value");
		}
		else {
			msgBuilder.append(" "+value);
		}
		
	}

	public static void addTriggerAndClearRule(AlarmRuleContext alarmRule) throws Exception {
		List<ReadingRuleContext> alarmTriggerRules = alarmRule.getAlarmTriggerRules();
		ReadingRuleContext alarmClear = alarmRule.getAlarmClearRule();
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		if(alarmTriggerRules != null) {
			
			for(ReadingRuleContext alarmTriggerRule :alarmTriggerRules) {
				alarmTriggerRule.setId(-1);
				alarmTriggerRule.setRuleType(WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE);
				alarmTriggerRule.setEventId(preRequsiteRule.getEventId());
			}
			if(alarmClear != null) {
				alarmClear.setId(-1);
				alarmClear.setRuleType(WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE);
				alarmClear.setEventId(preRequsiteRule.getEventId());
				alarmTriggerRules.add(alarmClear);
			}
			
			int executionOrder = 1;
			for(ReadingRuleContext alarmTriggerRule :alarmTriggerRules) {
				
				alarmTriggerRule.setOnSuccess(true);
				alarmTriggerRule.setExecutionOrder(executionOrder++);
				
				alarmTriggerRule.setCriteriaId(-1l);
				alarmTriggerRule.setWorkflowId(-1l);
				
				alarmTriggerRule.setRuleGroupId(preRequsiteRule.getId());
				alarmTriggerRule.setParentRuleId(preRequsiteRule.getId());
				alarmTriggerRule.setStatus(true);
				alarmTriggerRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				
				WorkflowRuleAPI.addWorkflowRule(alarmTriggerRule);
			}
			
			if(alarmRule.getAlarmClearRuleDuplicate() != null) {
				alarmClear = alarmRule.getAlarmClearRuleDuplicate();
				
				alarmClear.setCriteriaId(-1l);
				alarmClear.setWorkflowId(-1l);
				
				alarmClear.setId(-1);
				alarmClear.setRuleType(WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE);
				alarmClear.setEventId(preRequsiteRule.getEventId());
				
				alarmClear.setOnSuccess(false);
				
				alarmClear.setRuleGroupId(preRequsiteRule.getId());
				alarmClear.setParentRuleId(preRequsiteRule.getId());
				alarmClear.setStatus(true);
				alarmClear.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				
				WorkflowRuleAPI.addWorkflowRule(alarmClear);
			}
		}
	}
}
