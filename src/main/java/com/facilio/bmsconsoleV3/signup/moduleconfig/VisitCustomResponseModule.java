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

public class VisitCustomResponseModule extends BaseModuleConfig{

    public VisitCustomResponseModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.VISIT_CUSTOM_RESPONSE);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule visitCustomResponseModule = modBean.getModule(FacilioConstants.ContextNames.VISIT_CUSTOM_RESPONSE);

        FacilioForm visitResponseForm = new FacilioForm();
        visitResponseForm.setDisplayName("VISIT RESPONSE");
        visitResponseForm.setName("default_response");
        visitResponseForm.setModule(visitCustomResponseModule);
        visitResponseForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        visitResponseForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> visitResponseFormFields = new ArrayList<>();
        visitResponseFormFields.add(new FormField("responseName", FacilioField.FieldDisplayType.TEXTBOX, "Response Name", FormField.Required.REQUIRED, 1, 1));
        visitResponseFormFields.add(new FormField("messageTitle", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 2, 1));
        visitResponseFormFields.add(new FormField("messageText", FacilioField.FieldDisplayType.TEXTAREA, "Message", FormField.Required.REQUIRED, 3, 1));
//        visitResponseForm.setFields(visitResponseFormFields);

        FormSection visitResponseFormSection = new FormSection("Default", 1, visitResponseFormFields, false);
        visitResponseFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitResponseForm.setSections(Collections.singletonList(visitResponseFormSection));
        visitResponseForm.setIsSystemForm(true);
        visitResponseForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(visitResponseForm);
    }

}
