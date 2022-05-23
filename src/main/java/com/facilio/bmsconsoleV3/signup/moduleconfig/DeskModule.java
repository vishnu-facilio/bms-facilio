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

public class DeskModule extends BaseModuleConfig{

    public DeskModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.Floorplan.DESKS);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);

        FacilioForm desksForm = new FacilioForm();
        desksForm.setDisplayName("New Desk");
        desksForm.setName("default_desks_web");
        desksForm.setModule(deskModule);
        desksForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        desksForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

//      -->THESE FormField IS COMMON FOR desksForm AND desksPortalForm
        List<FormField> desksFormFields = new ArrayList<>();
        desksFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        desksFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        desksFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.REQUIRED, "department", 3, 2));
        desksFormFields.add(new FormField("deskType", FacilioField.FieldDisplayType.SELECTBOX, "Desk Type", FormField.Required.REQUIRED, 4, 2));
        desksFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        desksFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        desksFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));
//      -->THESE FormField IS COMMON FOR desksForm AND desksPortalForm
//        desksForm.setFields(desksFormFields);

        FormSection desksFormSection = new FormSection("Default", 1, desksFormFields, false);
        desksFormSection.setSectionType(FormSection.SectionType.FIELDS);
        desksForm.setSections(Collections.singletonList(desksFormSection));

        FacilioForm desksPortalForm = new FacilioForm();
        desksPortalForm.setDisplayName("New Desks");
        desksPortalForm.setName("default_desks_portal");
        desksPortalForm.setModule(deskModule);
        desksPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
//        desksPortalForm.setFields(desksFormFields);
        desksPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormSection desksPortalFormSection = new FormSection("Default", 1, desksFormFields, false);
        desksPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        desksPortalForm.setSections(Collections.singletonList(desksPortalFormSection));

        List<FacilioForm> deskModuleForms = new ArrayList<>();
        deskModuleForms.add(desksForm);
        deskModuleForms.add(desksPortalForm);

        return deskModuleForms;
    }
}
