package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.time.DateTimeUtil;

public class NewAlarmAPI {

	public static BaseAlarmContext getAlarm(String messageKey, Type type) throws Exception {
		Class contextClass = getAlarmClass(type);
		String moduleName = getAlarmModuleName(type);
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<? extends BaseAlarmContext> builder = new SelectRecordsBuilder<>()
				.beanClass(contextClass)
				.moduleName(moduleName)
				.select(modBean.getAllFields(moduleName))
				.andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", messageKey, StringOperators.IS));
		return (BaseAlarmContext) builder.fetchFirst();
	}
	
	public static List<BaseAlarmContext> getAlarms(List<Long> ids) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
		SelectRecordsBuilder<BaseAlarmContext> builder = new SelectRecordsBuilder<BaseAlarmContext>()
				.module(module)
				.beanClass(BaseAlarmContext.class)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		List<BaseAlarmContext> list = builder.get();
		return getExtendedAlarms(list);
	}
	
	private static List<BaseAlarmContext> getExtendedAlarms(List<BaseAlarmContext> list) throws Exception {
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
			SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<>()
					.moduleName(getAlarmModuleName(type))
					.beanClass(getAlarmClass(type))
					.select(modBean.getAllFields(getAlarmModuleName(type)))
					.andCondition(CriteriaAPI.getIdCondition(entry.getValue(), module));
			baseAlarms.addAll(selectBuilder.get());
		}
		
		
		return baseAlarms;
	}

	private static BaseAlarmContext getAlarm(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(Collections.singletonList(FieldFactory.getField("type", "TYPE", module, FieldType.ENUM)))
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		Map<String, Object> map = builder.fetchFirst();
		if (MapUtils.isNotEmpty(map)) {
			Object typeObject = map.get("type");
			BaseAlarmContext.Type type = null;
			if (typeObject != null && typeObject instanceof Number) {
				type = Type.valueOf(((Number) typeObject).intValue());
			}
			if (type != null) {
				SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<>()
						.moduleName(getAlarmModuleName(type))
						.beanClass(getAlarmClass(type))
						.select(modBean.getAllFields(getAlarmModuleName(type)))
						.andCondition(CriteriaAPI.getIdCondition(id, module));
				return selectBuilder.fetchFirst();
			}
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

		default:
			throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static String getAlarmModuleName(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}
		
		switch (type) {
		case READING_ALARM:
			return "newreadingalarm";

		default:
			throw new IllegalArgumentException("Invalid alarm type");
		}
	}
	
	public static AlarmOccurrenceContext getLatestAlarmOccurance(String messageKey, Type alarmType) throws Exception {
		BaseAlarmContext alarm = getAlarm(messageKey, alarmType);
		if (alarm == null) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class)
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", String.valueOf(alarm.getId()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC")
				.limit(1);
		AlarmOccurrenceContext alarmOccurrence = builder.fetchFirst();
		return alarmOccurrence;
	}

	public static AlarmOccurrenceContext getActiveAlarmOccurance(String messageKey, Type alarmType) throws Exception {
		AlarmOccurrenceContext alarmOccurance = getLatestAlarmOccurance(messageKey, alarmType);
		AlarmSeverityContext clearAlarmSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
		if (alarmOccurance.getSeverity().equals(clearAlarmSeverity)) {
			return null;
		}
		return alarmOccurance;
	}
	
	public static AlarmOccurrenceContext createAlarm(BaseEventContext baseEvent) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, true);
		
		BaseAlarmContext baseAlarm = baseEvent.updateAlarmContext(null, true);
		updateAlarmSystemFields(baseAlarm, alarmOccurrence);
		addAlarm(baseAlarm, baseEvent.getAlarmType(), alarmOccurrence.getSeverity(), modBean);
		alarmOccurrence.setAlarm(baseAlarm);
		
		addAlarmOccurrence(alarmOccurrence, baseEvent, modBean);
		return alarmOccurrence;
	}
	
	public static AlarmOccurrenceContext createAlarmOccurrence(BaseAlarmContext baseAlarm, BaseEventContext baseEvent) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, true);
		alarmOccurrence.setAlarm(baseAlarm);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		addAlarmOccurrence(alarmOccurrence, baseEvent, modBean);
		return alarmOccurrence;
	}
	
	private static void addAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent, ModuleBean modBean) throws Exception {
		InsertRecordBuilder<AlarmOccurrenceContext> occurranceBuilder = new InsertRecordBuilder<AlarmOccurrenceContext>()
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE));
		occurranceBuilder.insert(alarmOccurrence);
		
		rollUpAlarm(alarmOccurrence, baseEvent, modBean);
	}

	private static void addAlarm(BaseAlarmContext baseAlarm, Type type, AlarmSeverityContext severity, ModuleBean modBean) throws Exception {
		String moduleName = getAlarmModuleName(type);
		Class alarmClass = getAlarmClass(type);
		
		baseAlarm.setSeverity(severity);
		baseAlarm.setType(type);
		
		InsertRecordBuilder<BaseAlarmContext> alarmBuilder = new InsertRecordBuilder<BaseAlarmContext>()
				.moduleName(moduleName)
				.fields(modBean.getAllFields(moduleName));
		alarmBuilder.insert(baseAlarm);
	}

	public static void updateAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent) throws Exception {
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, false);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule occurrenceModule = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		UpdateRecordBuilder<AlarmOccurrenceContext> builder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
				.module(occurrenceModule)
				.fields(modBean.getAllFields(occurrenceModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(alarmOccurrence.getId(), occurrenceModule));
		builder.update(alarmOccurrence);
		
		rollUpAlarm(alarmOccurrence, baseEvent, modBean);
	}

	private static void rollUpAlarm(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent, ModuleBean modBean) throws Exception {
		// roll-up alarm object
		BaseAlarmContext baseAlarm = getAlarm(alarmOccurrence.getAlarm().getId());
		baseEvent.updateAlarmContext(baseAlarm, false);
		alarmOccurrence.updateAlarm(baseAlarm);
		
		updateAlarmSystemFields(baseAlarm, alarmOccurrence);
		
		FacilioModule alarmModule = modBean.getModule(getAlarmModuleName(baseAlarm.getTypeEnum()));
		UpdateRecordBuilder<BaseAlarmContext> alarmUpdateBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
				.module(alarmModule)
				.fields(modBean.getAllFields(alarmModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), alarmModule));
		alarmUpdateBuilder.update(baseAlarm);		
	}

	private static void updateAlarmSystemFields(BaseAlarmContext baseAlarm, AlarmOccurrenceContext alarmOccurrence) throws Exception {
		long currenTime = DateTimeUtil.getCurrenTime();
		baseAlarm.setLastOccurredTime(currenTime);
		if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
			baseAlarm.setLastClearedTime(currenTime);
		}
	}
}
