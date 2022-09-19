package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class HazardModule extends BaseModuleConfig{
    public HazardModule(){
        setModuleName(FacilioConstants.ContextNames.HAZARD);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> hazard = new ArrayList<FacilioView>();
        hazard.add(getAllHazardModuleView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.HAZARD);
        groupDetails.put("views", hazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHazardModuleView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Hazards");

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule hazardModule = modBean.getModule(FacilioConstants.ContextNames.HAZARD);

        FacilioForm hazardForm = new FacilioForm();
        hazardForm.setDisplayName("HAZARD");
        hazardForm.setName("default_hazard_web");
        hazardForm.setModule(hazardModule);
        hazardForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        hazardForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> hazardFormFields = new ArrayList<>();
        hazardFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        hazardFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        hazardFormFields.add(new FormField("type", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.OPTIONAL, 3, 1));
//        hazardForm.setFields(hazardFormFields);

        FormSection section = new FormSection("Default", 1, hazardFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        hazardForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(hazardForm);
    }
}

