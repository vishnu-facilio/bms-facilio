package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;


public class AlarmFilterRuleModule extends SignUpData {

    public static final String MODULE_NAME = "alarmCorrelationRule";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Alarm Correlation Rule");
        module.setDescription("Alarm Correlation Rule");
        module.setTableName("Alarm_Filter_Rule");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setModule(mod);
        nameField.setDisplayName("Name");
        nameField.setColumnName("NAME");
        nameField.setDataType(FieldType.STRING);
        nameField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        nameField.setDefault(true);
        nameField.setMainField(true);
        modBean.addField(nameField);

        LargeTextField descriptionField = new LargeTextField();
        descriptionField.setName("description");
        descriptionField.setDisplayName("Description");
        descriptionField.setModule(mod);
        descriptionField.setDataType(FieldType.LARGE_TEXT);
        descriptionField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        descriptionField.setDefault(true);
        modBean.addField(descriptionField);

        BooleanField statusField = new BooleanField();
        statusField.setName("status");
        statusField.setDisplayName("Status");
        statusField.setModule(mod);
        statusField.setColumnName("STATUS");
        statusField.setDataType(FieldType.BOOLEAN);
        statusField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        statusField.setDefault(true);
        modBean.addField(statusField);

        LookupField alarmType = new LookupField();
        alarmType.setDefault(true);
        alarmType.setName("alarmType");
        alarmType.setDisplayName("Alarm Type");
        alarmType.setModule(mod);
        alarmType.setDataType(FieldType.LOOKUP);
        alarmType.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmType.setColumnName("ALARM_TYPE");
        alarmType.setLookupModule(modBean.getModule(AlarmTypeModule.MODULE_NAME));
        modBean.addField(alarmType);

        SystemEnumField alarmApproach = new SystemEnumField();
        alarmApproach.setDefault(true);
        alarmApproach.setName("alarmApproach");
        alarmApproach.setDisplayName("Alarm Approach");
        alarmApproach.setModule(mod);
        alarmApproach.setEnumName("AlarmApproach");
        alarmApproach.setDataType(FieldType.SYSTEM_ENUM);
        alarmApproach.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        alarmApproach.setColumnName("STRATEGY");
        modBean.addField(alarmApproach);

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

        NumberField priorityField = new NumberField();
        priorityField.setName("priority");
        priorityField.setDisplayName("Priority");
        priorityField.setColumnName("PRIORITY");
        priorityField.setModule(mod);
        priorityField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        priorityField.setDataType(FieldType.NUMBER);
        modBean.addField(priorityField);

        NumberField controllerCriteriaId = new NumberField();
        controllerCriteriaId.setDefault(true);
        controllerCriteriaId.setName("controllerCriteriaId");
        controllerCriteriaId.setDisplayName("Controller Criteria Id");
        controllerCriteriaId.setModule(mod);
        controllerCriteriaId.setDataType(FieldType.NUMBER);
        controllerCriteriaId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        controllerCriteriaId.setColumnName("CONTROLLER_CRITERIA_ID");
        modBean.addField(controllerCriteriaId);

        NumberField siteCriteriaId = new NumberField();
        siteCriteriaId.setDefault(true);
        siteCriteriaId.setName("siteCriteriaId");
        siteCriteriaId.setDisplayName("Site Criteria Id");
        siteCriteriaId.setModule(mod);
        siteCriteriaId.setDataType(FieldType.NUMBER);
        siteCriteriaId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        siteCriteriaId.setColumnName("SITE_CRITERIA_ID");
        modBean.addField(siteCriteriaId);

        StringSystemEnumField ruleType = new StringSystemEnumField();
        ruleType.setDefault(true);
        ruleType.setName("ruleType");
        ruleType.setDisplayName("Rule Type");
        ruleType.setModule(mod);
        ruleType.setEnumName("RuleType");
        ruleType.setDataType(FieldType.STRING_SYSTEM_ENUM);
        ruleType.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        ruleType.setColumnName("FILTER_TYPE");
        modBean.addField(ruleType);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));
    }
}