package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DevicesModule extends BaseModuleConfig{

    public DevicesModule() throws Exception {
        setModuleName(FacilioConstants.ModuleNames.DEVICES);
    }

    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

        FacilioForm devicesModuleForm = new FacilioForm();
        devicesModuleForm.setDisplayName("DEVICES");
        devicesModuleForm.setName("web_default");
        devicesModuleForm.setModule(devicesModule);
        devicesModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        devicesModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> devicesModuleFormFields = new ArrayList<>();
        devicesModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        devicesModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        devicesModuleFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        devicesModuleFormFields.add(new FormField("associatedResource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space", FormField.Required.OPTIONAL, 4, 1));
        devicesModuleFormFields.add(new FormField("deviceType", FacilioField.FieldDisplayType.SELECTBOX, "Device Type", FormField.Required.REQUIRED,5, 1));
//        devicesModuleForm.setFields(devicesModuleFormFields);

        FormSection devicesModuleFormSection = new FormSection("Default", 1, devicesModuleFormFields, false);
        devicesModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        devicesModuleForm.setSections(Collections.singletonList(devicesModuleFormSection));
        devicesModuleForm.setIsSystemForm(true);
        devicesModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(devicesModuleForm);
    }
}
