package com.facilio.alarms.sensor.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class SensorRollUpUtil {
	
	private static final Logger LOGGER = Logger.getLogger(SensorRollUpUtil.class.getName());	
	
	public static void fetchSensorRollUpAlarmMeta(Set<Long> assetIds, LinkedHashMap<String, SensorRollUpEventContext> fieldSensorRollUpEventMeta, LinkedHashMap<Long, SensorRollUpEventContext> assetSensorRollUpEventMeta) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<SensorRollUpAlarmContext> sensorRollUpAlarms = new SelectRecordsBuilder<SensorRollUpAlarmContext>()
												.select(fields)
												.beanClass(SensorRollUpAlarmContext.class)
												.moduleName(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM)
												.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), assetIds, NumberOperators.EQUALS))
												.fetchSupplement((LookupField) fieldMap.get("severity"))
												.get();
		if (CollectionUtils.isNotEmpty(sensorRollUpAlarms)) {
			for (SensorRollUpAlarmContext sensorRollUpAlarm : sensorRollUpAlarms) 
			{
				SensorRollUpEventContext eventMeta = constructNewRollUpEventMeta(sensorRollUpAlarm);
				if(eventMeta.getReadingFieldId() == -1 || eventMeta.getReadingField() == null) {
					assetSensorRollUpEventMeta.put(sensorRollUpAlarm.getResource().getId(), eventMeta);
				}
				else {
					if(fieldSensorRollUpEventMeta.containsKey(ReadingsAPI.getRDMKey(eventMeta.getResource().getId(), eventMeta.getReadingField()))){
						LOGGER.log(Level.SEVERE, " Duplicate rollupfield sensor alarm_keys for alarm: "+sensorRollUpAlarm);
					}
					fieldSensorRollUpEventMeta.put(ReadingsAPI.getRDMKey(eventMeta.getResource().getId(), eventMeta.getReadingField()), eventMeta);
				}
			}
		}
	}
	
	public static List<String> getSensorRollUpAlarmsByAssetAndTime(List<SensorRollUpEventContext> sensorMeterRollUpEvents) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ROLLUP_EVENT);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Criteria criteria = new Criteria();
		for(SensorRollUpEventContext sensorMeterRollUpEvent: sensorMeterRollUpEvents) {			
			Criteria subCriteria = new Criteria();
			subCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), ""+sensorMeterRollUpEvent.getResource().getId(), NumberOperators.EQUALS));
			subCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"),""+sensorMeterRollUpEvent.getCreatedTime(), NumberOperators.EQUALS));
			criteria.orCriteria(subCriteria);
		}

		List<SensorRollUpEventContext> alreadyPresentSensorMeterRollUpEvents = new SelectRecordsBuilder<SensorRollUpEventContext>()
												.select(fields)
												.beanClass(SensorRollUpEventContext.class)
												.moduleName(FacilioConstants.ContextNames.SENSOR_ROLLUP_EVENT)
												.andCriteria(criteria)
												.get();
		
		List<String> assetIdsVsEventTime = new ArrayList<String>();
		if (alreadyPresentSensorMeterRollUpEvents != null && !alreadyPresentSensorMeterRollUpEvents.isEmpty()) {
			for (SensorRollUpEventContext sensorMeterRollUpEvent : alreadyPresentSensorMeterRollUpEvents) {
				assetIdsVsEventTime.add(sensorMeterRollUpEvent.getResource().getId()+"_"+sensorMeterRollUpEvent.getCreatedTime());
			}
		}
		return assetIdsVsEventTime;
	}

	public static SensorRollUpEventContext constructRollUpClearEvent(SensorRollUpEventContext previousSensorRollUpEventMeta, ReadingContext reading, boolean isHistorical, boolean isMeterRollUpEvent) throws Exception {
		
		if (previousSensorRollUpEventMeta != null && !previousSensorRollUpEventMeta.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) 
		{			
			SensorRollUpEventContext sensorRollUpEvent = new SensorRollUpEventContext();
			
			sensorRollUpEvent.setIsMeterRollUpEvent(isMeterRollUpEvent);

			sensorRollUpEvent.setReadingFieldId(previousSensorRollUpEventMeta.getReadingFieldId());

			sensorRollUpEvent.setSensorRule(previousSensorRollUpEventMeta.getSensorRule());
			
			sensorRollUpEvent.setAutoClear(true);
			sensorRollUpEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
			sensorRollUpEvent.setComment("Sensor alarm auto cleared because associated rule executed clear condition for the associated asset.");
			sensorRollUpEvent.setEventMessage(previousSensorRollUpEventMeta.getEventMessage());

			SensorRuleUtil.addDefaultEventProps(reading, null, sensorRollUpEvent);
			return sensorRollUpEvent;
		}
		return null;
	}
	
	public static SensorRollUpEventContext constructNewRollUpEventMeta (SensorRollUpAlarmContext sensorRollUpAlarm) {
		SensorRollUpEventContext eventMeta = new SensorRollUpEventContext();
		eventMeta.setOrgId(AccountUtil.getCurrentOrg().getId());
		eventMeta.setResource(sensorRollUpAlarm.getResource());
		eventMeta.setSeverityString(sensorRollUpAlarm.getSeverity().getSeverity());
		eventMeta.setSeverity(sensorRollUpAlarm.getSeverity());

		if(sensorRollUpAlarm.getReadingFieldId() != -1) {
			eventMeta.setReadingFieldId(sensorRollUpAlarm.getReadingFieldId());
			eventMeta.setReadingField(sensorRollUpAlarm.getReadingField());
		}
		
		if(!StringUtils.isEmpty(sensorRollUpAlarm.getSubject())) {
			eventMeta.setMessage(sensorRollUpAlarm.getSubject());
		}
		return eventMeta;
	}
	
	public static void runSensorMig() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule baseAlarmModule = modBean.getModule("basealarm");
		FacilioModule alarmOccurrenceModule = modBean.getModule("alarmoccurrence");
		FacilioModule baseEventModule = modBean.getModule("baseevent");
		
		LookupField sensorRule = new LookupField();
		sensorRule.setName("sensorRule");
		sensorRule.setDisplayName("Sensor Rule");
		sensorRule.setColumnName("RULE_ID");
		sensorRule.setOrgId(AccountUtil.getCurrentOrg().getId());
		sensorRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE.getIntValForDB());
		sensorRule.setDataType(FieldType.LOOKUP.getTypeAsInt());
		sensorRule.setDataType(FieldType.LOOKUP);
		sensorRule.setDefault(true);
		sensorRule.setDisabled(false);
		sensorRule.setRequired(false);
		sensorRule.setSpecialType("sensorRule");
		LookupField sensorRuleAlarmOccurrence = sensorRule.clone();
		LookupField sensorRuleEvent = sensorRule.clone();
		
		FacilioField readingField = new FacilioField();
		readingField.setName("readingFieldId");
		readingField.setDisplayName("Reading Field");
		readingField.setColumnName("READING_FIELD_ID");
		readingField.setOrgId(AccountUtil.getCurrentOrg().getId());
		readingField.setDisplayType(FacilioField.FieldDisplayType.NUMBER.getIntValForDB());
		readingField.setDataType(FieldType.NUMBER.getTypeAsInt());
		readingField.setDataType(FieldType.NUMBER);
		readingField.setDefault(true);
		readingField.setDisabled(false);
		readingField.setRequired(false);
		
		//sensor_alarms
		FacilioModule sensorAlarmModule = new FacilioModule();
		sensorAlarmModule.setName("sensoralarm");
		sensorAlarmModule.setDisplayName("Sensor Child Alarm");
		sensorAlarmModule.setTableName("SensorAlarm");
		sensorAlarmModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensorAlarmModule.setExtendModule(baseAlarmModule);
		sensorAlarmModule.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensorAlarmModuleId = modBean.addModule(sensorAlarmModule);
		
		sensorRule.setModule(modBean.getModule(sensorAlarmModuleId));
		readingField.setModule(modBean.getModule(sensorAlarmModuleId));
		modBean.addField(sensorRule);
		modBean.addField(readingField);
		
		FacilioModule sensoralarmoccurrence = new FacilioModule();
		sensoralarmoccurrence.setName("sensoralarmoccurrence");
		sensoralarmoccurrence.setDisplayName("Sensor Alarm Occurrence");
		sensoralarmoccurrence.setTableName("SensorAlarmOccurrence");
		sensoralarmoccurrence.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensoralarmoccurrence.setExtendModule(alarmOccurrenceModule);
		sensoralarmoccurrence.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensoralarmoccurrenceId = modBean.addModule(sensoralarmoccurrence);

		sensorRuleAlarmOccurrence.setModule(modBean.getModule(sensoralarmoccurrenceId));
		readingField.setModule(modBean.getModule(sensoralarmoccurrenceId));
		modBean.addField(sensorRuleAlarmOccurrence);
		modBean.addField(readingField);
		
		FacilioModule sensorevent = new FacilioModule();
		sensorevent.setName("sensorevent");
		sensorevent.setDisplayName("Sensor Event");
		sensorevent.setTableName("SensorEvent");
		sensorevent.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensorevent.setExtendModule(baseEventModule);
		sensorevent.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensoreventId = modBean.addModule(sensorevent);
		
		sensorRuleEvent.setModule(modBean.getModule(sensoreventId));
		readingField.setModule(modBean.getModule(sensoreventId));
		modBean.addField(sensorRuleEvent);
		modBean.addField(readingField);
		
		//sensor_rollup_alarms
		FacilioModule sensorrollupalarm = new FacilioModule();
		sensorrollupalarm.setName("sensorrollupalarm");
		sensorrollupalarm.setDisplayName("Sensor Alarm");
		sensorrollupalarm.setTableName("SensorRollUpAlarm");
		sensorrollupalarm.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensorrollupalarm.setExtendModule(baseAlarmModule);
		sensorrollupalarm.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensorrollupalarmId = modBean.addModule(sensorrollupalarm);
		
		readingField.setModule(modBean.getModule(sensorrollupalarmId));
		modBean.addField(readingField);
		
		FacilioModule sensorrollupalarmoccurrence = new FacilioModule();
		sensorrollupalarmoccurrence.setName("sensorrollupalarmoccurrence");
		sensorrollupalarmoccurrence.setDisplayName("Sensor RollUp Alarm Occurrence");
		sensorrollupalarmoccurrence.setTableName("SensorRollUpAlarmOccurrence");
		sensorrollupalarmoccurrence.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensorrollupalarmoccurrence.setExtendModule(alarmOccurrenceModule);
		sensorrollupalarmoccurrence.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensorrollupalarmoccurrenceId = modBean.addModule(sensorrollupalarmoccurrence);
		
		readingField.setModule(modBean.getModule(sensorrollupalarmoccurrenceId));
		modBean.addField(readingField);
		
		FacilioModule sensorrollupevent = new FacilioModule();
		sensorrollupevent.setName("sensorrollupevent");
		sensorrollupevent.setDisplayName("Sensor RollUp Event");
		sensorrollupevent.setTableName("SensorRollUpEvent");
		sensorrollupevent.setType(FacilioModule.ModuleType.BASE_ENTITY);
		sensorrollupevent.setExtendModule(baseEventModule);
		sensorrollupevent.setOrgId(AccountUtil.getCurrentOrg().getId());
		long sensorrollupeventId = modBean.addModule(sensorrollupevent);
		
		readingField.setModule(modBean.getModule(sensorrollupeventId));
		modBean.addField(readingField);	
	}

}
