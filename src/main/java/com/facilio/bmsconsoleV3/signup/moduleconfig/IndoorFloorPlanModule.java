package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IndoorFloorPlanModule extends BaseModuleConfig{

    public IndoorFloorPlanModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule indoorFloorPlanModule = modBean.getModule(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

        FacilioForm floorPlanAddForm = new FacilioForm();
        floorPlanAddForm.setDisplayName("CREATE FLOORPLAN");
        floorPlanAddForm.setName("default_floorplan_web");
        floorPlanAddForm.setModule(indoorFloorPlanModule);
        floorPlanAddForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        floorPlanAddForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));


        List<FormField> floorPlanAddFormFields = new ArrayList<>();
        floorPlanAddFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        floorPlanAddFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        floorPlanAddFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 3, 2));
        floorPlanAddFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.REQUIRED,"building", 4, 2));
        floorPlanAddFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.REQUIRED,"floor", 5, 2));
        floorPlanAddFormFields.add(new FormField("floorPlanType", FacilioField.FieldDisplayType.SELECTBOX, "Floor Plan Type", FormField.Required.REQUIRED,6, 2));
        floorPlanAddFormFields.add(new FormField("file", FacilioField.FieldDisplayType.FILE, "FloorPlan Image", FormField.Required.REQUIRED, 7, 1));

        FormSection floorPlanAddFormSection = new FormSection("Default", 1, floorPlanAddFormFields, false);
        floorPlanAddFormSection.setSectionType(FormSection.SectionType.FIELDS);
        floorPlanAddForm.setSections(Collections.singletonList(floorPlanAddFormSection));
        floorPlanAddForm.setIsSystemForm(true);
        floorPlanAddForm.setType(FacilioForm.Type.FORM);

        FacilioForm workPlaceFloorPlanAddForm = new FacilioForm();
        workPlaceFloorPlanAddForm.setDisplayName("CREATE FLOORPLAN");
        workPlaceFloorPlanAddForm.setName("default_workplace_floorplan_web");
        workPlaceFloorPlanAddForm.setModule(indoorFloorPlanModule);
        workPlaceFloorPlanAddForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workPlaceFloorPlanAddForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.WORKPLACE_APP));

        List<FormField> workPlaceFloorPlanAddFormFields = new ArrayList<>();
        workPlaceFloorPlanAddFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        workPlaceFloorPlanAddFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        workPlaceFloorPlanAddFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 3, 2));
        workPlaceFloorPlanAddFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.REQUIRED,"building", 4, 2));
        workPlaceFloorPlanAddFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.REQUIRED,"floor", 5, 2));
        workPlaceFloorPlanAddFormFields.add(new FormField("floorPlanType", FacilioField.FieldDisplayType.SELECTBOX, "Floor Plan Type", FormField.Required.REQUIRED,6, 2));
        workPlaceFloorPlanAddFormFields.add(new FormField("file", FacilioField.FieldDisplayType.FILE, "FloorPlan Image", FormField.Required.REQUIRED, 7, 1));

        FormSection workPlaceFloorPlanAddFormSection = new FormSection("Default", 1, workPlaceFloorPlanAddFormFields, false);
        workPlaceFloorPlanAddFormSection.setSectionType(FormSection.SectionType.FIELDS);
        workPlaceFloorPlanAddForm.setSections(Collections.singletonList(workPlaceFloorPlanAddFormSection));
        workPlaceFloorPlanAddForm.setIsSystemForm(true);
        workPlaceFloorPlanAddForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> indoorFloorPlanModuleForms = new ArrayList<>();
        indoorFloorPlanModuleForms.add(floorPlanAddForm);
        indoorFloorPlanModuleForms.add(workPlaceFloorPlanAddForm);

        return indoorFloorPlanModuleForms;
    }
}
