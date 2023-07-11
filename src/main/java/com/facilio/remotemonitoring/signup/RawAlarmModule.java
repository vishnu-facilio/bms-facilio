package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class RawAlarmModule extends SignUpData {

    public static final String MODULE_NAME = "rawAlarm";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = new FacilioModule();
        rawAlarmModule.setName(MODULE_NAME);
        rawAlarmModule.setDisplayName("Raw Alarms");
        rawAlarmModule.setDescription("Raw Alarms");
        rawAlarmModule.setTableName("Raw_Alarms");
        rawAlarmModule.setCustom(false);
        rawAlarmModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        rawAlarmModule.setTrashEnabled(true);
        modBean.addModule(rawAlarmModule);

        FacilioModule rawAlarmMod = modBean.getModule(MODULE_NAME);
        LookupField siteLookup = new LookupField();
        siteLookup.setName("site");
        siteLookup.setModule(rawAlarmMod);
        siteLookup.setDisplayName("Site");
        siteLookup.setColumnName("SITE");
        siteLookup.setDataType(FieldType.LOOKUP);
        siteLookup.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        siteLookup.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        siteLookup.setDefault(true);
        modBean.addField(siteLookup);


        StringField messageField = new StringField();
        messageField.setName("message");
        messageField.setModule(rawAlarmMod);
        messageField.setDisplayName("Message");
        messageField.setColumnName("MESSAGE");
        messageField.setDataType(FieldType.BIG_STRING);
        messageField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        messageField.setDefault(true);
        messageField.setMainField(true);
        modBean.addField(messageField);

        DateField occurredTime = new DateField();
        occurredTime.setName("occurredTime");
        occurredTime.setModule(rawAlarmMod);
        occurredTime.setDisplayName("Occurred Time");
        occurredTime.setColumnName("OCCURRED_TIME");
        occurredTime.setDefault(true);
        occurredTime.setDataType(FieldType.DATE_TIME);
        occurredTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        modBean.addField(occurredTime);

        DateField clearedTime = new DateField();
        clearedTime.setName("clearedTime");
        clearedTime.setModule(rawAlarmMod);
        clearedTime.setDisplayName("Cleared Time");
        clearedTime.setColumnName("CLEARED_TIME");
        clearedTime.setDefault(true);
        clearedTime.setDataType(FieldType.DATE_TIME);
        clearedTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        modBean.addField(clearedTime);

        SystemEnumField strategyField = new SystemEnumField();
        strategyField.setName("strategy");
        strategyField.setDisplayName("Strategy");
        strategyField.setModule(rawAlarmMod);
        strategyField.setDataType(FieldType.SYSTEM_ENUM);
        strategyField.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        strategyField.setDefault(true);
        strategyField.setColumnName("STRATEGY");
        strategyField.setEnumName("AlarmStrategy");
        modBean.addField(strategyField);

        SystemEnumField sourceTypeField = new SystemEnumField();
        sourceTypeField.setName("sourceType");
        sourceTypeField.setDisplayName("Source Type");
        sourceTypeField.setModule(rawAlarmMod);
        sourceTypeField.setDataType(FieldType.STRING_SYSTEM_ENUM);
        sourceTypeField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        sourceTypeField.setDefault(true);
        sourceTypeField.setColumnName("SOURCE_TYPE");
        sourceTypeField.setEnumName("RawAlarmSourceType");
        modBean.addField(sourceTypeField);

        BooleanField filteredField = new BooleanField();
        filteredField.setModule(rawAlarmMod);
        filteredField.setName("filtered");
        filteredField.setDisplayName("Filtered");
        filteredField.setDataType(FieldType.BOOLEAN);
        filteredField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        filteredField.setDefault(true);
        filteredField.setColumnName("FILTERED");
        modBean.addField(filteredField);

        BooleanField processedField = new BooleanField();
        processedField.setModule(rawAlarmMod);
        processedField.setName("processed");
        processedField.setDisplayName("Processed");
        processedField.setDataType(FieldType.BOOLEAN);
        processedField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        processedField.setDefault(true);
        processedField.setColumnName("PROCESSED");
        modBean.addField(processedField);

        LookupField clientField = new LookupField();
        clientField.setDefault(true);
        clientField.setName("client");
        clientField.setDisplayName("Client");
        clientField.setModule(rawAlarmMod);
        clientField.setDataType(FieldType.LOOKUP);
        clientField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        clientField.setColumnName("CLIENT_ID");
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        modBean.addField(clientField);

        LookupField controllerField = new LookupField();
        controllerField.setDefault(true);
        controllerField.setName("controller");
        controllerField.setDisplayName("Controller");
        controllerField.setModule(rawAlarmMod);
        controllerField.setDataType(FieldType.LOOKUP);
        controllerField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        controllerField.setColumnName("CONTROLLER");
        controllerField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
        modBean.addField(controllerField);

        LookupField alarmCategoryField = new LookupField();
        alarmCategoryField.setDefault(true);
        alarmCategoryField.setName("alarmCategory");
        alarmCategoryField.setDisplayName("Alarm Category");
        alarmCategoryField.setModule(rawAlarmMod);
        alarmCategoryField.setDataType(FieldType.LOOKUP);
        alarmCategoryField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmCategoryField.setColumnName("ALARM_CATEGORY");
        alarmCategoryField.setLookupModule(modBean.getModule(AlarmCategoryModule.MODULE_NAME));
        modBean.addField(alarmCategoryField);

        LookupField alarmTypeField = new LookupField();
        alarmTypeField.setDefault(true);
        alarmTypeField.setName("alarmType");
        alarmTypeField.setDisplayName("Alarm Type");
        alarmTypeField.setModule(rawAlarmMod);
        alarmTypeField.setDataType(FieldType.LOOKUP);
        alarmTypeField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmTypeField.setColumnName("ALARM_TYPE");
        alarmTypeField.setLookupModule(modBean.getModule(AlarmTypeModule.MODULE_NAME));
        modBean.addField(alarmTypeField);

        LookupField alarmDefinitionField = new LookupField();
        alarmDefinitionField.setDefault(true);
        alarmDefinitionField.setName("alarmDefinition");
        alarmDefinitionField.setDisplayName("Alarm Definition");
        alarmDefinitionField.setModule(rawAlarmMod);
        alarmDefinitionField.setDataType(FieldType.LOOKUP);
        alarmDefinitionField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmDefinitionField.setColumnName("ALARM_DEFINITION");
        alarmDefinitionField.setLookupModule(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
        modBean.addField(alarmDefinitionField);

        NumberField filterRuleCriteriaField = new NumberField();
        filterRuleCriteriaField.setDefault(true);
        filterRuleCriteriaField.setName("filterRuleCriteriaId");
        filterRuleCriteriaField.setDisplayName("Filter Rule Criteria");
        filterRuleCriteriaField.setModule(rawAlarmMod);
        filterRuleCriteriaField.setDataType(FieldType.NUMBER);
        filterRuleCriteriaField.setColumnName("FILTER_RULE_CRITERIA_ID");
        modBean.addField(filterRuleCriteriaField);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", rawAlarmMod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", rawAlarmMod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", rawAlarmMod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", rawAlarmMod));

    }
}