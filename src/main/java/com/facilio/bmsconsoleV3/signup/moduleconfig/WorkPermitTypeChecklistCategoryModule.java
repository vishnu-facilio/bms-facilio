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

public class WorkPermitTypeChecklistCategoryModule extends BaseModuleConfig{

    public WorkPermitTypeChecklistCategoryModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workPermitTypeChecklistCategoryModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY);

        FacilioForm workPermitTypeChecklistCategoryForm = new FacilioForm();
        workPermitTypeChecklistCategoryForm.setDisplayName("WORK PERMIT TYPE CHECKLIST CATEGORY");
        workPermitTypeChecklistCategoryForm.setName("default_workpermittypechecklistcategory_web");
        workPermitTypeChecklistCategoryForm.setModule(workPermitTypeChecklistCategoryModule);
        workPermitTypeChecklistCategoryForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workPermitTypeChecklistCategoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workPermitTypeChecklistCategoryFormFields = new ArrayList<>();
        workPermitTypeChecklistCategoryFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//        workPermitTypeChecklistCategoryForm.setFields(workPermitTypeChecklistCategoryFormFields);

        FormSection section = new FormSection("Default", 1, workPermitTypeChecklistCategoryFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workPermitTypeChecklistCategoryForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(workPermitTypeChecklistCategoryForm);
    }

}
