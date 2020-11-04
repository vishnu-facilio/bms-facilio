package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.workflows.context.ExpressionContext;

;

public class ReadingRuleAPI extends WorkflowRuleAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(ReadingRuleAPI.class.getName());
	
	public static final String ALARM_LOG_MODULE_TABLE_NAME = "AlarmLogData";
	
	public static final String ALARM_LOG_MODULE_FIELD_NAME = "alarmLog";
	
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
	
	protected static void addReadingRuleMetrics(ReadingRuleContext rule) throws Exception {
			
		if (rule.getRuleMetrics() != null && !rule.getRuleMetrics().isEmpty()) {
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(ReadingRuleMetricContext metric :rule.getRuleMetrics()) {
				props.add(FieldUtil.getAsProperties(metric));
			}
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getReadingRuleMetricModule().getTableName())
														.fields(FieldFactory.getReadingRuleMetricFields())
														.addRecords(props);
			insertBuilder.save();
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
		deleteRuleMetrics(rule);
		ReadingRuleAPI.addReadingRuleInclusionsExlusions(rule);
		ReadingRuleAPI.addReadingRuleMetrics(rule);
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
	private static void deleteRuleMetrics (ReadingRuleContext readingRule) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingRuleMetricFields());
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder();
		deleteBuilder.table(ModuleFactory.getReadingRuleMetricModule().getTableName());
		deleteBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingRuleId"), readingRule.getId()+"", NumberOperators.EQUALS));
		
		deleteBuilder.delete();
		
	}
	private static void fetchInclusionsExclusions (ReadingRuleContext readingRule) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleInclusionsExclusionsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleInclusionsExclusionsFields();
		FacilioField ruleId = FieldFactory.getAsMap(fields).get("ruleGroupId");
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(fields)
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
		return getReadingRules(criteria, null);
	}
	
	public static List<ReadingRuleContext> getReadingRules(Criteria criteria, List<FacilioField> fields) throws Exception {
		if (fields == null) {
			fields = FieldFactory.getWorkflowRuleFields();
			fields.addAll(FieldFactory.getReadingRuleFields());
		}
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(readingRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(readingRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule));
														;
		
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
		fetchReadingRuleMetricList(readingRule);
		if (fetchChildren) {
			setMatchedResources(readingRule);
			fetchAlarmMeta(readingRule);
		}
		
		return readingRule;
	}
	
	protected static ReadingRuleContext fetchReadingRuleMetricList(ReadingRuleContext rule) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getReadingRuleMetricFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReadingRuleMetricModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingRuleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ReadingRuleMetricContext> readingRuleMetricContexts =  FieldUtil.getAsBeanListFromMapList(props, ReadingRuleMetricContext.class);
			rule.setRuleMetrics(readingRuleMetricContexts);
		}
		return rule;
	}
	
	private static void fetchAlarmMeta (ReadingRuleContext rule) throws Exception {
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.NEW_READING_ALARM);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			List<ReadingAlarm> readingAlarms = new SelectRecordsBuilder<ReadingAlarm>()
													.select(fields)
													.beanClass(ReadingAlarm.class)
													.moduleName(FacilioConstants.ContextNames.NEW_READING_ALARM)
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(rule.getRuleGroupId()), PickListOperators.IS))
													.fetchSupplement((LookupField) fieldMap.get("severity"))
													.get();
			if (CollectionUtils.isNotEmpty(readingAlarms)) {
				Map<Long, ReadingRuleAlarmMeta> metaMap = new HashMap<>();
				for (ReadingAlarm alarm : readingAlarms) {
					ReadingRuleAlarmMeta alarmMeta = constructNewAlarmMeta(alarm.getId(), alarm.getResource(), rule, alarm.getSeverity().getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY), alarm.getSubject());
					metaMap.put(alarmMeta.getResourceId(), alarmMeta);
				}
				rule.setAlarmMetaMap(metaMap);
			}
		}
		else {
			oldFetchAlarmMeta(rule);
		}
	}

	private static void oldFetchAlarmMeta (ReadingRuleContext rule) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleAlarmMetaFields();
		FacilioField ruleGroupField = FieldFactory.getAsMap(fields).get("ruleGroupId");

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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

	public static ReadingRuleAlarmMeta constructNewAlarmMeta (long alarmId, ResourceContext resource, ReadingRuleContext rule, boolean isClear, String subject) {
		ReadingRuleAlarmMeta meta = new ReadingRuleAlarmMeta();
		meta.setOrgId(AccountUtil.getCurrentOrg().getId());
		meta.setAlarmId(alarmId);
		meta.setRuleGroupId(rule.getRuleGroupId());
		meta.setResourceId(resource.getId());
		meta.setResource(resource);
		meta.setReadingFieldId(rule.getReadingFieldId());
		meta.setClear(isClear);
		if(!StringUtils.isEmpty(subject)) {
			meta.setSubject(subject);
		}
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
//			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
//			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
				alarmRuleContext.addAlarmRCARules(ReadingRuleAPI.getAlarmRCARules(groupId));
				alarmRuleContextMap.put(groupId, alarmRuleContext);
			}
		}
		
		return alarmRuleContextMap;
	}
	
	public static List<ReadingRuleContext> getReadingRulesList(long ruleGroupId) throws Exception {
		return getReadingRulesList(Collections.singletonList(ruleGroupId));
	}
	
	public static List<ReadingRuleContext> getReadingRulesList(List<Long> ruleGroupIds) throws Exception {
		return getReadingRulesList(ruleGroupIds, true, true);
		
	}
	public static List<ReadingRuleContext> getReadingRulesList(List<Long> ruleGroupIds, boolean fetchChildren, boolean fetchExtended) throws Exception {
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
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowRuleModule))
													.andCondition(CriteriaAPI.getCondition(ruleGroupIdField, StringUtils.join(ruleGroupIds, ","), NumberOperators.EQUALS));
		ruleBuilder.select(fields);
		List<Map<String, Object>> props = ruleBuilder.get();
		List<ReadingRuleContext> readingRuleContexts = null;
		if(props != null && !props.isEmpty()) {
			readingRuleContexts = new ArrayList<>();
			List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, fetchChildren, fetchExtended);
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
			FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
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
			NumberOperators operator = (NumberOperators) Operator.getOperator(rule.getOperatorId());
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
		
		NumberOperators operator = (NumberOperators) Operator.getOperator(rule.getOperatorId());
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
	
	public static void fillDefaultPropsForAlarmRule(ReadingRuleContext alarmRule,ReadingRuleContext preRequsiteRule,WorkflowRuleContext.RuleType ruleType,Long parentId) throws Exception {
		
		alarmRule.setCriteriaId(-1l);
		alarmRule.setWorkflowId(-1l);
		alarmRule.setIncludedResources(null);
		alarmRule.setExcludedResources(null);
		
		alarmRule.setRuleType(ruleType);
		alarmRule.setModule(preRequsiteRule.getModule());
		alarmRule.setActivityType(preRequsiteRule.getActivityTypeEnum());
		alarmRule.setAssetCategoryId(preRequsiteRule.getAssetCategoryId());
		alarmRule.setRuleGroupId(preRequsiteRule.getId());
		alarmRule.setStatus(true);
		alarmRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		alarmRule.setParentRuleId(parentId);
	}
	
	private static void updateParentRuleId(long oldRuleId,long newRuleId) throws SQLException {

		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleFields())
				.andCustomWhere("PARENT_RULE_ID = ?", oldRuleId);

		Map<String,Object> value = new HashMap<>();
		value.put("parentRuleId", newRuleId);
		update.update(value);
	}

	public static void updateTriggerAndClearRule(AlarmRuleContext alarmRule,AlarmRuleContext oldRule, Context context) throws Exception {

		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();

		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		if(alarmTriggerRule != null) {
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmTriggerRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE,preRequsiteRule.getId());
			alarmTriggerRule.setOnSuccess(true);
			alarmTriggerRule.setClearAlarm(alarmRule.isAutoClear());


			FacilioChain chain = TransactionChainFactory.updateVersionedWorkflowRuleChain();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, alarmTriggerRule);
			chain.execute(context);
			updateParentRuleId(oldRule.getAlarmTriggerRule().getId(),alarmTriggerRule.getId());
		}
		else if (!alarmRule.isAutoClear()) {
			FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
			FacilioContext updateWorkflowContext = chain.getContext();
			ReadingRuleContext oldReadingRule = oldRule.getAlarmTriggerRule();
			oldReadingRule.setClearAlarm(alarmRule.isAutoClear());
			updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, oldReadingRule);
			chain.execute();
		}

		if(alarmTriggerRule == null) {
			alarmTriggerRule = oldRule.getAlarmTriggerRule();			// setting it here since its used bellow
		}

		List<Long> alarmRCARules = alarmRule.getAlarmRCARules();
		ReadingRuleAPI.deleteAlarmRCARules(alarmRule.getGroupId());
		ReadingRuleAPI.addAlarmRCARules(alarmRCARules, alarmRule.getGroupId());

		if(!alarmRule.isAutoClear()) {
			deleteAlarmClearRules(oldRule);

			ReadingRuleContext alarmClearRule = alarmRule.getAlarmClearRule();
			alarmClearRule.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,alarmTriggerRule.getId());
			alarmClearRule.setOnSuccess(false);
			alarmClearRule.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRule);


			ReadingRuleContext alarmClearRuleDuplicate = alarmRule.getAlarmClearRuleDuplicate();
			alarmClearRuleDuplicate.setThresholdType(ThresholdType.SIMPLE);
			ReadingRuleAPI.fillDefaultPropsForAlarmRule(alarmClearRuleDuplicate,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,preRequsiteRule.getId());
			alarmClearRuleDuplicate.setOnSuccess(false);
			alarmClearRuleDuplicate.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRuleDuplicate);
		}
		else {
			deleteAlarmClearRules(alarmRule);
		}
	}

	private static void deleteAlarmClearRules(AlarmRuleContext alarmRule) throws Exception {
		if (alarmRule.getAlarmClearRule() != null) {
			WorkflowRuleAPI.deleteWorkflowRule(alarmRule.getAlarmClearRule().getId());
		}
		if (alarmRule.getAlarmClearRuleDuplicate() != null) {
			WorkflowRuleAPI.deleteWorkflowRule(alarmRule.getAlarmClearRuleDuplicate().getId());
		}
	}

	public static void addTriggerAndClearRule(AlarmRuleContext alarmRule) throws Exception {
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();

		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		fillDefaultPropsForAlarmRule(alarmTriggerRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE,preRequsiteRule.getId());
		alarmTriggerRule.setOnSuccess(true);
		alarmTriggerRule.setClearAlarm(alarmRule.isAutoClear());
		WorkflowRuleAPI.addWorkflowRule(alarmTriggerRule);

		List<Long> alarmRCARules = alarmRule.getAlarmRCARules();
		addAlarmRCARules(alarmRCARules, alarmRule.getGroupId());
		
		if(!alarmRule.isAutoClear()) {
			ReadingRuleContext alarmClearRule = alarmRule.getAlarmClearRule();
			alarmClearRule.setThresholdType(ThresholdType.SIMPLE);
			fillDefaultPropsForAlarmRule(alarmClearRule,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,alarmTriggerRule.getId());
			alarmClearRule.setOnSuccess(false);
			alarmClearRule.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRule);

			ReadingRuleContext alarmClearRuleDuplicate = alarmRule.getAlarmClearRuleDuplicate();
			alarmClearRuleDuplicate.setThresholdType(ThresholdType.SIMPLE);
			fillDefaultPropsForAlarmRule(alarmClearRuleDuplicate,preRequsiteRule,WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE,preRequsiteRule.getId());
			alarmClearRuleDuplicate.setOnSuccess(false);
			alarmClearRuleDuplicate.setClearAlarm(false);
			WorkflowRuleAPI.addWorkflowRule(alarmClearRuleDuplicate);
		}

		if (CollectionUtils.isNotEmpty(alarmRule.getWorkflowRulesForAlarms())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
			List<AlarmWorkflowRuleContext> workflowRulesForAlarms = alarmRule.getWorkflowRulesForAlarms();
			for (AlarmWorkflowRuleContext alarmWorkflowRuleContext : workflowRulesForAlarms) {
				alarmWorkflowRuleContext.setRuleId(alarmTriggerRule.getRuleGroupId());
				alarmWorkflowRuleContext.setModule(module);
				alarmWorkflowRuleContext.setRuleType(WorkflowRuleContext.RuleType.ALARM_WORKFLOW_RULE);
				WorkflowRuleAPI.addWorkflowRule(alarmWorkflowRuleContext);
			}
		}
	}

	public static void deleteAlarmRCARules(Long groupId) throws Exception {
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleRCAMapping().getTableName())
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "rule", String.valueOf(groupId), NumberOperators.EQUALS));
		deleteRecordBuilder.delete();
	}

	public static List<Long> getAlarmRCARules(Long groupId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleRCAMapping().getTableName())
				.select(FieldFactory.getWorkflowRuleRCAMapping())
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "rule", String.valueOf(groupId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		List<Long> rcaIds = new ArrayList<>();
		for (Map<String, Object> prop : props) {
			rcaIds.add((Long) prop.get("rcaRule"));
		}
		return rcaIds;
	}

	public static void addAlarmRCARules(List<Long> alarmRCARules, Long groupId) throws Exception {
		if(alarmRCARules != null) {
			// TODO to be tested
			for (Long rcaID : alarmRCARules) {
				if (groupId == rcaID) {
					throw new IllegalArgumentException("Same Rule cannot be added as RCA");
				}
			}

			GenericInsertRecordBuilder mappingInsert = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getWorkflowRuleRCAMapping().getTableName())
					.fields(FieldFactory.getWorkflowRuleRCAMapping());
			for(Long alarmRCARule :alarmRCARules) {
				Map<String, Object> value = new HashMap<>();
				value.put("rule", groupId);
				value.put("rcaRule", alarmRCARule);
				mappingInsert.addRecord(value);
			}
			mappingInsert.save();
		}
	}

	public static WorkflowRuleContext updateAlarmWorkflowRule(AlarmWorkflowRuleContext rule) throws Exception {
		if (rule.getRuleId() <= 0) {
			throw new IllegalArgumentException("Rule cannot be empty");
		}
		updateWorkflowRuleWithChildren(rule);
		WorkflowRuleAPI.updateExtendedRule(rule, ModuleFactory.getAlarmWorkflowRuleModule(), FieldFactory.getAlarmWorkflowRuleFields());
		return rule;
	}

	public static List<WorkflowRuleContext> getAlarmWorkflowRules(long ruleId) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));

		List<FacilioField> fields = FieldFactory.getAlarmWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();

		FacilioModule alarmWorkflowRuleModule = ModuleFactory.getAlarmWorkflowRuleModule();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(alarmWorkflowRuleModule.getTableName()).on("Workflow_Rule.ID = " + alarmWorkflowRuleModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> list = builder.get();
		// List<AlarmWorkflowRuleContext> workflows = (List<AlarmWorkflowRuleContext>) FieldUtil.getAsBeanListFromMapList(list, AlarmWorkflowRuleContext.class);
		List<WorkflowRuleContext> workflows = WorkflowRuleAPI.getWorkFlowsFromMapList(list, true, true);

		return workflows;
	}
	public static List<ReadingRuleContext> getAlarmRulesList(Long ruleGroupId) throws Exception {
		if (ruleGroupId == null && ruleGroupId < 0) {
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
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowRuleModule))
				.andCondition(CriteriaAPI.getCondition(ruleGroupIdField, StringUtils.join(Collections.singleton(ruleGroupId), ","), NumberOperators.EQUALS));
		ruleBuilder.select(fields);
		List<Map<String, Object>> props = ruleBuilder.get();
		List<ReadingRuleContext> readingRuleContexts = null;
		if(props != null && !props.isEmpty()) {
			readingRuleContexts = new ArrayList<>();
			List<WorkflowRuleContext> workflowRuleContexts = getWorkFlowsFromMapList(props, false, true);
			for(WorkflowRuleContext workflowRuleContext :workflowRuleContexts) {
				workflowRuleContext.setActions(ActionAPI.getActiveActionsFromWorkflowRule(workflowRuleContext.getId()));
				readingRuleContexts.add((ReadingRuleContext)workflowRuleContext);
			}
		}
		return readingRuleContexts;
	}

	public static Map<Long, String> getRuleForRca(Long categoryId) throws Exception {

		List<FacilioField> fields = new ArrayList<FacilioField>();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		FacilioModule workflowRuleModule = ModuleFactory.getWorkflowRuleModule();
			fields = FieldFactory.getWorkflowRuleFields();
			fields.addAll(FieldFactory.getReadingRuleFields());



		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(readingRuleModule.getTableName())
				.innerJoin(workflowRuleModule.getTableName())
				.on(readingRuleModule.getTableName()+".ID = "+ workflowRuleModule.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", WorkflowRuleContext.RuleType.READING_RULE.getIntVal()+"", NumberOperators.EQUALS ))
				.andCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategoryId", StringUtils.join(Collections.singleton(categoryId), ","), NumberOperators.EQUALS));
		ruleBuilder.select(fields);
		List<Map<String, Object>> props = ruleBuilder.get();
		Map<Long, String> rcaMap = new HashMap<>();
		if(props != null && props.size() > 0) {
			for(Map<String, Object> record : props) {
				rcaMap.put((Long) record.get("id"), (String) record.get("name"));
			}
		}
		return rcaMap;
	}
	public static Map<String,Object> getMatchedResourcesWithCount(ReadingRuleContext readingRule) throws Exception{
		Map<String,Object> resourcesWithCount=new HashMap<>();
		List<Long> matchedResourceIds=new ArrayList<>();
		Map<Long, ResourceContext> matchedResources = new HashMap<>();
			if (readingRule.getAssetCategoryId() == -1) {
				long resourceId = readingRule.getResourceId();
				matchedResources=Collections.singletonMap(resourceId, ResourceAPI.getExtendedResource(resourceId));
				for(Long matchedResourceId:matchedResources.keySet())
				{
					matchedResourceIds.add(matchedResourceId);
				}	
			}
			else {
				List<AssetContext> categoryAssets = AssetsAPI.getAssetListOfCategory(readingRule.getAssetCategoryId());
				if (categoryAssets != null && !categoryAssets.isEmpty()) {
					fetchInclusionsExclusions(readingRule);
					
					for (AssetContext asset : categoryAssets) {
						if ( (readingRule.getIncludedResources() == null 
								|| readingRule.getIncludedResources().isEmpty() 
								|| readingRule.getIncludedResources().contains(asset.getId()))
								&& (readingRule.getExcludedResources() == null 
									|| readingRule.getExcludedResources().isEmpty()
									|| !readingRule.getExcludedResources().contains(asset.getId()))
								) {
							matchedResources.put(asset.getId(), asset);
							matchedResourceIds.add(asset.getId());
						}
					}
				}
			}
			resourcesWithCount.put("count",matchedResourceIds.size());
			resourcesWithCount.put("resourceIds",matchedResourceIds);

			return resourcesWithCount;
		}
	
	public static FacilioContext addAdditionalPropsForRecordBasedInstantJob(FacilioModule module, Object record, Map<Long, List<UpdateChangeSet>> currentChangeSet, List<EventType> eventTypes, Context context, RuleType... ruleTypes) {
		
		FacilioContext instantParallelWorkflowRuleJobContext = new FacilioContext();
		HashMap<String, Object> workflowRuleExecutionMap = new HashMap<String, Object>();	
		//workflowRuleExecutionMap.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, workflowRulesForInstantJobs);
		workflowRuleExecutionMap.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		workflowRuleExecutionMap.put(FacilioConstants.ContextNames.RECORD, record);
		workflowRuleExecutionMap.put(FacilioConstants.ContextNames.CHANGE_SET, currentChangeSet);
		//workflowRuleExecutionMap.put(FacilioConstants.ContextNames.PLACE_HOLDER, recordPlaceHolders);
		//workflowRuleExecutionMap.put(FacilioConstants.ContextNames.PROPAGATE_ERROR, propagateError);
		//workflowRuleExecutionMap.put(FacilioConstants.ContextNames.WORKFLOW_RULE_CACHE_MAP, workflowRuleCacheMap);
		workflowRuleExecutionMap.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
		workflowRuleExecutionMap.put(FacilioConstants.ContextNames.RULE_TYPES, ruleTypes);
		
		FacilioContext recordContextFilteredForRuleExecution = new FacilioContext();
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META));
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META));
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.IS_READING_RULE_EXECUTE_FROM_JOB, (Boolean) context.get(FacilioConstants.ContextNames.IS_READING_RULE_EXECUTE_FROM_JOB));
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.READING_RULE_ALARM_META, (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META));	
		recordContextFilteredForRuleExecution.put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT));	
		recordContextFilteredForRuleExecution.put(EventConstants.EventContextNames.PREVIOUS_EVENT_META, (ReadingEventContext)context.get(EventConstants.EventContextNames.PREVIOUS_EVENT_META));	
		recordContextFilteredForRuleExecution.put(EventConstants.EventContextNames.EVENT_RULE_LIST, context.get(EventConstants.EventContextNames.EVENT_RULE_LIST));	
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.READING_RULE_ALARM_OCCURANCE, (AlarmOccurrenceContext) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_OCCURANCE));	
		recordContextFilteredForRuleExecution.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME));	

		instantParallelWorkflowRuleJobContext.put(FacilioConstants.ContextNames.RECORD_CONTEXT_FOR_RULE_EXECUTION, recordContextFilteredForRuleExecution);
		instantParallelWorkflowRuleJobContext.put(FacilioConstants.ContextNames.WORKFLOW_PARALLEL_RULE_EXECUTION_MAP, workflowRuleExecutionMap);
		return instantParallelWorkflowRuleJobContext;		
	}
}
