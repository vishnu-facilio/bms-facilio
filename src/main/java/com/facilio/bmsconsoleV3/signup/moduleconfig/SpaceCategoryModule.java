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

public class SpaceCategoryModule extends BaseModuleConfig{

    public SpaceCategoryModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.SPACE_CATEGORY);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceCategoryModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);

        FacilioForm spaceCategoryForm = new FacilioForm();
        spaceCategoryForm.setDisplayName("Space Category");
        spaceCategoryForm.setName("default_spacecategory_web");
        spaceCategoryForm.setModule(spaceCategoryModule);
        spaceCategoryForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceCategoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> spaceCategoryFormFields = new ArrayList<>();
        spaceCategoryFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.OPTIONAL, 1, 1));
        spaceCategoryFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceCategoryFormFields.add(new FormField("commonArea", FacilioField.FieldDisplayType.DECISION_BOX, "Is Common Area", FormField.Required.OPTIONAL, 3, 1));
//        spaceCategoryForm.setFields(spaceCategoryFormFields);

        FormSection section = new FormSection("Default", 1, spaceCategoryFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        spaceCategoryForm.setSections(Collections.singletonList(section));
        spaceCategoryForm.setIsSystemForm(true);
        spaceCategoryForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(spaceCategoryForm);
    }
}
