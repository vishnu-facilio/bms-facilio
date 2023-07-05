package com.facilio.bmsconsole.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.WordUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleMetricContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;

public class AlarmAPI {
	private static final Logger LOGGER = LogManager.getLogger(AlarmAPI.class.getName());
	public static final String ALARM_COST_FIELD_NAME = "alarmCost";
	public static JSONObject constructClearEvent(AlarmContext alarm, String msg) throws Exception {
		return constructClearEvent(alarm, msg, -1);
	}
	
	public static JSONObject constructClearEvent(AlarmContext alarm, String msg, long timestamp) throws Exception {
		JSONObject event = new JSONObject();
		
		event.put("message", alarm.getSubject());
		event.put("condition", alarm.getCondition());
		event.put("source", alarm.getSource());
		if (alarm.getResource() != null && alarm.getResource().getId() > 0) {
			event.put("resourceId", alarm.getResource().getId());
		}
		event.put("autoClear", true);
		event.put("siteId", alarm.getSiteId());
		event.put("severity", FacilioConstants.Alarm.CLEAR_SEVERITY);
		event.put("comment", msg);
		if (timestamp != -1) {
			event.put("timestamp", timestamp);
		}
		event.put("alarmId", alarm.getId());
		
		if (alarm.getControllerId() != -1) {
			event.put("controllerId", alarm.getControllerId());
		}
		
		switch (alarm.getSourceTypeEnum()) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
				ReadingAlarmContext readingAlarm = getReadingAlarmContext(alarm.getId());
				event.put("sourceType", readingAlarm.getSourceTypeEnum().getIndex());
				event.put("ruleId", readingAlarm.getRuleId());
				event.put("readingFieldId", readingAlarm.getReadingFieldId());
				break;
			case ML_ALARM:
				MLAlarmContext mlAlarm = getMLAlarmContext(alarm.getId());
				event.put("sourceType", mlAlarm.getSourceTypeEnum().getIndex());
				event.put("ruleId", mlAlarm.getRuleId());
				event.put("readingFieldId", mlAlarm.getReadingFieldId());
				break;
			default:
				break;
		}
		
		return event;
	}
	
	public static AlarmContext getExtendedAlarm (long alarmId, SourceType type) throws Exception {
		switch (type) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
				return getReadingAlarmContext(alarmId);
			case ML_ALARM:
				return getMLAlarmContext(alarmId);
			default:
				return null;
		}
	}
	
	public static MLAlarmContext getMLAlarmContext (long alarmId) throws Exception {
		return getExtendedAlarm(alarmId, FacilioConstants.ContextNames.ML_ALARM);
	}
	
	public static ReadingAlarmContext getReadingAlarmContext(long alarmId) throws Exception {
		return getExtendedAlarm(alarmId, FacilioConstants.ContextNames.READING_ALARM);
	}
	
	@SuppressWarnings("unchecked")
	private static <E extends AlarmContext> E getExtendedAlarm(long alarmId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>()
																.select(modBean.getAllFields(moduleName))
																.module(module)
																.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
																.andCondition(CriteriaAPI.getIdCondition(alarmId, module));
		
		List<E> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static void loadExtendedAlarms(List<AlarmContext> alarms) throws Exception {
		if (alarms != null && !alarms.isEmpty()) {
			Map<SourceType, List<Long>> typeWiseIds = new HashMap<>();
			for (AlarmContext alarm : alarms) {
				List<Long> ids = typeWiseIds.get(alarm.getSourceTypeEnum());
				if (ids == null) {
					ids = new ArrayList<>();
					typeWiseIds.put(alarm.getSourceTypeEnum(), ids);
				}
				ids.add(alarm.getId());
			}
			Map<SourceType, Map<Long, ? extends AlarmContext>> typeWiseAlarms = getTypeWiseAlarms(typeWiseIds);
			
			for (int i = 0; i < alarms.size(); i++) {
				AlarmContext alarm = alarms.get(i);
				SourceType type = alarm.getSourceTypeEnum();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM:
						case ANOMALY_ALARM:
						case ML_ALARM:
							alarms.set(i, typeWiseAlarms.get(alarm.getSourceTypeEnum()).get(alarm.getId()));
							break;
						default:
							break;
					}
				}
			}
		}
	}
	
	public static boolean isReadingRuleAlarm (SourceType type) {
		switch (type) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
			case ML_ALARM:
				return true;
			default:
				return false;
		}
	}
	
	private static Map<SourceType, Map<Long, ? extends AlarmContext>> getTypeWiseAlarms (Map<SourceType, List<Long>> typeWiseIds) throws Exception {
		Map<SourceType, Map<Long,? extends AlarmContext>> typewiseAlarms = new HashMap<>();
		
		if (typeWiseIds != null && !typeWiseIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map.Entry<SourceType, List<Long>> entry : typeWiseIds.entrySet()) {
				SourceType type = entry.getKey();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM:
						case ANOMALY_ALARM: 
							typewiseAlarms.put(entry.getKey(), fetchExtendedAlarms(modBean.getModule(FacilioConstants.ContextNames.READING_ALARM), modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM), entry.getValue(), ReadingAlarmContext.class));
							break;
						case ML_ALARM:
							typewiseAlarms.put(entry.getKey(), fetchExtendedAlarms(modBean.getModule(FacilioConstants.ContextNames.ML_ALARM), modBean.getAllFields(FacilioConstants.ContextNames.ML_ALARM), entry.getValue(), MLAlarmContext.class));
							break;
						default:
							break;
					}
				}
			}
		}
		
		return typewiseAlarms;
	}
	
	private static <E extends AlarmContext> Map<Long, E> fetchExtendedAlarms(FacilioModule module, List<FacilioField> fields, List<Long> ids, Class<E> alarmClass) throws Exception {
		SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>()
																		.select(fields)
																		.module(module)
																		.beanClass(alarmClass)
																		.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return selectBuilder.getAsMap();
	}
	
	public static void updateAlarmDetailsInTicket(AlarmContext sourceAlarm, AlarmContext destinationAlarm) throws Exception {
		
		TicketCategoryContext category =  null;
		if (sourceAlarm.getAlarmTypeEnum() != null) {
			switch (sourceAlarm.getAlarmTypeEnum()) {
				case FIRE:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Fire Safety");
					break;
				case HVAC:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "HVAC");
					break;
				case ENERGY:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Energy");
					break;
				default:
					break;
			}
			if(category != null) {
				destinationAlarm.setCategory(category);
			}
		}
		
		if(sourceAlarm != destinationAlarm && destinationAlarm.getSeverity() != null) {
			Map<Long, AlarmSeverityContext> severityMap = AlarmAPI.getAlarmSeverityMap(sourceAlarm.getSeverity().getId(), destinationAlarm.getSeverity().getId());
			if (destinationAlarm.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY) || destinationAlarm.isForceSeverity() || severityMap.get(sourceAlarm.getSeverity().getId()).getCardinality() > severityMap.get(destinationAlarm.getSeverity().getId()).getCardinality()) { //Updating severity only if previous severity is lower
				destinationAlarm.setPreviousSeverity(sourceAlarm.getSeverity());
			}
			else {
				destinationAlarm.setSeverity(null);
			}
		}
		
		ResourceContext resource = sourceAlarm.getResource();
		if(resource != null && resource.getId() != -1) {
			resource = ResourceAPI.getResource(resource.getId());
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					if(sourceAlarm.getSource() != null) {
						String description;
						if(sourceAlarm.isAcknowledged()) {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nSource - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",sourceAlarm.getSource(), resource.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nSource - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",sourceAlarm.getSource(),resource.getName());
						}
						destinationAlarm.setDescription(description);
					}
					break;
				case ASSET:
					String description;
					BaseSpaceContext space = SpaceAPI.getBaseSpace(resource.getSpaceId());
					
					if(sourceAlarm.isAcknowledged()) {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nSource - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nSource - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					else {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nSource - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nSource - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					destinationAlarm.setDescription(description);
					break;
			}
		}
		else {
			if(sourceAlarm.isAcknowledged()) {
				destinationAlarm.setDescription(MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nSource - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",sourceAlarm.getSource()));
			}
			else {
				destinationAlarm.setDescription(MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nSource - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",sourceAlarm.getSource()));
			}
		}
	}
	
	public static String sendAlarmSMS(AlarmContext alarm, String to, String message) throws Exception {
		ResourceContext resource = alarm.getResource();
		if(resource != null) {
			resource = ResourceAPI.getResource(resource.getId());
			String spaceName = null;
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					spaceName = resource.getName();
					break;
				case ASSET:
					spaceName = SpaceAPI.getBaseSpace(resource.getSpaceId()).getName();
					break;
			}
			String sms = null;
			if(message != null && !message.isEmpty()) {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, {3}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName, message);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}, {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), message);
				}
			}
			else {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}", alarm.getAlarmTypeVal(), alarm.getSubject());
				}
			}
			JSONObject json = new JSONObject();
			json.put("to", to);
			json.put("message", sms);
			return SMSUtil.sendSMS(json);
		}
		return null;
	}
	
	public static long addAlarmEntity() throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentAccount().getOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table("Alarm_Entity")
				.fields(FieldFactory.getAlarmEntityFields())
				.addRecord(prop);
		insertBuilder.save();
		return (long) prop.get("id");
	}
	
	public static AlarmSeverityContext getAlarmSeverity(String severity) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("SEVERITY = ?", severity);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static AlarmSeverityContext getAlarmSeverity(long severityId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("ID = ?", severityId);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static Map<Long, AlarmSeverityContext> getAlarmSeverityMap(Long... ids) throws Exception {
		return getAlarmSeverityMap(Arrays.asList(ids));
	}
	
	public static Map<Long, AlarmSeverityContext> getAlarmSeverityMap(Collection<Long> ids) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_SEVERITY);
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.module(module)
																		.beanClass(AlarmSeverityContext.class)
																		.andCondition(CriteriaAPI.getIdCondition(ids, module))
																		;
		
		return selectBuilder.getAsMap();
	}
	
	public static List<AlarmSeverityContext> getAlarmSeverityList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.orderBy("Alarm_Severity.CARDINALITY");
		
		return selectBuilder.get();
	}
	
	public static AlarmType getAlarmTypeFromResource(long resourceId) throws Exception {
		if (resourceId != -1) {
			ResourceContext resource = ResourceAPI.getExtendedResource(resourceId);
			if (resource != null && resource.getResourceTypeEnum() == ResourceType.ASSET) {
				AssetContext asset = (AssetContext) resource;
				if (asset.getCategory() != null && asset.getCategory().getId() != -1) {
					return getAlarmTypeFromAssetCategory(asset.getCategory().getId());
				}
			}
		}
		return null;
	}
	
	private static SelectRecordsBuilder<ReadingAlarmContext> getReadingAlarmsSelectBuilder (long startTime, long endTime, boolean isWithAnomaly, List<FacilioField> fields, Map<String, FacilioField> fieldMap) {
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
																.select(fields)
																.moduleName(FacilioConstants.ContextNames.READING_ALARM)
																.beanClass(ReadingAlarmContext.class)
																.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL))
																.orderBy("CREATED_TIME")
																;
		
		Condition condition1 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL);
		Condition condition2 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), CommonOperators.IS_EMPTY);
		
		Criteria criteria = new Criteria();
		
		criteria.addOrCondition(condition1);
		criteria.addOrCondition(condition2);
		
		selectBuilder.andCriteria(criteria);
		if(!isWithAnomaly) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(SourceType.ANOMALY_ALARM.getIndex()), NumberOperators.NOT_EQUALS));
		}
		return selectBuilder;
	}
	
	public static List<ReadingAlarmContext> getReadingAlarms(List<Long> resourceId, Long ruleId, long fieldId, long startTime, long endTime, boolean isWithAnomaly) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = getReadingAlarmsSelectBuilder(startTime, endTime, isWithAnomaly, fields, fieldMap);
		if (resourceId != null && resourceId.size() > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId, PickListOperators.IS));
		}

		boolean ruleAvailable = false;
		if (ruleId != null && ruleId != -1) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(ruleId), NumberOperators.EQUALS));
			ruleAvailable = true;
		}
		if (CollectionUtils.isNotEmpty(resourceId)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId, PickListOperators.IS));
		}
		else if (!ruleAvailable) {
			throw new IllegalArgumentException("Resource Id or Rule Id should be available");
		}
		
		if(fieldId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), String.valueOf(fieldId), NumberOperators.EQUALS));
		}
		List<ReadingAlarmContext> alarms = selectBuilder.get();
		/*if (AccountUtil.getCurrentOrg().getId() == 75) {
			LOGGER.info("Fetched Alarm Query : "+selectBuilder.toString());
		}*/
		return alarms;
	}
	
	public static List<ReadingAlarmContext> getReadingAlarms (long entityId, long startTime, long endTime, boolean isWithAnomaly) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = getReadingAlarmsSelectBuilder(startTime, endTime, isWithAnomaly, fields, fieldMap)
																	.andCondition(CriteriaAPI.getCondition(fieldMap.get("entityId"), String.valueOf(entityId), NumberOperators.EQUALS))
																	;
		
		return selectBuilder.get();
	}
	
	public static AlarmType getAlarmTypeFromAssetCategory(long categoryId) throws Exception {
		AssetCategoryContext category = AssetsAPI.getCategoryForAsset(categoryId);
		
		switch (category.getTypeEnum()) {
			case ENERGY:
				return AlarmType.ENERGY;
			case FIRE:
				return AlarmType.FIRE;
			case HVAC:
				return AlarmType.HVAC;
			case MISC:
				return AlarmType.MAINTENANCE;
			default:
				return null;
		}
	}
	
	public static void addReadingAlarmProps(JSONObject obj, ReadingRuleContext rule, ReadingContext reading) throws Exception {
		obj.put("readingFieldId", rule.getReadingFieldId());
		obj.put("readingDataId", reading.getId());
		obj.put("readingVal", reading.getReading(rule.getReadingField().getName()));
		obj.put("condition", rule.getName());
		obj.put("ruleId", rule.getRuleGroupId());
		obj.put("subRuleId", rule.getId());
		if (rule.getBaselineId() != -1) {
			obj.put("baselineId", rule.getBaselineId());
		}
		obj.put("sourceType", SourceType.THRESHOLD_ALARM.getIndex());
		DateRange range = getRange(rule, reading);
		obj.put("startTime", range.getStartTime());
		if (range.getEndTime() != -1) {
			obj.put("endTime", range.getEndTime());
		}
		
		obj.put("readingMessage", getMessage(rule, range, reading));
		obj.put("resourceId", reading.getParentId());
		obj.put("siteId", ((ResourceContext) reading.getParent()).getSiteId());
		
		String resourceName = ((ResourceContext)reading.getParent()).getName();
		obj.put("source", resourceName);
		obj.put("timestamp", reading.getTtime());
		
		JSONObject values = getMetricsValuesForRule(rule, reading);
		obj.put("metricValues", values);
	}
	
	private static JSONObject getMetricsValuesForRule(ReadingRuleContext rule, ReadingContext reading) throws Exception {
		try {
			if(rule.getRuleGroupId() > 0) {
				long resourceId = ((ResourceContext)reading.getParent()).getId();
				ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(rule.getRuleGroupId());
				if(readingRule.getRuleMetrics() !=null && !readingRule.getRuleMetrics().isEmpty()) {
					
					Pair<Long, FacilioField> pair = null;
					List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
					JSONObject metricValues = new JSONObject();
					
					for( ReadingRuleMetricContext metric : readingRule.getRuleMetrics()) {
						long tempResourceId = metric.getResourceId() > 0 ? metric.getResourceId() : resourceId;   
						pair = Pair.of(tempResourceId, metric.getField());
						rdmPairs.add(pair);
					}
					
					Map<String, ReadingDataMeta> rdms = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
					
					
					for(String rdmKey : rdms.keySet()) {
						ReadingDataMeta rdm = rdms.get(rdmKey);
						
						long actualLastRecordedTime = CommonAPI.getActualLastRecordedTime(rdm.getField().getModule());
						if(actualLastRecordedTime > 0) {
							if(rdm.getTtime() >= actualLastRecordedTime) {
								metricValues.put(rdmKey, rdm.getValue());
							}
						}
						else {
							metricValues.put(rdmKey, rdm.getValue());
						}
					}
					return metricValues;
				}
			}
		}
		catch(Exception e) {
			LOGGER.error("ERROR DURING METRICS VALUE FETCH", e);
		}
		return null;
	}
	
	public static void addMLAlarmProps (JSONObject obj, ReadingRuleContext rule, Context context) throws Exception {
		obj.put("sourceType", SourceType.ML_ALARM.getIndex());
		obj.put("resourceId", rule.getResourceId());
		
		ResourceContext resource = ResourceAPI.getResource(rule.getResourceId());
		if (resource != null) {
			obj.put("source", resource.getName());
		}
		
		obj.put("siteId", rule.getSiteId());
		obj.put("timestamp", context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME));
		obj.put("ruleId", rule.getRuleGroupId());
		obj.put("readingFieldId", rule.getReadingFieldId());
		obj.put("condition", rule.getName());
	}
	
	private static DateRange getRange(ReadingRuleContext rule, ReadingContext reading) {
		DateRange range = null;
		switch (rule.getThresholdTypeEnum()) {
			case SIMPLE:
				if (rule.getCriteria() != null) {
					range = new DateRange();
					range.setStartTime(reading.getTtime());
				}
				else {
					WorkflowContext workflow = rule.getWorkflow();
					ExpressionContext expression = (ExpressionContext) workflow.getExpressions().get(0);
					if (expression.getLimit() != null) {
						range = new DateRange();
						range.setStartTime(reading.getTtime());
					}
					else {
						Condition condition = expression.getCriteria().getConditions().get("2");
						if(condition != null) {
							range = ((DateOperators) condition.getOperator()).getRange(condition.getValue());
						}
						else {
							range = new DateRange();
							range.setStartTime(reading.getTtime());
						}
					}
				}
				break;
			case AGGREGATION:
			case BASE_LINE:
				range = DateOperators.LAST_N_HOURS.getRange(String.valueOf(rule.getDateRange())+","+reading.getTtime());
				break;
			case FLAPPING:
				range = new DateRange();
				range.setEndTime(reading.getTtime());
				range.setStartTime(range.getEndTime() - rule.getFlapInterval());
				break;
			case ADVANCED:
			case FUNCTION:
				range = new DateRange();
				range.setStartTime(reading.getTtime());
				break;
		}
		return range;
	}
	
	private static String getMessage(ReadingRuleContext rule, DateRange range, ReadingContext reading) throws Exception {
		StringBuilder msgBuilder = new StringBuilder();
		if (rule.getAggregation() != null) {
			if(rule.getDateRange() == 1) {
				msgBuilder.append("Hourly ")
							.append(rule.getAggregation());
			}
			else {
				msgBuilder.append(WordUtils.capitalize(rule.getAggregation()));
			}
			msgBuilder.append(" of ");
		}
		msgBuilder.append("'")
					.append(rule.getReadingField().getDisplayName())
					.append("' ");
		
		NumberOperators operator = (NumberOperators) Operator.getOperator(rule.getOperatorId());
		switch (rule.getThresholdTypeEnum()) {
			case SIMPLE:
				appendSimpleMsg(msgBuilder, operator, rule, reading);
				appendOccurences(msgBuilder, rule);
				break;
			case AGGREGATION:
				appendSimpleMsg(msgBuilder, operator, rule, reading);
				break;
			case BASE_LINE:
				appendBaseLineMsg(msgBuilder, operator, rule);
				break;
			case FLAPPING:
				appendFlappingMsg(msgBuilder, rule);
				break;
			case ADVANCED:
				appendAdvancedMsg(msgBuilder, rule, reading);
				break;
			case FUNCTION:
				appendFunctionMsg(msgBuilder, rule, reading);
				break;
		}
		
		if (range.getEndTime() != -1) {
			msgBuilder.append(" between ")
					.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(DateTimeUtil.READABLE_DATE_FORMAT))
					.append(" and ")
					.append(DateTimeUtil.getZonedDateTime(range.getEndTime()).format(DateTimeUtil.READABLE_DATE_FORMAT));
		}
		else {
			msgBuilder.append(" at ")
						.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(DateTimeUtil.READABLE_DATE_FORMAT));
		}
		
		return msgBuilder.toString();
	}
	
	public static void appendOccurences (StringBuilder msgBuilder, ReadingRuleContext rule) {
		WorkflowContext workflow = rule.getWorkflow();
		if (workflow != null) {
			ExpressionContext expression = (ExpressionContext) workflow.getExpressions().get(0);
			if (expression.getAggregateCondition() != null && !expression.getAggregateCondition().isEmpty()) {
				msgBuilder.append(" ")
							.append(getInWords(Integer.parseInt(rule.getPercentage())));
				if (expression.getLimit() != null) {
					msgBuilder.append(" consecutively");
				}
			}
		}
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	public static void appendSimpleMsg(StringBuilder msgBuilder, NumberOperators operator, ReadingRuleContext rule, ReadingContext reading) {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was ");
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't ");
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went below ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("exceeded ");
				break;
		}
		
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
			msgBuilder.append("previous value (")
						.append(DECIMAL_FORMAT.format(reading.getReading(rule.getReadingField().getName())))
						.append(")");
		}
		else {
			msgBuilder.append(value);
		}
		appendUnit(msgBuilder, rule);
	}
	
	public static void appendBaseLineMsg (StringBuilder msgBuilder, NumberOperators operator, ReadingRuleContext rule) throws Exception {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was along ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't along ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				msgBuilder.append("lower than ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				msgBuilder.append("higher than ");
				break;
		}
		
		BaseLineContext bl = BaseLineAPI.getBaseLine(rule.getBaselineId());
		msgBuilder.append("the ");
		msgBuilder.append("base line ")
					.append("'")
					.append(bl.getName())
					.append("'");
	}
	
	private static String getInWords (int val) {
		switch (val) {
			case 1:
				return "once";
			case 2:
				return "twice";
			case 3:
				return "thrice";
			default:
				return val+" times";
		}
	}
	
	public static void appendFlappingMsg (StringBuilder msgBuilder, ReadingRuleContext rule) {
		msgBuilder.append("flapped ")
					.append(getInWords(rule.getFlapFrequency()));
		
		switch (rule.getReadingField().getDataTypeEnum()) {
			case NUMBER:
			case DECIMAL:
				msgBuilder.append(" below ")
							.append(rule.getMinFlapValue());
				appendUnit(msgBuilder, rule);
				msgBuilder.append(" and beyond ")
							.append(rule.getMaxFlapValue());
				appendUnit(msgBuilder, rule);
				break;
			default:
				break;
		}
	}
	
	private static Object formatValue (ReadingContext reading, FacilioField field) {
		if (field.getDataTypeEnum() == FieldType.DECIMAL) {
			return DECIMAL_FORMAT.format(FacilioUtil.castOrParseValueAsPerType(field, reading.getReading(field.getName())));
		}
		else {
			return reading.getReading(field.getName());
		}
	}
	
	private static void appendAdvancedMsg (StringBuilder msgBuilder, ReadingRuleContext rule, ReadingContext reading) {
		msgBuilder.append("recorded ")
					.append(formatValue(reading, rule.getReadingField()));
		
		appendUnit(msgBuilder, rule);
		
		msgBuilder.append(" when the complex condition set in '")
					.append(rule.getName())
					.append("'")
					.append(" rule evaluated to true");
	}
	
	private static void appendFunctionMsg (StringBuilder msgBuilder, ReadingRuleContext rule, ReadingContext reading) {
		msgBuilder.append("recorded ")
					.append(formatValue(reading, rule.getReadingField()));
		appendUnit(msgBuilder, rule);
		
		String functionName = null;
		if (rule.getWorkflow() != null) {
			ExpressionContext expr = (ExpressionContext) rule.getWorkflow().getExpressions().get(1);
			functionName = expr.getDefaultFunctionContext().getFunctionName();
		}
		
		msgBuilder.append(" when the function (")
					.append(functionName)
					.append(") set in '")
					.append(rule.getName())
					.append("'")
					.append(" rule evaluated to true");
	}
	
	private static void appendUnit(StringBuilder msgBuilder, ReadingRuleContext rule) {
		if (rule.getReadingField() instanceof NumberField && ((NumberField)rule.getReadingField()).getUnit() != null) {
			msgBuilder.append(((NumberField)rule.getReadingField()).getUnit());
		}
	}
	
	private static void updatePercentage(String percentage, StringBuilder msgBuilder) {
		if (percentage != null && !percentage.equals("0")) {
			msgBuilder.append(percentage)
						.append("% ");
		}
	}
	
	public static AlarmContext getAlarm(long id) throws Exception {
		String moduleName = FacilioConstants.ContextNames.ALARM;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
													.module(module)
													.beanClass(AlarmContext.class)
													.select(fields)
													.andCondition(CriteriaAPI.getIdCondition(id, module))
													;
		
		List<AlarmContext> alarms = builder.get();
		
		if (alarms != null && !alarms.isEmpty()) {
			return alarms.get(0);
		}
		return null;
	}
	
	public static List<AlarmContext> getAlarms(Collection<Long> ids) throws Exception {
		
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		
		String moduleName = FacilioConstants.ContextNames.ALARM;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
													.module(module)
													.beanClass(AlarmContext.class)
													.select(fields)
													.andCondition(CriteriaAPI.getIdCondition(ids, module));
													;
		
		List<AlarmContext> alarms = builder.get();
		
		return alarms;
	}
	
	public static List<AlarmContext> getActiveAlarmsFromWoId (Collection<Long> ids) throws Exception {
		String moduleName = FacilioConstants.ContextNames.ALARM;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField woIdField = fieldMap.get("woId");
		FacilioField severityField = fieldMap.get("severity");
		AlarmSeverityContext clearSeverity = getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
													.module(module)
													.beanClass(AlarmContext.class)
													.select(fields)
													.andCondition(CriteriaAPI.getCondition(woIdField, ids, PickListOperators.IS))
													.andCondition(CriteriaAPI.getCondition(severityField, String.valueOf(clearSeverity.getId()), PickListOperators.ISN_T))
													;
		
		List<AlarmContext> alarms = builder.get();
		return alarms;
	}
	
	public static AlarmSeverityContext getMaxSeverity(List<AlarmContext> alarms) throws Exception {
		
		if (alarms != null && !alarms.isEmpty()) {
			List<Long> severityList = new ArrayList<>();
			
			for(AlarmContext alarm :alarms) {
				severityList.add(alarm.getSeverity().getId());
			}
			
			Map<Integer,AlarmSeverityContext> alarmSeverityMap = new HashMap<>();
			Map<Long, AlarmSeverityContext> severities = getAlarmSeverityMap(severityList);
			
			if(severities != null) {
				for(Long key :severities.keySet()) {
					AlarmSeverityContext sev = severities.get(key);
					alarmSeverityMap.put(sev.getCardinality(), sev);
				}
				
				List<Integer> cardinalityList = new ArrayList<>(alarmSeverityMap.keySet());
				Collections.sort(cardinalityList);
				
				if(cardinalityList != null && !cardinalityList.isEmpty()) {
					return alarmSeverityMap.get(cardinalityList.get(0));
				}
			}
		}
		return getAlarmSeverity("Clear");
	}
	
	public static int updateWoIdInAlarm(long woId, long alarmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM);
		
		AlarmContext alarmContext = new AlarmContext();
		alarmContext.setWoId(woId);
		alarmContext.setIsWoCreated(true);
		
		UpdateRecordBuilder<AlarmContext> updateBuilder = new UpdateRecordBuilder<AlarmContext>()
				.module(module)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.ALARM))
				.andCondition(CriteriaAPI.getIdCondition(alarmId, module));
		
		return updateBuilder.update(alarmContext);
	}
	
	public static String getAlarmModuleName(SourceType type) throws Exception {
		
		if (type == null) {
			return FacilioConstants.ContextNames.ALARM;
		}
		
		switch (type) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
				return FacilioConstants.ContextNames.READING_ALARM;
			case ML_ALARM:
				return FacilioConstants.ContextNames.ML_ALARM;
			default:
				return FacilioConstants.ContextNames.ALARM;
		}
	}
	
	public static List<FacilioField> getAlarmFields(SourceType type) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if (type == null) {
			return bean.getAllFields(FacilioConstants.ContextNames.ALARM);
		}
		
		switch (type) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
				return bean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
			case ML_ALARM:
				return bean.getAllFields(FacilioConstants.ContextNames.ML_ALARM);
			default:
				return bean.getAllFields(FacilioConstants.ContextNames.ALARM);
		}
	}
	
	public static Class<? extends AlarmContext> getAlarmClass(SourceType type) {
		if (type == null) {
			return AlarmContext.class;
		}
		
		switch (type) {
			case THRESHOLD_ALARM:
			case ANOMALY_ALARM:
				return ReadingAlarmContext.class;
			case ML_ALARM:
				return MLAlarmContext.class;
			default:
				return AlarmContext.class;
		}
	}
	
	public static List<AlarmContext> getAlarms(Long assetId) throws Exception {
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			FacilioChain alarmListChain = ReadOnlyChainFactory.getV2AlarmListChain();
			
	 		FacilioContext context = alarmListChain.getContext();
	 		context.put(FacilioConstants.ContextNames.CV_NAME, "active");
	 		context.put(ContextNames.MODULE_NAME, "newreadingalarm");
	 		String filters = "{\"resource\":[{\"operatorId\":36,\"value\":[\""+String.valueOf(assetId)+"\"]}]}";
	 		JSONParser parser = new JSONParser();
	 		JSONObject filter = (JSONObject) parser.parse(filters);
	 		context.put(FacilioConstants.ContextNames.FILTERS, filter);
	 		JSONObject sorting = new JSONObject();
			sorting.put("orderBy", "lastOccurredTime");
			sorting.put("orderType", "desc");
	 		context.put(FacilioConstants.ContextNames.SORTING, sorting);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
			alarmListChain.execute();
			
			return (List<AlarmContext>)context.get(ContextNames.RECORD_LIST);
		}else{
			FacilioChain chain = ReadOnlyChainFactory.getAlarmListChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.CV_NAME, "active");
			String filters = "{\"resource\":[{\"operatorId\":36,\"value\":[\""+String.valueOf(assetId)+"\"]}]}";
	 		JSONParser parser = new JSONParser();
	 		JSONObject filter = (JSONObject) parser.parse(filters);
	 		context.put(FacilioConstants.ContextNames.FILTERS, filter);
			JSONObject sorting = new JSONObject();
 			sorting.put("orderBy", "modifiedTime");
 			sorting.put("orderType", "desc");
	 		context.put(FacilioConstants.ContextNames.SORTING, sorting);
	 		context.put(FacilioConstants.ContextNames.ALARM_ENTITY_ID, -1L);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
	 		
			chain.execute();
			
			return (List<AlarmContext>) chain.getContext().get(FacilioConstants.ContextNames.ALARM_LIST);
		}
		 
	 }
	
	public static SensorRollUpAlarmContext getSensorAlarms(long alarmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<SensorRollUpAlarmContext> selectBuilder = new SelectRecordsBuilder<SensorRollUpAlarmContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM))
																		.moduleName(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM)
																		.beanClass(SensorRollUpAlarmContext.class)	
																		.andCondition(CriteriaAPI.getIdCondition(alarmId, ModuleFactory.getSensorRollUpAlarmModule()))
																		;
	

		List<SensorRollUpAlarmContext> alarms = selectBuilder.get();
		// TODO Auto-generated method stub
		return alarms.get(0);
	}

	public static List<SensorAlarmContext> getSensorChildAlarms(SensorRollUpAlarmContext alarm, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<SensorAlarmContext> selectBuilder = new SelectRecordsBuilder<SensorAlarmContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ALARM))
																		.moduleName(FacilioConstants.ContextNames.SENSOR_ALARM)
																		.beanClass(SensorAlarmContext.class)
																		;
		if (alarm.getResource().getId() > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(alarm.getResource().getId()), NumberOperators.EQUALS));
		}
		if (alarm.getReadingFieldId() > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), String.valueOf(alarm.getReadingFieldId()), NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("IS_METER_ROLL_UP", "meterRollUp", Boolean.FALSE.toString(), BooleanOperators.IS));
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("IS_METER_ROLL_UP", "meterRollUp", Boolean.TRUE.toString(), BooleanOperators.IS));
		
		}

		List<SensorAlarmContext> alarms = selectBuilder.get();
		return alarms;
	}

	public static BigDecimal getOccurencesDuration(long id, DateRange range) throws Exception {
		AlarmOccurrenceContext.Type alarmOccurrenceType = AlarmOccurrenceContext.Type.SENSOR;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType));
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		FacilioField removeField = fieldMap.get("duration");
		List<FacilioField> filteredFields = allFields;
		filteredFields.remove(removeField);
		
		
	
		SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.aggregate(NumberAggregateOperator.SUM, fieldMap.get("duration"))
//				.select(filteredFields)
				.module(module)
				.beanClass(NewAlarmAPI.getOccurrenceClass(alarmOccurrenceType))
				.moduleName(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType))
				;
		
		
		selectbuilder.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" + id, NumberOperators.EQUALS));
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + range.getEndTime(), NumberOperators.LESS_THAN_EQUAL));
		
		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + range.getStartTime(), NumberOperators.GREATER_THAN_EQUAL));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);
		
		
		selectbuilder.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectbuilder.getAsProps();
		
		BigDecimal duration = null;
		
		if (props != null && !props.isEmpty() ) {
			duration = (BigDecimal) props.get(0).get("duration");
		}
		
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(allFields)
				.module(module)
				.beanClass(NewAlarmAPI.getOccurrenceClass(alarmOccurrenceType))
				.moduleName(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" + id, NumberOperators.EQUALS))
				;

		
		selectBuilder.andCriteria(criteria);
		selectBuilder.orderBy("CREATED_TIME");
		selectBuilder.limit(1);
		
		List<AlarmOccurrenceContext> prop = selectBuilder.get();
		
		if(prop != null && !prop.isEmpty()) {
			AlarmOccurrenceContext occurrence = (AlarmOccurrenceContext) prop.get(0);
			if (occurrence.getCreatedTime() < range.getStartTime()) {
				long differenceInDuration = (occurrence.getClearedTime() - range.getStartTime());
				long inSeconds = (differenceInDuration/1000);
				BigDecimal bigD = new BigDecimal(inSeconds);
				bigD =  BigDecimal.valueOf(inSeconds);
				if (duration != null) {
					duration = duration.add(bigD);
				}else {
					duration = bigD;
				}
			}
		}	
		
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilderLastRecord = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(allFields)
				.module(module)
				.beanClass(NewAlarmAPI.getOccurrenceClass(alarmOccurrenceType))
				.moduleName(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" + id, NumberOperators.EQUALS))
				;		
		selectBuilderLastRecord.andCriteria(criteria);	
		selectBuilderLastRecord.orderBy("CREATED_TIME " + "DESC");
		selectBuilderLastRecord.limit(1);
		
		
		
		List<AlarmOccurrenceContext> propLastRecord = selectBuilderLastRecord.get();
		
		if(propLastRecord != null && !propLastRecord.isEmpty()) {
			AlarmOccurrenceContext occurrence = (AlarmOccurrenceContext) prop.get(0);
			if (occurrence.getCreatedTime() > range.getStartTime() && occurrence.getClearedTime() == -1) {
				long differenceInDuration = (occurrence.getCurrentTime() - occurrence.getCreatedTime());
				long inSeconds = (differenceInDuration/1000);
				BigDecimal bigD = new BigDecimal(inSeconds);
				bigD =  BigDecimal.valueOf(inSeconds);
				if (duration != null) {
					duration = duration.add(bigD);
				}else {
					duration = bigD;
				}
				
			}
		}
		
		
		
		return duration;
	}

	public static BigDecimal getOccurencesAverageFrequencyFailure(long id, DateRange range) throws Exception {
		AlarmOccurrenceContext.Type alarmOccurrenceType = AlarmOccurrenceContext.Type.SENSOR;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType));
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		FacilioField removeField = fieldMap.get("timeBetweeenOccurrence");
		List<FacilioField> filteredFields = allFields;
		filteredFields.remove(removeField);
	
		SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
//				.select(filteredFields)
				.module(module)
				.beanClass(NewAlarmAPI.getOccurrenceClass(alarmOccurrenceType))
				.moduleName(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" + id, NumberOperators.EQUALS))
				;
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + range.getEndTime(), NumberOperators.LESS_THAN_EQUAL));
		
		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + range.getStartTime(), NumberOperators.GREATER_THAN_EQUAL));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);
		
		
		selectbuilder.andCriteria(criteria);
		selectbuilder.aggregate(NumberAggregateOperator.AVERAGE, fieldMap.get("timeBetweeenOccurrence"));
		
		List<Map<String, Object>> props = selectbuilder.getAsProps();
		
		BigDecimal timeBetweeenOccurrence = null;
		
		if (props != null && !props.isEmpty() ) {
			timeBetweeenOccurrence = (BigDecimal) props.get(0).get("timeBetweeenOccurrence");
		}
		
		return  timeBetweeenOccurrence;
	}
	
}
