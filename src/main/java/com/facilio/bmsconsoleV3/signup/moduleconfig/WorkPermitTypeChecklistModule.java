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

public class WorkPermitTypeChecklistModule extends BaseModuleConfig {

    public WorkPermitTypeChecklistModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workPermitTypeChecklistModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST);

        FacilioForm workPermitTypeChecklistForm = new FacilioForm();
        workPermitTypeChecklistForm.setDisplayName("WORK PERMIT TYPE CHECKLIST");
        workPermitTypeChecklistForm.setName("default_workpermittypechecklist_web");
        workPermitTypeChecklistForm.setModule(workPermitTypeChecklistModule);
        workPermitTypeChecklistForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workPermitTypeChecklistForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workPermitTypeChecklistFormFields = new ArrayList<>();
        workPermitTypeChecklistFormFields.add(new FormField("item", FacilioField.FieldDisplayType.TEXTBOX, "Item", FormField.Required.REQUIRED, 1, 1));
        workPermitTypeChecklistForm.setFields(workPermitTypeChecklistFormFields);

        FormSection section = new FormSection("Default", 1, workPermitTypeChecklistFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workPermitTypeChecklistForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(workPermitTypeChecklistForm);
    }


}

