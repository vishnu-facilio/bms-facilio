package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class AlarmFilterRuleCriteriaModule extends SignUpData {
    public static final String MODULE_NAME = "alarmFilterRuleCriteriaModule";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Alarm Filter Rule Criteria");
        module.setDescription("Alarm Filter Rule Criteria");
        module.setTableName("Alarm_Filter_Rule_Criteria");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField alarmFilterRuleField = new LookupField();
        alarmFilterRuleField.setDefault(true);
        alarmFilterRuleField.setName("alarmFilterRule");
        alarmFilterRuleField.setDisplayName("Alarm Filter Rule");
        alarmFilterRuleField.setModule(mod);
        alarmFilterRuleField.setDataType(FieldType.LOOKUP);
        alarmFilterRuleField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmFilterRuleField.setColumnName("ALARM_FILTER_RULE_ID");
        alarmFilterRuleField.setLookupModule(modBean.getModule(AlarmFilterRuleModule.MODULE_NAME));
        modBean.addField(alarmFilterRuleField);

        LookupField alarmDefinitionField = new LookupField();
        alarmDefinitionField.setDefault(true);
        alarmDefinitionField.setName("alarmDefinition");
        alarmDefinitionField.setDisplayName("Alarm Definition");
        alarmDefinitionField.setModule(mod);
        alarmDefinitionField.setDataType(FieldType.LOOKUP);
        alarmDefinitionField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmDefinitionField.setColumnName("ALARM_DEFINITION_ID");
        alarmDefinitionField.setLookupModule(modBean.getModule(AlarmTypeModule.MODULE_NAME));
        modBean.addField(alarmDefinitionField);

        SystemEnumField controllerTypeField = new SystemEnumField();
        controllerTypeField.setDefault(true);
        controllerTypeField.setName("controllerType");
        controllerTypeField.setDisplayName("Controller Type");
        controllerTypeField.setModule(mod);
        controllerTypeField.setDataType(FieldType.SYSTEM_ENUM);
        controllerTypeField.setEnumName("ControllerType");
        controllerTypeField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        controllerTypeField.setColumnName("CONTROLLER_TYPE");
        modBean.addField(controllerTypeField);

        SystemEnumField alarmFilterCriteriaType = new SystemEnumField();
        alarmFilterCriteriaType.setDefault(true);
        alarmFilterCriteriaType.setName("filterCriteria");
        alarmFilterCriteriaType.setDisplayName("Filter Criteria");
        alarmFilterCriteriaType.setModule(mod);
        alarmFilterCriteriaType.setDataType(FieldType.STRING_SYSTEM_ENUM);
        alarmFilterCriteriaType.setEnumName("AlarmFilterCriteriaType");
        alarmFilterCriteriaType.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        alarmFilterCriteriaType.setColumnName("ALARM_FILTER_CRITERIA_TYPE");
        modBean.addField(alarmFilterCriteriaType);

        NumberField alarmDurationField = new NumberField();
        alarmDurationField.setDefault(true);
        alarmDurationField.setName("alarmDuration");
        alarmDurationField.setDisplayName("Alarm Duration");
        alarmDurationField.setModule(mod);
        alarmDurationField.setDataType(FieldType.NUMBER);
        alarmDurationField.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        alarmDurationField.setColumnName("ALARM_DURATION");
        modBean.addField(alarmDurationField);

        NumberField alarmCountField = new NumberField();
        alarmCountField.setDefault(true);
        alarmCountField.setName("alarmCount");
        alarmCountField.setDisplayName("Alarm Count");
        alarmCountField.setModule(mod);
        alarmCountField.setDataType(FieldType.NUMBER);
        alarmCountField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        alarmCountField.setColumnName("ALARM_COUNT");
        modBean.addField(alarmCountField);

        NumberField alarmCountPeriodField = new NumberField();
        alarmCountPeriodField.setDefault(true);
        alarmCountPeriodField.setName("alarmCountPeriod");
        alarmCountPeriodField.setDisplayName("Alarm Count Period");
        alarmCountPeriodField.setModule(mod);
        alarmCountPeriodField.setDataType(FieldType.NUMBER);
        alarmCountPeriodField.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        alarmCountPeriodField.setColumnName("ALARM_COUNT_PERIOD");
        modBean.addField(alarmCountPeriodField);

        NumberField alarmClearPeriodField = new NumberField();
        alarmClearPeriodField.setDefault(true);
        alarmClearPeriodField.setName("alarmClearPeriod");
        alarmClearPeriodField.setDisplayName("Alarm Clear Period");
        alarmClearPeriodField.setModule(mod);
        alarmClearPeriodField.setDataType(FieldType.NUMBER);
        alarmClearPeriodField.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        alarmClearPeriodField.setColumnName("ALARM_CLEAR_PERIOD");
        modBean.addField(alarmClearPeriodField);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));
    }
}