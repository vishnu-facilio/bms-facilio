package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class FilteredAlarmModule extends SignUpData {

    public static final String MODULE_NAME = "filteredAlarm";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = new FacilioModule();
        rawAlarmModule.setName(MODULE_NAME);
        rawAlarmModule.setDisplayName("Filtered Alarms");
        rawAlarmModule.setDescription("Filtered Alarms");
        rawAlarmModule.setTableName("Filtered_Alarms");
        rawAlarmModule.setCustom(false);
        rawAlarmModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        rawAlarmModule.setTrashEnabled(true);
        modBean.addModule(rawAlarmModule);

        FacilioModule mod = modBean.getModule(MODULE_NAME);
        LookupField siteLookup = new LookupField();
        siteLookup.setName("site");
        siteLookup.setModule(mod);
        siteLookup.setDisplayName("Site");
        siteLookup.setColumnName("SITE");
        siteLookup.setDataType(FieldType.LOOKUP);
        siteLookup.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        siteLookup.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        siteLookup.setDefault(true);
        modBean.addField(siteLookup);


        StringField messageField = new StringField();
        messageField.setName("message");
        messageField.setModule(mod);
        messageField.setDisplayName("Message");
        messageField.setColumnName("MESSAGE");
        messageField.setDataType(FieldType.BIG_STRING);
        messageField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        messageField.setDefault(true);
        messageField.setMainField(true);
        modBean.addField(messageField);

        DateField occurredTime = new DateField();
        occurredTime.setName("occurredTime");
        occurredTime.setModule(mod);
        occurredTime.setDisplayName("Occurred Time");
        occurredTime.setColumnName("OCCURRED_TIME");
        occurredTime.setDefault(true);
        occurredTime.setDataType(FieldType.DATE_TIME);
        occurredTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        modBean.addField(occurredTime);

        DateField clearedTime = new DateField();
        clearedTime.setName("clearedTime");
        clearedTime.setModule(mod);
        clearedTime.setDisplayName("Cleared Time");
        clearedTime.setColumnName("CLEARED_TIME");
        clearedTime.setDefault(true);
        clearedTime.setDataType(FieldType.DATE_TIME);
        clearedTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        modBean.addField(clearedTime);

        LookupField clientField = new LookupField();
        clientField.setDefault(true);
        clientField.setName("client");
        clientField.setDisplayName("Client");
        clientField.setModule(mod);
        clientField.setDataType(FieldType.LOOKUP);
        clientField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        clientField.setColumnName("CLIENT_ID");
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        modBean.addField(clientField);

        LookupField flaggedEventField = new LookupField();
        flaggedEventField.setDefault(true);
        flaggedEventField.setName("flaggedEvent");
        flaggedEventField.setDisplayName("Flagged Event");
        flaggedEventField.setModule(mod);
        flaggedEventField.setDataType(FieldType.LOOKUP);
        flaggedEventField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventField.setColumnName("FLAGGED_EVENT");
        flaggedEventField.setLookupModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        modBean.addField(flaggedEventField);

        LookupField alarmCategoryField = new LookupField();
        alarmCategoryField.setDefault(true);
        alarmCategoryField.setName("alarmCategory");
        alarmCategoryField.setDisplayName("Alarm Category");
        alarmCategoryField.setModule(mod);
        alarmCategoryField.setDataType(FieldType.LOOKUP);
        alarmCategoryField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmCategoryField.setColumnName("ALARM_CATEGORY");
        alarmCategoryField.setLookupModule(modBean.getModule(AlarmCategoryModule.MODULE_NAME));
        modBean.addField(alarmCategoryField);

        LookupField alarmTypeField = new LookupField();
        alarmTypeField.setDefault(true);
        alarmTypeField.setName("alarmType");
        alarmTypeField.setDisplayName("Alarm Type");
        alarmTypeField.setModule(mod);
        alarmTypeField.setDataType(FieldType.LOOKUP);
        alarmTypeField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmTypeField.setColumnName("ALARM_TYPE");
        alarmTypeField.setLookupModule(modBean.getModule(AlarmTypeModule.MODULE_NAME));
        modBean.addField(alarmTypeField);


        LookupField controllerField = new LookupField();
        controllerField.setDefault(true);
        controllerField.setName("controller");
        controllerField.setDisplayName("Controller");
        controllerField.setModule(mod);
        controllerField.setDataType(FieldType.LOOKUP);
        controllerField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        controllerField.setColumnName("CONTROLLER");
        controllerField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
        modBean.addField(controllerField);

        LookupField rawAlarmField = new LookupField();
        rawAlarmField.setDefault(true);
        rawAlarmField.setName("rawAlarm");
        rawAlarmField.setDisplayName("Raw Alarm");
        rawAlarmField.setModule(mod);
        rawAlarmField.setDataType(FieldType.LOOKUP);
        rawAlarmField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        rawAlarmField.setColumnName("RAW_ALARM");
        rawAlarmField.setLookupModule(modBean.getModule(RawAlarmModule.MODULE_NAME));
        modBean.addField(rawAlarmField);

        LookupField flaggedEventRule = new LookupField();
        flaggedEventRule.setDefault(true);
        flaggedEventRule.setName("flaggedEventRule");
        flaggedEventRule.setDisplayName("Flagged Event Rule");
        flaggedEventRule.setModule(mod);
        flaggedEventRule.setDataType(FieldType.LOOKUP);
        flaggedEventRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventRule.setColumnName("FLAGGED_EVENT_RULE");
        flaggedEventRule.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        modBean.addField(flaggedEventRule);

        LookupField alarmFilterRule = new LookupField();
        alarmFilterRule.setDefault(true);
        alarmFilterRule.setName("alarmFilterRule");
        alarmFilterRule.setDisplayName("Alarm Filter Rule");
        alarmFilterRule.setModule(mod);
        alarmFilterRule.setDataType(FieldType.LOOKUP);
        alarmFilterRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmFilterRule.setColumnName("ALARM_FILTER_RULE");
        alarmFilterRule.setLookupModule(modBean.getModule(AlarmFilterRuleModule.MODULE_NAME));
        modBean.addField(alarmFilterRule);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

    }
}
