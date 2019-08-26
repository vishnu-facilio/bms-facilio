package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

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
			Type type = Type.valueOf((int) map.getType());
			List<Long> alarmIds = alarmMap.get(type);
			if (CollectionUtils.isEmpty(alarmIds)) {
				alarmIds = new ArrayList<>();
				alarmMap.put(type, alarmIds);
			}
			alarmIds.add((Long) map.getId());
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
		return builder.fetchFirst();
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
		if (CollectionUtils.isNotEmpty(occurences)) {
			return occurences.stream().collect(Collectors.toMap(AlarmOccurrenceContext::getId, Function.identity()));
		}
		return null;
	}

	public static List<AlarmOccurrenceContext> getLatestAlarmOccurance(List<String> messageKeys) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class).moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(fields).innerJoin("BaseAlarm").on("AlarmOccurrence.ALARM_ID = BaseAlarm.ID")
				.andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", StringUtils.join(messageKeys, ','),
						StringOperators.IS))
				.orderBy("CREATED_TIME DESC, ID DESC").limit(1);
		List<AlarmOccurrenceContext> list = builder.get();

		List<Long> alarmIds = new ArrayList<>();
		for (AlarmOccurrenceContext alarmOccurrence : list) {
			alarmIds.add(alarmOccurrence.getAlarm().getId());
		}
		Map<Long, BaseAlarmContext> alarmMap = FieldUtil.getAsMap(getAlarms(alarmIds));
		for (AlarmOccurrenceContext alarmOccurrence : list) {
			alarmOccurrence.setAlarm(alarmMap.get(alarmOccurrence.getAlarm().getId()));
		}
		return list;
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
				return alarmOccurance;
			}
		}
		return null;
	}

	public static AlarmOccurrenceContext createAlarm(BaseEventContext baseEvent, Context context) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, context,true);

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
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, context,true);
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
		long currentTime = DateTimeUtil.getCurrenTime();
		baseAlarm.setLastOccurredTime(currentTime);
		baseAlarm.setLastCreatedTime(alarmOccurrence.getCreatedTime());
		if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
			baseAlarm.setLastClearedTime(currentTime);
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
		return occurrenceContexts;
	}

	public static AlarmOccurrenceContext getAlarmOccurrence(long recordId) throws Exception {
		List<AlarmOccurrenceContext> alarmOccurrences = getAlarmOccurrences(Collections.singletonList(recordId));
		if (CollectionUtils.isNotEmpty(alarmOccurrences)) {
			return alarmOccurrences.get(0);
		}
		return null;
	}

	public static List<AlarmOccurrenceContext> getReadingAlarmOccurrences(List<Long> resourceId, Long ruleId, long fieldId, long startTime, long endTime) throws Exception {
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
//		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(resourceId)) {
//			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId, PickListOperators.IS));
//		}
		if (!ruleAvailable) {
			throw new IllegalArgumentException("Resource Id or Rule Id should be available");
		}

		if(fieldId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), String.valueOf(fieldId), NumberOperators.EQUALS));
		}
		List<AlarmOccurrenceContext> alarms = selectBuilder.get();
		/*if (AccountUtil.getCurrentOrg().getId() == 75) {
			LOGGER.info("Fetched Alarm Query : "+selectBuilder.toString());
		}*/
		return alarms;
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

		return selectBuilder.get();
	}

	private static SelectRecordsBuilder<AlarmOccurrenceContext> getAlarmBuilder(long startTime, long endTime,
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

	public static void deleteIntervalBasedAlarmOccurrences(long ruleId, long startTime, long endTime, long resourceId)
			throws Exception {

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
		criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + startTime, NumberOperators.GREATER_THAN));

		selectbuilder.andCriteria(criteria);
		List<AlarmOccurrenceContext> AlarmOccurrenceList = selectbuilder.get();

		List<Long> delAlarmOccurrenceIds = new ArrayList<Long>();
		AlarmOccurrenceContext initialEdgeCaseAlarmOccurrence = new AlarmOccurrenceContext();
		AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence = new AlarmOccurrenceContext();

		if (AlarmOccurrenceList != null && !AlarmOccurrenceList.isEmpty()){

			if (AlarmOccurrenceList.get(0).getCreatedTime() < startTime) {
				initialEdgeCaseAlarmOccurrence = AlarmOccurrenceList.get(0);
			}
			if (AlarmOccurrenceList.get(AlarmOccurrenceList.size() - 1).getClearedTime() > endTime) {
				finalEdgeCaseAlarmOccurrence = AlarmOccurrenceList.get(AlarmOccurrenceList.size() - 1);
			}

			for (AlarmOccurrenceContext alarmOccurrence : AlarmOccurrenceList) {
				if (alarmOccurrence.equals(initialEdgeCaseAlarmOccurrence) || alarmOccurrence.equals(finalEdgeCaseAlarmOccurrence)) {
					continue;
				}
				delAlarmOccurrenceIds.add((Long) alarmOccurrence.getId());
			}

			deleteAllAlarmOccurences(delAlarmOccurrenceIds);
			
			FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
			List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());

		}	
	}

	public static void deleteAllAlarmOccurences(List<Long> delAlarmOccurrenceIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);

		GenericDeleteRecordBuilder deletebuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(delAlarmOccurrenceIds, module));
		deletebuilder.delete();

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
