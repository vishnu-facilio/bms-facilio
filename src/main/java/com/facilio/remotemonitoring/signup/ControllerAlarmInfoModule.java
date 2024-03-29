package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class ControllerAlarmInfoModule extends SignUpData {
    public static final String MODULE_NAME = "controllerAlarmInfo";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Controller Alarm Info");
        module.setDescription("Controller Alarm Info");
        module.setTableName("Controller_Alarm_Info");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

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

        LookupField alarmDefinitionField = new LookupField();
        alarmDefinitionField.setDefault(true);
        alarmDefinitionField.setName("alarmDefinition");
        alarmDefinitionField.setDisplayName("Alarm Definition");
        alarmDefinitionField.setModule(mod);
        alarmDefinitionField.setDataType(FieldType.LOOKUP);
        alarmDefinitionField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmDefinitionField.setColumnName("ALARM_DEFINITION");
        alarmDefinitionField.setLookupModule(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
        modBean.addField(alarmDefinitionField);

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

        SystemEnumField alarmApproach = new SystemEnumField();
        alarmApproach.setName("alarmApproach");
        alarmApproach.setDisplayName("Alarm Approach");
        alarmApproach.setModule(mod);
        alarmApproach.setDataType(FieldType.SYSTEM_ENUM);
        alarmApproach.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        alarmApproach.setDefault(true);
        alarmApproach.setColumnName("STRATEGY");
        alarmApproach.setEnumName("AlarmApproach");
        modBean.addField(alarmApproach);

        DateField clearedTime = new DateField();
        clearedTime.setName("alarmLastReceivedTime");
        clearedTime.setModule(mod);
        clearedTime.setDisplayName("Cleared Time");
        clearedTime.setColumnName("ALARM_LAST_RECEIVED_TIME");
        clearedTime.setDefault(true);
        clearedTime.setDataType(FieldType.DATE_TIME);
        clearedTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        modBean.addField(clearedTime);

        BooleanField filteredField = new BooleanField();
        filteredField.setName("filtered");
        filteredField.setModule(mod);
        filteredField.setDisplayName("Filtered");
        filteredField.setColumnName("FILTERED");
        filteredField.setDefault(true);
        filteredField.setDataType(FieldType.BOOLEAN);
        filteredField.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        modBean.addField(filteredField);

        LookupField alarmEvent = new LookupField();
        alarmEvent.setDefault(true);
        alarmEvent.setName("lastAlarmEvent");
        alarmEvent.setDisplayName("Last Alarm Event");
        alarmEvent.setModule(mod);
        alarmEvent.setDataType(FieldType.LOOKUP);
        alarmEvent.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmEvent.setColumnName("LAST_ALARM_EVENT");
        alarmEvent.setLookupModule(modBean.getModule(RawAlarmModule.MODULE_NAME));
        modBean.addField(alarmEvent);
    }
}