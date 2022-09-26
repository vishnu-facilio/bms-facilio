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

public class WorkPermitTypeModule extends BaseModuleConfig{

    public WorkPermitTypeModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workPermitTypeModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE);

        FacilioForm workPermitTypeForm = new FacilioForm();
        workPermitTypeForm.setDisplayName("WORK PERMIT TYPE");
        workPermitTypeForm.setName("default_workpermittype_web");
        workPermitTypeForm.setModule(workPermitTypeModule);
        workPermitTypeForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workPermitTypeForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workPermitTypeFormFields = new ArrayList<>();
        workPermitTypeFormFields.add(new FormField("type", FacilioField.FieldDisplayType.TEXTBOX, "Type", FormField.Required.REQUIRED, 1, 1));
        workPermitTypeForm.setFields(workPermitTypeFormFields);

        FormSection section = new FormSection("Default", 1, workPermitTypeFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workPermitTypeForm.setSections(Collections.singletonList(section));
        workPermitTypeForm.setIsSystemForm(true);
        workPermitTypeForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(workPermitTypeForm);
    }

}
