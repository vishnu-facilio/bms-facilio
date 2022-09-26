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

public class RequesterModule extends BaseModuleConfig{

    public RequesterModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.REQUESTER);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule requesterModule = modBean.getModule(FacilioConstants.ContextNames.REQUESTER);

        FacilioForm requesterModuleForm = new FacilioForm();
        requesterModuleForm.setDisplayName("REQUESTER");
        requesterModuleForm.setName("web_default");
        requesterModuleForm.setModule(requesterModule);
        requesterModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        requesterModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> requesterModuleFormFields = new ArrayList<>();
        requesterModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Requester Name", FormField.Required.REQUIRED, 1, 2));
        requesterModuleFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Requester Email", FormField.Required.REQUIRED, 2, 2));
//        requesterModuleForm.setFields(requesterModuleFormFields);

        FormSection section = new FormSection("Default", 1, requesterModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        requesterModuleForm.setSections(Collections.singletonList(section));
        requesterModuleForm.setIsSystemForm(true);
        requesterModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(requesterModuleForm);
    }

}
