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

    public static final String RAW_ALARM_FIELD_NAME = "alarm";
    public static final String ALARM_FILTER_RULE_FIELD_NAME = "alarmCorrelationRule";
    public static final String FLAGGED_ALARM_PROCESS_FIELD_NAME = "flaggedAlarmProcess";
    public static final String FLAGGED_ALARM_FIELD_NAME = "flaggedAlarm";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule alarmModule = new FacilioModule();
        alarmModule.setName(MODULE_NAME);
        alarmModule.setDisplayName("Filtered Alarms");
        alarmModule.setDescription("Filtered Alarms");
        alarmModule.setTableName("Filtered_Alarms");
        alarmModule.setCustom(false);
        alarmModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        alarmModule.setTrashEnabled(true);
        modBean.addModule(alarmModule);

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
        flaggedEventField.setName(FLAGGED_ALARM_FIELD_NAME);
        flaggedEventField.setDisplayName("Flagged Alarm");
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
        rawAlarmField.setName(RAW_ALARM_FIELD_NAME);
        rawAlarmField.setDisplayName("Alarm");
        rawAlarmField.setModule(mod);
        rawAlarmField.setDataType(FieldType.LOOKUP);
        rawAlarmField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        rawAlarmField.setColumnName("RAW_ALARM");
        rawAlarmField.setLookupModule(modBean.getModule(RawAlarmModule.MODULE_NAME));
        modBean.addField(rawAlarmField);

        LookupField flaggedAlarmProcess = new LookupField();
        flaggedAlarmProcess.setDefault(true);
        flaggedAlarmProcess.setName(FLAGGED_ALARM_PROCESS_FIELD_NAME);
        flaggedAlarmProcess.setDisplayName("Flagged Alarm Process");
        flaggedAlarmProcess.setModule(mod);
        flaggedAlarmProcess.setDataType(FieldType.LOOKUP);
        flaggedAlarmProcess.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedAlarmProcess.setColumnName("FLAGGED_EVENT_RULE");
        flaggedAlarmProcess.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        modBean.addField(flaggedAlarmProcess);

        LookupField alarmCorrelationRule = new LookupField();
        alarmCorrelationRule.setDefault(true);
        alarmCorrelationRule.setName(ALARM_FILTER_RULE_FIELD_NAME);
        alarmCorrelationRule.setDisplayName("Alarm Correlation Rule");
        alarmCorrelationRule.setModule(mod);
        alarmCorrelationRule.setDataType(FieldType.LOOKUP);
        alarmCorrelationRule.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmCorrelationRule.setColumnName("ALARM_FILTER_RULE");
        alarmCorrelationRule.setLookupModule(modBean.getModule(AlarmFilterRuleModule.MODULE_NAME));
        modBean.addField(alarmCorrelationRule);

        LookupField asset = new LookupField();
        asset.setDefault(true);
        asset.setName("asset");
        asset.setDisplayName("Asset");
        asset.setModule(mod);
        asset.setDataType(FieldType.LOOKUP);
        asset.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        asset.setColumnName("ASSET_ID");
        asset.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        modBean.addField(asset);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

    }
}
