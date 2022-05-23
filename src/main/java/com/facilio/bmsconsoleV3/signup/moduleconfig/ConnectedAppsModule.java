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

public class ConnectedAppsModule extends BaseModuleConfig{

    public ConnectedAppsModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.CONNECTED_APPS);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule connectedAppsModule = modBean.getModule(FacilioConstants.ContextNames.CONNECTED_APPS);

        FacilioForm connectedAppsModuleForm = new FacilioForm();
        connectedAppsModuleForm.setDisplayName("CONNECTED APP");
        connectedAppsModuleForm.setName("web_default");
        connectedAppsModuleForm.setModule(connectedAppsModule);
        connectedAppsModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        connectedAppsModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> connectedAppsModuleFormFields = new ArrayList<>();
        connectedAppsModuleFormFields.add(new FormField("logoId", FacilioField.FieldDisplayType.IMAGE, "Logo", FormField.Required.OPTIONAL, 1, 1));
        connectedAppsModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        connectedAppsModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        connectedAppsModuleFormFields.add(new FormField("sandBoxBaseUrl", FacilioField.FieldDisplayType.TEXTBOX, "Sandbox Base URL", FormField.Required.REQUIRED, 4, 1));
        connectedAppsModuleFormFields.add(new FormField("productionBaseUrl", FacilioField.FieldDisplayType.TEXTBOX, "Production Base URL", FormField.Required.REQUIRED, 4, 1));
        connectedAppsModuleFormFields.add(new FormField("startUrl", FacilioField.FieldDisplayType.TEXTBOX, "Welcome URL", FormField.Required.REQUIRED, 5, 1));
        connectedAppsModuleFormFields.add(new FormField("showInLauncher", FacilioField.FieldDisplayType.DECISION_BOX, "Show in Launcher", FormField.Required.OPTIONAL, 6, 1));
//        connectedAppsModuleForm.setFields(connectedAppsModuleFormFields);

        FormSection Section = new FormSection("Default", 1, connectedAppsModuleFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        connectedAppsModuleForm.setSections(Collections.singletonList(Section));

        return Collections.singletonList(connectedAppsModuleForm);
    }
}
