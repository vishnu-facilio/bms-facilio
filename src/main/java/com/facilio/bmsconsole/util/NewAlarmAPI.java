package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class NewAlarmAPI {
	
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
	
	public static List<AlarmOccurrenceContext> getLatestAlarmOccurance(List<String> messageKeys) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.beanClass(AlarmOccurrenceContext.class)
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
				.select(fields)
				.innerJoin("BaseAlarm")
					.on("AlarmOccurrence.ALARM_ID = BaseAlarm.ID")
				.andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", StringUtils.join(messageKeys, ','), StringOperators.IS))
				.orderBy("CREATED_TIME DESC, ID DESC")
				.limit(1);
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
		AlarmSeverityContext clearAlarmSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
		if (alarmOccurance.getSeverity().equals(clearAlarmSeverity)) {
			return null;
		}
		return alarmOccurance;
	}
	
	public static AlarmOccurrenceContext createAlarm(BaseEventContext baseEvent) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, true);
		
		BaseAlarmContext baseAlarm = baseEvent.updateAlarmContext(null, true);
		updateAlarmSystemFields(baseAlarm, alarmOccurrence);
		
		baseAlarm.setSeverity(alarmOccurrence.getSeverity());
		baseAlarm.setType(baseEvent.getAlarmType());
		
		alarmOccurrence.setAlarm(baseAlarm);
		
		addAlarmOccurrence(alarmOccurrence, baseEvent, true);
		return alarmOccurrence;
	}
	
	public static AlarmOccurrenceContext createAlarmOccurrence(BaseAlarmContext baseAlarm, BaseEventContext baseEvent, boolean mostRecent) throws Exception {
		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, true);
		alarmOccurrence.setAlarm(baseAlarm);
		
		addAlarmOccurrence(alarmOccurrence, baseEvent, mostRecent);
		return alarmOccurrence;
	}
	
	private static void addAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent, boolean mostRecent) throws Exception {
		rollUpAlarm(alarmOccurrence, baseEvent, mostRecent);
	}

	private static void addAlarm(BaseAlarmContext baseAlarm, Type type, AlarmSeverityContext severity, ModuleBean modBean) throws Exception {
		
	}

	public static void updateAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent, boolean mostRecent) throws Exception {
		baseEvent.updateAlarmOccurrenceContext(alarmOccurrence, false);

		rollUpAlarm(alarmOccurrence, baseEvent, mostRecent);
	}

	private static void rollUpAlarm(AlarmOccurrenceContext alarmOccurrence, BaseEventContext baseEvent, boolean mostRecent) throws Exception {
		if (!mostRecent) {
			return;
		}
		// roll-up alarm object
		BaseAlarmContext baseAlarm = alarmOccurrence.getAlarm();
		baseEvent.updateAlarmContext(baseAlarm, false);
		alarmOccurrence.updateAlarm(baseAlarm);
		
		updateAlarmSystemFields(baseAlarm, alarmOccurrence);
	}

	private static void updateAlarmSystemFields(BaseAlarmContext baseAlarm, AlarmOccurrenceContext alarmOccurrence) throws Exception {
		long currenTime = DateTimeUtil.getCurrenTime();
		baseAlarm.setLastOccurredTime(currenTime);
		if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
			baseAlarm.setLastClearedTime(currenTime);
		}
	}
}
