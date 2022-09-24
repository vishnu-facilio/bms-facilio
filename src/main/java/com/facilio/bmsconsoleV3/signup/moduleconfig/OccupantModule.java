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

public class OccupantModule extends BaseModuleConfig{
    public OccupantModule(){
        setModuleName(FacilioConstants.ContextNames.OCCUPANT);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule occupantModule = modBean.getModule(FacilioConstants.ContextNames.OCCUPANT);

        FacilioForm occupantForm = new FacilioForm();
        occupantForm.setDisplayName("OCCUPANT");
        occupantForm.setName("default_occupant_web");
        occupantForm.setModule(occupantModule);
        occupantForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        occupantForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> occupantFormFields = new ArrayList<>();
        occupantFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        occupantFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 2, 2));
        occupantFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 2, 3));
        occupantFormFields.add(new FormField("occupantType", FacilioField.FieldDisplayType.SELECTBOX, "Occupant Type", FormField.Required.OPTIONAL, 3, 1));
        occupantFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, "tenant",4, 1));
        occupantFormFields.add(new FormField("isPortalAccessNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Portal Access Needed", FormField.Required.OPTIONAL, 5, 1));
        occupantFormFields.add(new FormField("locatedSpace", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Located Space", FormField.Required.OPTIONAL, 6, 1));
//        occupantForm.setFields(occupantFormFields);

        FormSection section = new FormSection("Default", 1, occupantFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        occupantForm.setSections(Collections.singletonList(section));
        occupantForm.setIsSystemForm(true);
        occupantForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(occupantForm);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> occupant = new ArrayList<FacilioView>();
        occupant.add(getAllOccupantsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.OCCUPANT);
        groupDetails.put("views", occupant);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllOccupantsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Occupants");

        return allView;
    }
}
