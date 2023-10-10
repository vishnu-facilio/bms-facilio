package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.StringField;

public class AlarmTypeModule extends SignUpData {

    public static final String MODULE_NAME = "alarmType";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Alarm Type");
        module.setDescription("Alarm Type");
        module.setTableName("Alarm_Type");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setDisplayName("Name");
        nameField.setModule(mod);
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

        BooleanField uncategorisedAlarmField = new BooleanField();
        uncategorisedAlarmField.setName("uncategorisedAlarm");
        uncategorisedAlarmField.setDisplayName("Uncategorised Alarm");
        uncategorisedAlarmField.setModule(mod);
        uncategorisedAlarmField.setColumnName("UNCATEGORISED_ALARM");
        uncategorisedAlarmField.setDataType(FieldType.BOOLEAN);
        uncategorisedAlarmField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        uncategorisedAlarmField.setDefault(true);
        modBean.addField(uncategorisedAlarmField);

        StringField linkName = new StringField();
        linkName.setName("linkName");
        linkName.setDisplayName("Link Name");
        linkName.setModule(mod);
        linkName.setColumnName("LINK_NAME");
        linkName.setDataType(FieldType.STRING);
        linkName.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        linkName.setDefault(true);
        modBean.addField(linkName);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));
    }
}
