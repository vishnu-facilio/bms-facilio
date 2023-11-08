package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

public class AlarmDefinitionMappingModule extends SignUpData {

    public static final String MODULE_NAME = "alarmRegexMatcher";
    public static final String MODULE_DISPLAY_NAME = "Alarm Regex Matcher";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName(MODULE_DISPLAY_NAME);
        module.setDescription("Alarm Regex Matcher");
        module.setTableName("Alarm_Definition_Mapping");
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

        LookupField alarmDefinitionField = new LookupField();
        alarmDefinitionField.setName("alarmDefinition");
        alarmDefinitionField.setModule(mod);
        alarmDefinitionField.setDisplayName("Alarm Definition");
        alarmDefinitionField.setColumnName("ALARM_DEFINITION");
        alarmDefinitionField.setDataType(FieldType.LOOKUP);
        alarmDefinitionField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmDefinitionField.setDefault(true);
        alarmDefinitionField.setLookupModule(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
        modBean.addField(alarmDefinitionField);

        StringField regExField = new StringField();
        regExField.setName("regularExpression");
        regExField.setModule(mod);
        regExField.setDisplayName("Regular Expression");
        regExField.setColumnName("REGULAR_EXPRESSION");
        regExField.setDataType(FieldType.BIG_STRING);
        regExField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        regExField.setDefault(true);
        modBean.addField(regExField);

        NumberField priorityField = new NumberField();
        priorityField.setName("priority");
        priorityField.setDisplayName("Priority");
        priorityField.setColumnName("PRIORITY");
        priorityField.setModule(mod);
        priorityField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        priorityField.setDataType(FieldType.NUMBER);
        modBean.addField(priorityField);

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


        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

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