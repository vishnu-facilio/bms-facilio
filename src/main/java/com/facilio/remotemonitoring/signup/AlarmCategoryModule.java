package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;


public class AlarmCategoryModule extends SignUpData {
    public static final String MODULE_NAME = "alarmCategory";
    public static final String MODULE_DISPLAY_NAME = "Alarm Category";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule alarmCategoryModule = new FacilioModule();
        alarmCategoryModule.setName(MODULE_NAME);
        alarmCategoryModule.setDisplayName(MODULE_DISPLAY_NAME);
        alarmCategoryModule.setDescription("Alarm Category");
        alarmCategoryModule.setTableName("Alarm_Category");
        alarmCategoryModule.setCustom(false);
        alarmCategoryModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
        alarmCategoryModule.setTrashEnabled(true);
        modBean.addModule(alarmCategoryModule);

        FacilioModule alarmCatModule = modBean.getModule(MODULE_NAME);

        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setModule(alarmCatModule);
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
        descriptionField.setModule(alarmCatModule);
        descriptionField.setDataType(FieldType.LARGE_TEXT);
        descriptionField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        descriptionField.setDefault(true);
        modBean.addField(descriptionField);

        LookupField clientField = new LookupField();
        clientField.setDefault(true);
        clientField.setName("client");
        clientField.setDisplayName("Client");
        clientField.setModule(alarmCatModule);
        clientField.setDataType(FieldType.LOOKUP);
        clientField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        clientField.setColumnName("CLIENT_ID");
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        modBean.addField(clientField);


        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", alarmCatModule));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", alarmCatModule));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", alarmCatModule));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", alarmCatModule));

        addSystemButtons();
    }

    private static void addSystemButtons() throws Exception{
        SystemButtonApi.addCreateButtonWithModuleDisplayName(MODULE_NAME);
        SystemButtonApi.addExportAsCSV(MODULE_NAME);
        SystemButtonApi.addExportAsExcel(MODULE_NAME);
        SystemButtonApi.addListEditButton(MODULE_NAME);
        SystemButtonApi.addListDeleteButton(MODULE_NAME);
        SystemButtonApi.addBulkDeleteButton(MODULE_NAME);
    }
}
