package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BMSAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLAnomalyAlarm;
import com.facilio.bmsconsole.context.RCAAlarm;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingAlarmCategoryContext;
import com.facilio.bmsconsole.context.ReadingAlarmOccurrenceContext;
import com.facilio.bmsconsole.context.ReadingRCAAlarm;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class NewAlarmAPI {

	private static final Logger LOGGER = Logger.getLogger(NewAlarmAPI.class.getName());

	public static List<BaseAlarmContext> getAlarms(List<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.EMPTY_LIST;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
		SelectRecordsBuilder<BaseAlarmContext> builder = new SelectRecordsBuilder<BaseAlarmContext>().module(module)
				.beanClass(BaseAlarmContext.class).select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		List<BaseAlarmContext> list = builder.get();
		return getExtendedAlarms(list);
	}

	public static List<BaseAlarmContext> getExtendedAlarms(List<BaseAlarmContext> list) throws Exception {
		List<BaseAlarmContext> baseAlarms = new ArrayList<>();
		if (CollectionUtils.isEmpty(list)) {
			return baseAlarms;
		}

		Map<Type, List<Long>> alarmMap = new HashMap<>();
		for (BaseAlarmContext map : list) {
			Type type = Type.valueOf(map.getType());
			List<Long> alarmIds = alarmMap.get(type);
			if (CollectionUtils.isEmpty(alarmIds)) {
				alarmIds = new ArrayList<>();
				alarmMap.put(type, alarmIds);
			}
			alarmIds.add(map.getId());
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (Entry<Type, List<Long>> entry : alarmMap.entrySet()) {
			Type type = entry.getKey();
			FacilioModule module = modBean.getModule(getAlarmModuleName(type));
			SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<BaseAlarmContext>()
					.moduleName(getAlarmModuleName(type)).beanClass(getAlarmClass(type))
					.select(modBean.getAllFields(getAlarmModuleName(type)))
					.andCondition(CriteriaAPI.getIdCondition(entry.getValue(), module));
			List<LookupField> lookupFields = getLookupFields(type);
			if (CollectionUtils.isNotEmpty(lookupFields)) {
				selectBuilder.fetchLookups(lookupFields);
			}
			List<BaseAlarmContext> alarmList = selectBuilder.get();
			baseAlarms.addAll(alarmList);
		}

		return baseAlarms;
	}

	public static BaseAlarmContext getAlarm(long id) throws Exception {
		List<BaseAlarmContext> alarms = getAlarms(Collections.singletonList(id));
		if (CollectionUtils.isNotEmpty(alarms)) {
			return alarms.get(0);
		}
		return null;
	}

	public static Class getOccurrenceClass(AlarmOccurrenceContext.Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid type");
		}
		switch (type) {
			case NORMAL:
				return AlarmOccurrenceContext.class;
			case ANOMALY:
				return MLAlarmOccurenceContext.class;
			case READING:
				return ReadingAlarmOccurrenceContext.class;
			default:
				throw new IllegalArgumentException("Invalid type");
		}
	}

	public static String getOccurrenceModuleName(AlarmOccurrenceContext.Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid type");
		}
		switch (type) {
			case NORMAL:
				return FacilioConstants.ContextNames.ALARM_OCCURRENCE;
			case ANOMALY:
				return FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE;
			case READING:
				return FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE;
			default:
				throw new IllegalArgumentException("Invalid type");
		}
	}

	private static Class getAlarmClass(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return ReadingAlarm.class;
			case ML_ANOMALY_ALARM:
				return MLAnomalyAlarm.class;
			case RCA_ALARM:
				return RCAAlarm.class;
			case READING_RCA_ALARM:
				return ReadingRCAAlarm.class;
			case BMS_ALARM:
				return BMSAlarmContext.class;

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	private static List<LookupField> getLookupFields(Type type) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<LookupField> lookupFields = null;
		switch (type) {
			case READING_ALARM:
				lookupFields = new ArrayList<>();
				FacilioField rule = modBean.getField("rule", FacilioConstants.ContextNames.NEW_READING_ALARM);
				if (rule instanceof LookupField) {
					lookupFields.add((LookupField) rule);
				}
		}
		return lookupFields;
	}

	public static String getAlarmModuleName(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return "newreadingalarm";
			case ML_ANOMALY_ALARM:
				return "mlanomalyalarm";
			case RCA_ALARM:
				return "rcaalarm";
			case READING_RCA_ALARM:
				return "readingrcaalarm";
			case BMS_ALARM:
				return "bmsalarm";

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static AlarmOccurrenceContext getLatestAlarmOccurance(BaseAlarmContext baseAlarm) throws Exception {
		if (baseAlarm == null) {
			return null;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class).moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(fields).andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm",
						String.valueOf(baseAlarm.getId()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC, ID DESC").limit(1);
		AlarmOccurrenceContext alarmOccurrenceContext = builder.fetchFirst();
		List<AlarmOccurrenceContext> extendedOccurrences = getExtendedOccurrence(Collections.singletonList(alarmOccurrenceContext));
		if (CollectionUtils.isNotEmpty(extendedOccurrences)) {
			alarmOccurrenceContext = extendedOccurrences.get(0);
			updateAlarmObject(Collections.singletonList(alarmOccurrenceContext));
			return alarmOccurrenceContext;
		}
		return null;
	}

	public static Map<Long, AlarmOccurrenceContext> getLatestAlarmOccuranceMap(List<BaseAlarmContext> baseAlarms) throws Exception {
		if (baseAlarms == null) {
			return null;
		}

		List<Long> ids = baseAlarms.stream().map(alarm -> alarm.getId()).collect(Collectors.toList());

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class).moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), ids, NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC, ID DESC");
		List<AlarmOccurrenceContext> occurences = builder.get();
		occurences = getExtendedOccurrence(occurences);
//		updateAlarmObject(occurences);
		if (CollectionUtils.isNotEmpty(occurences)) {
			return occurences.stream().collect(Collectors.toMap(AlarmOccurrenceContext::getId, Function.identity()));
		}
		return null;
	}

	public static List<AlarmOccurrenceContext> getLatestAlarmOccurance(List<String> messageKeys) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<BaseAlarmContext>()
				.beanClass(BaseAlarmContext.class)
				.moduleName(FacilioConstants.ContextNames.BASE_ALARM)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.BASE_ALARM))
				.andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", StringUtils.join(messageKeys, ','),
						StringOperators.IS));
		List<BaseAlarmContext> baseAlarmContexts = selectBuilder.get();

		List<Long> latestOccurrenceId = new ArrayList<>();
		for (BaseAlarmContext baseAlarmContext : baseAlarmContexts) {
			latestOccurrenceId.add(baseAlarmContext.getLastOccurrence().getId());
		}

		List<AlarmOccurrenceContext> alarmOccurrences = getAlarmOccurrences(latestOccurrenceId);
		return alarmOccurrences;
	}

	public static AlarmOccurrenceContext getLatestAlarmOccurance(String messageKey) throws Exception {
		List<AlarmOccurrenceContext> list = getLatestAlarmOccurance(Collections.singletonList(messageKey));
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public static AlarmOccurrenceContext getActiveAlarmOccurance(String messageKey, Type alarmType) throws Exception {
		AlarmOccurrenceContext alarmOccurance = getLatestAlarmOccurance(messageKey);
		if (alarmOccurance != null) {
			AlarmSeverityContext clearAlarmSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
			if (!alarmOccurance.getSeverity().equals(clearAlarmSeverity)) {
				updateAlarmObject(Collections.singletonList(alarmOccurance));
				return alarmOccurance;
			}
		}
		return null;
	}

	public static AlarmOccurrenceContext createAlarm(BaseEventContext baseEvent, Context context) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = baseEvent.updateAlarmOccurrenceContext(null, context, true);

		BaseAlarmContext baseAlarm = baseEvent.updateAlarmContext(null, true);
		updateAlarmSystemFields(baseAlarm, alarmOccurrence);

		baseAlarm.setSeverity(alarmOccurrence.getSeverity());
		baseAlarm.setType(baseEvent.getEventTypeEnum());

		alarmOccurrence.setAlarm(baseAlarm);

		addAlarmOccurrence(alarmOccurrence, baseEvent, true);
		return alarmOccurrence;
	}

	public static AlarmOccurrenceContext createAlarmOccurrence(BaseAlarmContext baseAlarm, BaseEventContext baseEvent,
															   boolean mostRecent, Context context) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = baseEvent.updateAlarmOccurrenceContext(null, context, true);
		alarmOccurrence.setAlarm(baseAlarm);

		addAlarmOccurrence(alarmOccurrence, baseEvent, mostRecent);
		return alarmOccurrence;
	}

	private static void addAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent,
										   boolean mostRecent) throws Exception {
		rollUpAlarm(alarmOccurrence, baseEvent, mostRecent);
	}

	public static void updateAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent,
											 boolean mostRecent, Context context) throws Exception {
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, context, false);

		rollUpAlarm(alarmOccurrence, baseEvent, mostRecent);
	}

	private static void rollUpAlarm(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent,
									boolean mostRecent) throws Exception {
		if (!mostRecent) {
			return;
		}
		// roll-up alarm object
		BaseAlarmContext baseAlarm = alarmOccurrence.getAlarm();
		baseEvent.updateAlarmContext(baseAlarm, false);
		alarmOccurrence.updateAlarm(baseAlarm);

		updateAlarmSystemFields(baseAlarm, alarmOccurrence);
	}

	private static void updateAlarmSystemFields(BaseAlarmContext baseAlarm, AlarmOccurrenceContext alarmOccurrence)
			throws Exception {
		baseAlarm.setLastOccurredTime(alarmOccurrence.getLastOccurredTime());
		baseAlarm.setLastCreatedTime(alarmOccurrence.getCreatedTime());
		if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
			baseAlarm.setLastClearedTime(alarmOccurrence.getLastOccurredTime());
		}
	}

	public static void loadAlarmLookups(List<BaseAlarmContext> alarms) throws Exception {
		updateResource(alarms);
	}

	private static void updateResource(List<BaseAlarmContext> alarms) throws Exception {
		if (CollectionUtils.isNotEmpty(alarms)) {
			List<Long> resourceIds = alarms.stream().filter(alarm -> alarm != null && alarm.getResource() != null)
					.map(alarm -> alarm.getResource().getId()).collect(Collectors.toList());
			Map<Long, ResourceContext> extendedResources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds,
					false);
			for (BaseAlarmContext alarm : alarms) {
				if (alarm != null) {
					ResourceContext resource = alarm.getResource();
					if (resource != null) {
						alarm.setResource(extendedResources.get(resource.getId()));
					}
				}
			}
		}
	}

	public static List<AlarmOccurrenceContext> getAlarmOccurrences(List<Long> recordIds) throws Exception {
		if (CollectionUtils.isEmpty(recordIds)) {
			return new ArrayList<>();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		List<LookupField> fetchLookupFields = new ArrayList<>();
		fetchLookupFields.add((LookupField) fieldMap.get("severity"));
		fetchLookupFields.add((LookupField) fieldMap.get("previousSeverity"));
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.module(module)
				.select(allFields)
				.fetchLookups(fetchLookupFields)
				.beanClass(AlarmOccurrenceContext.class).andCondition(CriteriaAPI.getIdCondition(recordIds, module));
		List<AlarmOccurrenceContext> occurrenceContexts = builder.get();
		occurrenceContexts = getExtendedOccurrence(occurrenceContexts);
		updateAlarmObject(occurrenceContexts);
		return occurrenceContexts;
	}

	private static List<AlarmOccurrenceContext> getExtendedOccurrence(List<AlarmOccurrenceContext> occurrenceContexts) throws Exception {
		List<AlarmOccurrenceContext> newList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(occurrenceContexts)) {
			HashMap<AlarmOccurrenceContext.Type, List<Long>> occurrenceMap = new HashMap<>();

			for (AlarmOccurrenceContext occurrenceContext : occurrenceContexts) {
				if (occurrenceContext.getTypeEnum() == AlarmOccurrenceContext.Type.NORMAL) {
					newList.add(occurrenceContext);
					continue;
				}

				List<Long> list = occurrenceMap.get(occurrenceContext.getTypeEnum());
				if (list == null) {
					list = new ArrayList<>();
					occurrenceMap.put(occurrenceContext.getTypeEnum(), list);
				}
				list.add(occurrenceContext.getId());
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (AlarmOccurrenceContext.Type type : occurrenceMap.keySet()) {
				FacilioModule module = modBean.getModule(getOccurrenceModuleName(type));
				SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
						.moduleName(getOccurrenceModuleName(type)).beanClass(getOccurrenceClass(type))
						.select(modBean.getAllFields(getOccurrenceModuleName(type)))
						.andCondition(CriteriaAPI.getIdCondition(occurrenceMap.get(type), module));
				newList.addAll(selectBuilder.get());
			}
		}
		return newList;
	}

	private static void updateAlarmObject(List<AlarmOccurrenceContext> occurrenceContexts) throws Exception {
		if (CollectionUtils.isNotEmpty(occurrenceContexts)) {
			List<Long> alarmIds = new ArrayList<>();
			for (AlarmOccurrenceContext alarmOccurrence: occurrenceContexts) {
				alarmIds.add(alarmOccurrence.getAlarm().getId());
			}
			Map<Long, BaseAlarmContext> alarmMap = FieldUtil.getAsMap(getAlarms(alarmIds));
			for (AlarmOccurrenceContext alarmOccurrence: occurrenceContexts) {
				alarmOccurrence.setAlarm(alarmMap.get(alarmOccurrence.getAlarm().getId()));
			}
		}
	}

	public static AlarmOccurrenceContext getAlarmOccurrence(long recordId) throws Exception {
		List<AlarmOccurrenceContext> alarmOccurrences = getAlarmOccurrences(Collections.singletonList(recordId));
		if (CollectionUtils.isNotEmpty(alarmOccurrences)) {
			updateAlarmObject(Collections.singletonList(alarmOccurrences.get(0)));
			return alarmOccurrences.get(0);
		}
		return null;
	}

	public static List<AlarmOccurrenceContext> getReadingAlarmOccurrences(List<Long> resourceId, Long ruleId, long fieldId, long startTime, long endTime, Long alarmId, Long parentAlarmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = getAlarmBuilder(startTime, endTime, fields, fieldMap);
		boolean ruleAvailable = false;
		if (CollectionUtils.isNotEmpty(resourceId)) {
			ruleAvailable = true;
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId, PickListOperators.IS));
		}

		if (ruleId != null && ruleId != -1) {
			FacilioModule readingAlarmModule = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
			selectBuilder
					.innerJoin(readingAlarmModule.getTableName())
					.on("AlarmOccurrence.ALARM_ID = " + readingAlarmModule.getTableName() + ".ID");
			Map<String, FacilioField> readingAlarmFieldMap = FieldFactory.getAsMap(modBean.getAllFields(readingAlarmModule.getName()));
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingAlarmFieldMap.get("rule"), String.valueOf(ruleId), PickListOperators.IS));
			ruleAvailable = true;
		}
		if (parentAlarmId != null && parentAlarmId != -1) {
			FacilioModule anomalyOccurrence = modBean.getModule(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
			Map<String, FacilioField> anomalyOccurrenceFields = FieldFactory.getAsMap(modBean.getAllFields(anomalyOccurrence.getName()));
			selectBuilder
				.innerJoin(anomalyOccurrence.getTableName())
				.on("AlarmOccurrence.ID = " + anomalyOccurrence.getTableName() + ".ID");
			selectBuilder.andCondition(CriteriaAPI.getCondition(anomalyOccurrenceFields.get("parentAlarm"), String.valueOf(parentAlarmId), PickListOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition(anomalyOccurrenceFields.get("mlanomalyType"),
					String.valueOf(MLAlarmOccurenceContext.MLAnomalyType.RCA.getIndex()), NumberOperators.EQUALS));
		}
		if (alarmId != null) {
			FacilioModule alarmOccurence = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			Map<String, FacilioField> alarmOccurenceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(alarmOccurence.getName()));
			selectBuilder.andCondition(CriteriaAPI.getCondition(alarmOccurenceFieldMap.get("alarm"), String.valueOf(alarmId), PickListOperators.IS));
			ruleAvailable = true;
			
		}
//		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(resourceId)) {
//			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId, PickListOperators.IS));
//		}
		if (!ruleAvailable) {
			throw new IllegalArgumentException("Resource Id or Rule Id should be available");
		}

		if(fieldId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), String.valueOf(fieldId), NumberOperators.EQUALS));
		}
		List<AlarmOccurrenceContext> occurrenceContexts = selectBuilder.get();
		occurrenceContexts = getExtendedOccurrence(occurrenceContexts);
		updateAlarmObject(occurrenceContexts);
		return occurrenceContexts;
	}

	public static List<AlarmOccurrenceContext> getReadingAlarmOccurrences(long entityId, long startTime, long endTime)
			throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = getAlarmBuilder(startTime, endTime, fields,
				fieldMap)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(entityId),
						NumberOperators.EQUALS));

		List<AlarmOccurrenceContext> occurrenceContexts = selectBuilder.get();
		occurrenceContexts = getExtendedOccurrence(occurrenceContexts);
		updateAlarmObject(occurrenceContexts);
		return occurrenceContexts;
	}
	
	public static SelectRecordsBuilder<AlarmOccurrenceContext> getAlarmBuilder(long alarmId, long startTime, long endTime,
			List<FacilioField> fields, Map<String, FacilioField> fieldMap) {
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = getAlarmBuilder(startTime, endTime, fields, fieldMap);
		builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS));
		return builder;
	}

	public static SelectRecordsBuilder<AlarmOccurrenceContext> getAlarmBuilder(long startTime, long endTime,
																				List<FacilioField> fields, Map<String, FacilioField> fieldMap) {
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(fields).moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.beanClass(AlarmOccurrenceContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(endTime),
						NumberOperators.LESS_THAN_EQUAL))
				.orderBy("CREATED_TIME");

		Condition condition1 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), String.valueOf(startTime),
				NumberOperators.GREATER_THAN_EQUAL);
		Condition condition2 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), CommonOperators.IS_EMPTY);

		Criteria criteria = new Criteria();

		criteria.addOrCondition(condition1);
		criteria.addOrCondition(condition2);

		selectBuilder.andCriteria(criteria);
		return selectBuilder;
	}

	public static Long getActiveAlarms(long spaceId) throws Exception
	{
		FacilioField countFld = new FacilioField();
		countFld.setName("active");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("BaseAlarm")
				.innerJoin("Alarm_Severity")
				.on("BaseAlarm.SEVERITY=Alarm_Severity.ID")
				.andCustomWhere("BaseAlarm.ORGID=?",orgId)
				.andCustomWhere("Alarm_Severity.SEVERITY != ?",FacilioConstants.Alarm.CLEAR_SEVERITY)
				//.andCondition(getSpaceCondition(spaceId));
				.andCondition(spaceCond);
		List<Map<String, Object>> rs = builder.get();
		if(rs.isEmpty()) {
			return 0L;
		}
		
		return ((Number) rs.get(0).get("active")).longValue();
		
	}

	public static ReadingAlarmCategoryContext getReadingAlarmCategory(long resourceId) throws Exception {
		if (resourceId > 0) {
			ResourceContext resource = ResourceAPI.getExtendedResource(resourceId);
			if (resource != null && resource.getResourceTypeEnum() == ResourceContext.ResourceType.ASSET) {
				AssetContext asset = (AssetContext) resource;
				if (asset.getCategory() != null && asset.getCategory().getId() != -1) {
					return getAlarmTypeFromAssetCategory(asset.getCategory().getId());
				}
			}
		}
		return null;
	}

	public static ReadingAlarmCategoryContext getAlarmTypeFromAssetCategory(long categoryId) throws Exception {
		AssetCategoryContext category = AssetsAPI.getCategoryForAsset(categoryId);

		String name = null;
		switch (category.getTypeEnum()) {
			case ENERGY:
				name = "energy";
				break;
			case HVAC:
				name = "hvac";
				break;
			default:
				return null;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ReadingAlarmCategoryContext> builder = new SelectRecordsBuilder<ReadingAlarmCategoryContext>()
				.module(modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_CATEGORY))
				.select(modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM_CATEGORY))
				.beanClass(ReadingAlarmCategoryContext.class)
				.andCondition(CriteriaAPI.getCondition("NAME", "name", name, StringOperators.IS));
		return builder.fetchFirst();
	}

	public static void deleteIntervalBasedAlarmOccurrences(long ruleId, long startTime, long endTime, long resourceId) throws Exception 
	{	
		Map<Long, AlarmOccurrenceContext> delAlarmOccurrencesMap = new HashMap<Long, AlarmOccurrenceContext>();
		AlarmOccurrenceContext initialEdgeCaseAlarmOccurrence = null, finalEdgeCaseAlarmOccurrence = null;
		
		List<AlarmOccurrenceContext> alarmOccurrenceList = getAllAlarmOccurrences(ruleId, startTime, endTime, resourceId);

		if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
		{
			AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
			BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
			BaseAlarmContext baseAlarmContext = getBaseAlarmById(baseAlarm.getId());
			
			if (alarmOccurrenceList.get(0).getCreatedTime() < startTime) 
			{
				initialEdgeCaseAlarmOccurrence = (alarmOccurrenceList.get(0));
				clearInitialEdgeAlarmOccurenceWithEvents(initialEdgeCaseAlarmOccurrence, startTime);  
			}

			if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime) 
			{
				finalEdgeCaseAlarmOccurrence = (alarmOccurrenceList.get(alarmOccurrenceList.size() - 1));
				clearFinalEdgeAlarmOccurenceWithEvents(finalEdgeCaseAlarmOccurrence,endTime);
			}	
			
			for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceList) 
			{
				if(alarmOccurrence.getAlarm().getId() != baseAlarm.getId() || baseAlarmContext == null)
				{
					throw new Exception ("Different/No Base Alarms in the historical interval which is rule/resource specific");
				}
				
				if (alarmOccurrence.equals(initialEdgeCaseAlarmOccurrence) || alarmOccurrence.equals(finalEdgeCaseAlarmOccurrence)) {
					continue;
				}
				delAlarmOccurrencesMap.put(alarmOccurrence.getId(), alarmOccurrence);
			}
						
			if(MapUtils.isNotEmpty(delAlarmOccurrencesMap))
			{
				changeLatestAlarmOccurrence(new ArrayList<AlarmOccurrenceContext>(delAlarmOccurrencesMap.values()), baseAlarmContext);
				deleteAllAlarmOccurences(new ArrayList<Long>(delAlarmOccurrencesMap.keySet()));
				updateBaseAlarmWithNoOfOccurences(baseAlarm);
			}			
		}	
	}
	
	public static void changeLatestAlarmOccurrence(List<AlarmOccurrenceContext> alarmOccurrenceList, BaseAlarmContext baseAlarmContext) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		AlarmOccurrenceContext firstOccurrence = alarmOccurrenceList.get(0);
		
		for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceList) 
		{
			if(alarmOccurrence.getId() == baseAlarmContext.getLastOccurrence().getId())
			{
				SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
						.select(modBean.getAllFields(module.getName()))
						.module(modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE))
						.beanClass(AlarmOccurrenceContext.class)
						.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + firstOccurrence.getCreatedTime(), NumberOperators.LESS_THAN))
						.orderBy("CREATED_TIME DESC").limit(1);
				AlarmOccurrenceContext newLatestAlarmOccurrence =  selectbuilder.fetchFirst();		
	
				if (newLatestAlarmOccurrence != null) 
				{
					baseAlarmContext.setLastOccurrence(newLatestAlarmOccurrence);
					updateBaseAlarmBuilder(baseAlarmContext);
				}	
			}
		}
	}

	public static BaseEventContext generateEvent(AlarmOccurrenceContext alarmOccurrence, AlarmSeverityContext severityContext, long createdTime) 
			throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());

		BaseEventContext event = new BaseEventContext();
		event.setSeverity(severityContext);
		event.setSeverityString(severityContext.getSeverity());
		event.setComment("Automated Event");
		event.setEventMessage("Automated Event");
		event.setMessageKey(alarmOccurrence.getAlarm().getKey());
		event.setCreatedTime(createdTime);
		event.setResource(alarmOccurrence.getAlarm().getResource());
		event.setAlarmOccurrence(alarmOccurrence) ;

		InsertRecordBuilder<BaseEventContext> insertBuilder = new InsertRecordBuilder<BaseEventContext>()
				.moduleName(FacilioConstants.ContextNames.BASE_EVENT).fields(allEventFields);

		insertBuilder.addRecord(event);
		insertBuilder.save();	
		
		return event;
	}
	
	public static List<AlarmOccurrenceContext> getAllAlarmOccurrences(long ruleId, long startTime, long endTime, long resourceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
	
		SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class).moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(allFields).innerJoin("ReadingAlarm").on("AlarmOccurrence.ALARM_ID = ReadingAlarm.ID")
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", "" + ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resource", "" + resourceId, NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + endTime, NumberOperators.LESS_THAN));
		
		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + startTime, NumberOperators.GREATER_THAN));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);
		
		selectbuilder.andCriteria(criteria);
		selectbuilder.orderBy("CREATED_TIME");
		
		List<AlarmOccurrenceContext> alarmOccurrenceList = selectbuilder.get();
		
		return alarmOccurrenceList;
	}
	
	public static void updateAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		
		UpdateRecordBuilder<AlarmOccurrenceContext> updateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
				.moduleName(module.getName())
				.andCondition(CriteriaAPI.getIdCondition(alarmOccurrence.getId(), module))
				.fields(allFields);
		updateBuilder.update(alarmOccurrence);
	}
	
	public static void deleteAllAlarmOccurences(List<Long> delAlarmOccurrenceIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);

		GenericDeleteRecordBuilder deletebuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(delAlarmOccurrenceIds, module));
		deletebuilder.delete();

	}
	
	public static void clearInitialEdgeAlarmOccurenceWithEvents(AlarmOccurrenceContext initialEdgeCaseAlarmOccurrence, long startTime) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		LOGGER.info("Initial Alarm Occurance Id --- "+initialEdgeCaseAlarmOccurrence.getId());
		
		generateEvent(initialEdgeCaseAlarmOccurrence, AlarmAPI.getAlarmSeverity("Clear"), startTime - 1);

		initialEdgeCaseAlarmOccurrence.setClearedTime(startTime - 1);
		updateAlarmOccurrence(initialEdgeCaseAlarmOccurrence);
		
		DeleteRecordBuilder<BaseEventContext> deletebuilder = new DeleteRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +initialEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + startTime, NumberOperators.GREATER_THAN_EQUAL));
		deletebuilder.delete();
		
		updateAlarmOccurrenceWithNoOfEvents(initialEdgeCaseAlarmOccurrence);
	}
	
	public static void clearFinalEdgeAlarmOccurenceWithEvents(AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence, long endTime) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		LOGGER.info("Final Alarm Occurance Id --- "+finalEdgeCaseAlarmOccurrence.getId());
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" +endTime, NumberOperators.GREATER_THAN))
				.orderBy("CREATED_TIME").limit(1);
	
		List<BaseEventContext> nextClosestEventList = selectEventbuilder.get();
		if (nextClosestEventList.isEmpty()) 
		{
			BaseEventContext event = generateEvent(finalEdgeCaseAlarmOccurrence, finalEdgeCaseAlarmOccurrence.getSeverity(), endTime + 1);	
			nextClosestEventList.add(event);		
		}
		
		if (nextClosestEventList != null)
		{ 	
			finalEdgeCaseAlarmOccurrence.setCreatedTime(nextClosestEventList.get(0).getCreatedTime());	
			updateAlarmOccurrence(finalEdgeCaseAlarmOccurrence);
		}
	
		DeleteRecordBuilder<BaseEventContext> deletebuilder = new DeleteRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence",""+finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", ""+endTime, NumberOperators.LESS_THAN_EQUAL));
		deletebuilder.delete();
		
		updateAlarmOccurrenceWithNoOfEvents(finalEdgeCaseAlarmOccurrence);	
	}
	
	public static void updateAlarmOccurrenceWithNoOfEvents(AlarmOccurrenceContext edgeCaseAlarmOccurrence) 
			throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());	
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> alarmOccurrenceFields = modBean.getAllFields(module.getName());;
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allEventFields);
		FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("alarmOccurrence"));
		countField.setName("count");
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(countField);
		selectFields.add(fieldMap.get("alarmOccurrence"));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(eventModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence", "" +edgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS));
				
		List<Map<String, Object>> propsList = selectBuilder.get();
				
		for(Map<String, Object> prop :propsList)
		{
			if((long) prop.get("count") > 0)
			{	
				edgeCaseAlarmOccurrence.setNoOfEvents((long) prop.get("count"));
				
				UpdateRecordBuilder<AlarmOccurrenceContext> updateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
						.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
						.andCondition(CriteriaAPI.getIdCondition(edgeCaseAlarmOccurrence.getId(), modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE)))
						.fields(alarmOccurrenceFields);
				updateBuilder.update(edgeCaseAlarmOccurrence);		
			}			
		}
			
	}	
	
	public static void updateBaseAlarmWithNoOfOccurences(BaseAlarmContext baseAlarm) 
			throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());	
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("alarm"));
		countField.setName("count");
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(countField);
		selectFields.add(fieldMap.get("alarm"));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarm.getId(), NumberOperators.EQUALS));
				
		List<Map<String, Object>> propsList = selectBuilder.get();
				
		for(Map<String, Object> prop :propsList)
		{
			if((long) prop.get("count") > 0)
			{	
				baseAlarm.setNoOfOccurrences((long) prop.get("count"));
				updateBaseAlarmBuilder(baseAlarm);
			}
			else if ((long) prop.get("count") == 0)
			{	
				GenericDeleteRecordBuilder deletebuilder = new GenericDeleteRecordBuilder()
						.table(modBean.getModule((FacilioConstants.ContextNames.BASE_ALARM)).getTableName())
						.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule((FacilioConstants.ContextNames.BASE_ALARM))));
				deletebuilder.delete();		
			}			
		}		
	}

	public static BaseAlarmContext getBaseAlarmById(long baseAlarmId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
		
		SelectRecordsBuilder<BaseAlarmContext> selectEventbuilder = new SelectRecordsBuilder<BaseAlarmContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM))
				.beanClass(BaseAlarmContext.class)
				.andCondition(CriteriaAPI.getIdCondition(baseAlarmId, module));
	
		BaseAlarmContext baseAlarmContext = selectEventbuilder.fetchFirst();
		if (baseAlarmContext != null) {			
			return baseAlarmContext;
		}
		return null;	
	}
	
	public static void updateBaseAlarmBuilder(BaseAlarmContext baseAlarm) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> baseAlarmFields = modBean.getAllFields((FacilioConstants.ContextNames.BASE_ALARM));
		
		UpdateRecordBuilder<BaseAlarmContext> updateBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
				.moduleName(FacilioConstants.ContextNames.BASE_ALARM)
				.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(),modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM)))
				.fields(baseAlarmFields);
		updateBuilder.update(baseAlarm);	
	}

	public static List<ReadingAlarmCategoryContext> getReadingAlarmCategory(List<Long> readingCategoryIds) throws Exception {
		if (CollectionUtils.isEmpty(readingCategoryIds)) {
			return new ArrayList<>();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ReadingAlarmCategoryContext> builder = new SelectRecordsBuilder<ReadingAlarmCategoryContext>()
				.module(modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_CATEGORY))
				.select(modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM_CATEGORY))
				.beanClass(ReadingAlarmCategoryContext.class)
				.andCondition(CriteriaAPI.getIdCondition(readingCategoryIds, modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_CATEGORY)));
		return builder.get();
	}
}
