package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LabourModule extends BaseModuleConfig{

    public LabourModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.LABOUR);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);

        FacilioForm labourForm = new FacilioForm();
        labourForm.setDisplayName("LABOUR");
        labourForm.setName("web_default");
        labourForm.setModule(labourModule);
        labourForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        labourForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> labourFormFields = new ArrayList<>();
        labourFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        labourFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        labourFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 4, 1));
        labourFormFields.add(new FormField("location", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL,5, 1));
        labourFormFields.add(new FormField("user", FacilioField.FieldDisplayType.USER, "User", FormField.Required.OPTIONAL, 6, 1));
        labourFormFields.add(new FormField("cost", FacilioField.FieldDisplayType.DECIMAL, "Rate Per Hour", FormField.Required.OPTIONAL, 8, 1));
        labourFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, "site", 2, 1));
        labourFormFields.add(new FormField("availability", FacilioField.FieldDisplayType.DECISION_BOX, "Active", FormField.Required.OPTIONAL, 9, 1));
//        labourForm.setFields(labourFormFields);

        FormSection section = new FormSection("Default", 1, labourFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        labourForm.setSections(Collections.singletonList(section));
        labourForm.setIsSystemForm(true);
        labourForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(labourForm);
    }
}
