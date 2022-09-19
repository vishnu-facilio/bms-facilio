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

public class TermsAndConditionsModule extends BaseModuleConfig{

    public TermsAndConditionsModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule termsAndConditionsModule = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);

        FacilioForm termsAndConditionForm = new FacilioForm();
        termsAndConditionForm.setDisplayName("TERMS AND CONDITION");
        termsAndConditionForm.setName("web_default");
        termsAndConditionForm.setModule(termsAndConditionsModule);
        termsAndConditionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        termsAndConditionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> termsAndConditionFormFields = new ArrayList<>();
        termsAndConditionFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        termsAndConditionFormFields.add(new FormField("termType", FacilioField.FieldDisplayType.TEXTBOX, "Term Type", FormField.Required.OPTIONAL, 2, 1));
        termsAndConditionFormFields.add(new FormField("shortDesc", FacilioField.FieldDisplayType.TEXTAREA, "Short Description", FormField.Required.OPTIONAL, 3, 1));
        FormField descField = new FormField("longDesc", FacilioField.FieldDisplayType.TEXTAREA, "Long Description", FormField.Required.OPTIONAL, 4, 1);
        descField.addToConfig("richText", true);
        termsAndConditionFormFields.add(descField);
        termsAndConditionFormFields.add(new FormField("defaultOnPo", FacilioField.FieldDisplayType.DECISION_BOX, "Default On PO", FormField.Required.OPTIONAL, 5, 2));
        termsAndConditionFormFields.add(new FormField("defaultOnQuotation", FacilioField.FieldDisplayType.DECISION_BOX, "Default On Quotation", FormField.Required.OPTIONAL, 6, 2));
//        termsAndConditionForm.setFields(termsAndConditionFormFields);

        FormSection section = new FormSection("Default", 1, termsAndConditionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        termsAndConditionForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(termsAndConditionForm);
    }

}
