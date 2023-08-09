package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class AmenityModule extends BaseModuleConfig{
    public AmenityModule(){
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> amenity = new ArrayList<FacilioView>();
        amenity.add(getAllAmenityView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FacilityBooking.AMENITY);
        groupDetails.put("views", amenity);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAmenityView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Amenity");
        allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule amenityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.AMENITY);

        FacilioForm amenityForm = new FacilioForm();
        amenityForm.setDisplayName("Amenity");
        amenityForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.AMENITY +"_web");
        amenityForm.setModule(amenityModule);
        amenityForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        amenityForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> amenityFormFields = new ArrayList<>();
        amenityFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        amenityFormFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL,2, 1));
        amenityFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL,3, 1));
        amenityFormFields.add(new FormField("amenityLogo", FacilioField.FieldDisplayType.SELECTBOX, "Amenity Logo", FormField.Required.OPTIONAL,4, 1));
//        amenityForm.setFields(amenityFormFields);

        FormSection section = new FormSection("Default", 1,amenityFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        amenityForm.setSections(Collections.singletonList(section));
        amenityForm.setIsSystemForm(true);
        amenityForm.setType(FacilioForm.Type.FORM);

        return  Collections.singletonList(amenityForm);
    }
}
