package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
public class FlaggedEventAlarmTypeRelModule extends SignUpData {

    public static final String MODULE_NAME = "flaggedEventRuleAlarmType";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = new FacilioModule();
        rawAlarmModule.setName(MODULE_NAME);
        rawAlarmModule.setDisplayName("Flagged Event Rule Alarm Type");
        rawAlarmModule.setDescription("Flagged Event Rule Alarm Type");
        rawAlarmModule.setTableName("Flagged_Event_Rule_Alarm_Type");
        rawAlarmModule.setCustom(false);
        rawAlarmModule.setType(FacilioModule.ModuleType.SUB_ENTITY);
        modBean.addModule(rawAlarmModule);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField flaggedEventRuleField = new LookupField();
        flaggedEventRuleField.setDefault(true);
        flaggedEventRuleField.setName("flaggedEventRule");
        flaggedEventRuleField.setDisplayName("Flagged Event Rule");
        flaggedEventRuleField.setModule(mod);
        flaggedEventRuleField.setDataType(FieldType.LOOKUP);
        flaggedEventRuleField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventRuleField.setColumnName("FLAGGED_EVENT_RULE");
        flaggedEventRuleField.setLookupModule(modBean.getModule(FlaggedEventRuleModule.MODULE_NAME));
        modBean.addField(flaggedEventRuleField);

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

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

    }
}