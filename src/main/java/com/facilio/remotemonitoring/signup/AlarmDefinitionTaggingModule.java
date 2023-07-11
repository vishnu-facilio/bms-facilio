package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class AlarmDefinitionTaggingModule extends SignUpData {
    public static final String MODULE_NAME = "alarmDefinitionTagging";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Alarm Definition Tagging");
        module.setDescription("Alarm Definition Tagging");
        module.setTableName("Alarm_Definition_Tagging");
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

//        LookupField controllerTypeField = new LookupField();
//        controllerTypeField.setDefault(true);
//        controllerTypeField.setName("controllerType");
//        controllerTypeField.setDisplayName("Controller Type");
//        controllerTypeField.setModule(mod);
//        controllerTypeField.setDataType(FieldType.LOOKUP);
//        controllerTypeField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
//        controllerTypeField.setColumnName("CONTROLLER_TYPE");
//        controllerTypeField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
//        modBean.addField(controllerTypeField);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));
    }
}
