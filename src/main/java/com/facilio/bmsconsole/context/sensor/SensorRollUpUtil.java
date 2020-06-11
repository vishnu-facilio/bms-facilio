package com.facilio.bmsconsole.context.sensor;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class SensorRollUpUtil {
	
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
		sensorAlarmModule.setDisplayName("Sensor Alarm");
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
		sensorrollupalarm.setDisplayName("Sensor RollUp Alarm");
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
