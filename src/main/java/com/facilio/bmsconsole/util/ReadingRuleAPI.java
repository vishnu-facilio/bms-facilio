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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
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
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Alarm;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.sql.mysql.DeleteRecordBuilder;
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
		deleteInclusionsExclusions(oldRule);
		ReadingRuleAPI.addReadingRuleInclusionsExlusions((ReadingRuleContext) rule);
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
	private static void deleteInclusionsExclusions (ReadingRuleContext readingRule) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingRuleInclusionsExclusionsFields());
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder();
		deleteBuilder.table(ModuleFactory.getReadingRuleInclusionsExclusionsModule().getTableName());
		deleteBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleGroupId"), readingRule.getRuleGroupId()+"", NumberOperators.EQUALS));
		
		deleteBuilder.delete();
		
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
	public static Map<Long, AlarmRuleContext> getAlarmRuleMap(List<Long> ruleGroupIds) throws Exception {
		
		List<ReadingRuleContext> readingRuleContexts = getReadingRulesList(ruleGroupIds);
		
		Map<Long,List<ReadingRuleContext>> readingRuleMap = new HashMap<>();
		Map<Long,AlarmRuleContext> alarmRuleContextMap = new HashMap<>();
		if(readingRuleContexts != null) {
			for(ReadingRuleContext readingRuleContext :readingRuleContexts) {
				
				if(readingRuleMap.get(readingRuleContext.getRuleGroupId()) == null) {
					List<ReadingRuleContext> readingRuleList = new ArrayList<>();
					readingRuleMap.put(readingRuleContext.getRuleGroupId(), readingRuleList);
				}
				readingRuleMap.get(readingRuleContext.getRuleGroupId()).add(readingRuleContext);
			}
			
			for(Long groupId :readingRuleMap.keySet()) {
				AlarmRuleContext alarmRuleContext = new AlarmRuleContext(readingRuleMap.get(groupId));
				alarmRuleContextMap.put(groupId, alarmRuleContext);
			}
		}
		
		return alarmRuleContextMap;
	}
	
	public static List<ReadingRuleContext> getReadingRulesList(long ruleGroupId) throws Exception {
		return getReadingRulesList(Collections.singletonList(ruleGroupId));
	}
	public static List<ReadingRuleContext> getReadingRulesList(List<Long> ruleGroupIds) throws Exception {
		if (ruleGroupIds == null || ruleGroupIds.isEmpty()) {
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
													.andCondition(CriteriaAPI.getCondition(ruleGroupIdField, StringUtils.join(ruleGroupIds, ","), NumberOperators.EQUALS));
		ruleBuilder.select(fields);
		List<Map<String, Object>> props = ruleBuilder.get();
		List<ReadingRuleContext> readingRuleContexts = null;
		if(props != null && !props.isEmpty()) {
			readingRuleContexts = new ArrayList<>();
			List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, false, true, true);
			for(WorkflowRuleContext workflowRuleContext :workflowRuleContexts) {
				workflowRuleContext.setActions(ActionAPI.getActiveActionsFromWorkflowRule(workflowRuleContext.getId()));
				readingRuleContexts.add((ReadingRuleContext)workflowRuleContext);
			}
		}
		return readingRuleContexts;
	}
	public static void addClearEvent(Context context, Map<String, Object> placeHolders, ReadingRuleContext readingRuleContext, long readingDataId, Object readingVal, long ttime, long resourceId) throws Exception {
		
		Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
		boolean isHistorical = true;
		if (alarmMetaMap == null) {
			alarmMetaMap = readingRuleContext.getAlarmMetaMap();
			isHistorical = false;
		}
		
		ReadingRuleAlarmMeta alarmMeta = alarmMetaMap != null ? alarmMetaMap.get(resourceId) : null;
		if (isHistorical) {
			LOGGER.info("Alarm meta for rule : "+readingRuleContext.getId()+" for resource : "+resourceId+" at time : "+ttime+"::"+alarmMeta);
		}
		if (alarmMeta != null && !alarmMeta.isClear()) {
			alarmMeta.setClear(true);
			AlarmContext alarm = AlarmAPI.getAlarm(alarmMeta.getAlarmId());
			
			JSONObject json = AlarmAPI.constructClearEvent(alarm, "System auto cleared Alarm because associated rule executed clear condition for the associated resource", ttime);
			if (alarm.getSourceTypeEnum() == SourceType.THRESHOLD_ALARM) {
				json.put("readingDataId", readingDataId);
				json.put("readingVal", readingVal);
			}
			
			if (isHistorical) {
				LOGGER.info("Clearing alarm for rule : "+readingRuleContext.getId()+" for resource : "+resourceId);
			}
			
			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
		}
	}
	
	public static String getRuleConditionText(ReadingRuleContext rule) throws Exception {
		StringBuilder msgBuilder = new StringBuilder();
		switch (rule.getThresholdTypeEnum()) {
		case SIMPLE:
			appendSimpleMsg(msgBuilder, rule);
			AlarmAPI.appendOccurences(msgBuilder, rule);	// not working good bcs wf getting saved in different format from client.
			break;
		case AGGREGATION:
			appendSimpleMsg(msgBuilder, rule);
			break;
		case BASE_LINE:
			NumberOperators operator = (NumberOperators) Operator.OPERATOR_MAP.get(rule.getOperatorId());
			AlarmAPI.appendBaseLineMsg(msgBuilder, operator, rule);
			break;
		case FLAPPING:
			AlarmAPI.appendFlappingMsg(msgBuilder, rule);
			break;
//		case ADVANCED:
//			appendAdvancedMsg(msgBuilder, rule, reading);
//			break;
//		case FUNCTION:
//			appendFunctionMsg(msgBuilder, rule, reading);
//			break;
	}
		return msgBuilder.toString();
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
	
	public static void fillDefaultPropsForAlarmRule(ReadingRuleContext alarmRule,ReadingRuleContext preRequsiteRule,WorkflowRuleContext.RuleType ruleType,Long parentId) {
		
		alarmRule.setCriteriaId(-1l);
		alarmRule.setWorkflowId(-1l);
		alarmRule.setIncludedResources(null);
		alarmRule.setExcludedResources(null);
		
		alarmRule.setRuleType(ruleType);
		alarmRule.setEventId(preRequsiteRule.getEventId());
		alarmRule.setAssetCategoryId(preRequsiteRule.getAssetCategoryId());
		alarmRule.setRuleGroupId(preRequsiteRule.getId());
		alarmRule.setStatus(true);
		alarmRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		alarmRule.setParentRuleId(parentId);
		
	}

	public static void addTriggerAndClearRule(AlarmRuleContext alarmRule) throws Exception {
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		Map<String,Long> ruleNameVsIdMap = new HashMap<>();
		ruleNameVsIdMap.put(preRequsiteRule.getName(), preRequsiteRule.getId());
		
		
		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		fillDefaultPropsForAlarmRule(alarmTriggerRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE,preRequsiteRule.getId());
		alarmTriggerRule.setOnSuccess(true);
		alarmTriggerRule.setClearAlarm(alarmRule.isAutoClear());
		WorkflowRuleAPI.addWorkflowRule(alarmTriggerRule);
		ruleNameVsIdMap.put(alarmTriggerRule.getName(), alarmTriggerRule.getId());
		
		
		List<ReadingRuleContext> alarmRCARules = alarmRule.getAlarmRCARules();
		
		if(alarmRCARules != null) {
			
			int executionOrder = 1;
			for(ReadingRuleContext alarmRCARule :alarmRCARules) {
				
				alarmRCARule.setExecutionOrder(executionOrder++);
				
				Long parentId = alarmRCARule.getParentRuleName() != null ? ruleNameVsIdMap.get(alarmRCARule.getParentRuleName()) : alarmTriggerRule.getId();
				if(alarmRCARule.getParentRuleName() == null) {
					alarmRCARule.setOnSuccess(true);
				}
				alarmRCARule.setClearAlarm(false);
				fillDefaultPropsForAlarmRule(alarmRCARule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_RCA_RULES,parentId);
				
				WorkflowRuleAPI.addWorkflowRule(alarmRCARule);
				ruleNameVsIdMap.put(alarmRCARule.getName(), alarmRCARule.getId());
			}
		}
		
		if(!alarmRule.isAutoClear()) {
			ReadingRuleContext alarmClearRule = alarmRule.getAlarmClearRule();
			alarmClearRule.setThresholdType(ThresholdType.SIMPLE);
			fillDefaultPropsForAlarmRule(alarmClearRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,alarmTriggerRule.getId());
			alarmClearRule.setOnSuccess(false);
			alarmClearRule.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRule);
			ruleNameVsIdMap.put(alarmClearRule.getName(), alarmClearRule.getId());
			
			
			ReadingRuleContext alarmClearRuleDuplicate = alarmRule.getAlarmClearRuleDuplicate();
			alarmClearRuleDuplicate.setThresholdType(ThresholdType.SIMPLE);
			fillDefaultPropsForAlarmRule(alarmClearRuleDuplicate,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,preRequsiteRule.getId());
			alarmClearRuleDuplicate.setOnSuccess(false);
			alarmClearRuleDuplicate.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRuleDuplicate);
			ruleNameVsIdMap.put(alarmClearRuleDuplicate.getName(), alarmClearRuleDuplicate.getId());
		}
	}
}
