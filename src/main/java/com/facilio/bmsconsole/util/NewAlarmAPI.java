package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;

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
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURANCE)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURANCE))
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", String.valueOf(alarm.getId()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC")
				.limit(1);
		return builder.fetchFirst();
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
		addAlarm(baseAlarm, baseEvent.getAlarmType(), alarmOccurrence.getSeverity(), modBean);
		alarmOccurrence.setAlarm(baseAlarm);
		
		addAlarmOccurrence(alarmOccurrence, modBean);
		return alarmOccurrence;
	}
	
	private static void addAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence, ModuleBean modBean) throws Exception {
		InsertRecordBuilder<AlarmOccurrenceContext> occurranceBuilder = new InsertRecordBuilder<AlarmOccurrenceContext>()
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURANCE)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURANCE));
		occurranceBuilder.insert(alarmOccurrence);
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

		UpdateRecordBuilder<AlarmOccurrenceContext> builder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
				.moduleName(FacilioConstants.ContextNames.ALARM_OCCURANCE)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURANCE));
		builder.update(alarmOccurrence);
		
		// roll-up alarm object
		
	}
}
