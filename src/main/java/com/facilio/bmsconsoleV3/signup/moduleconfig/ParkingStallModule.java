package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ParkingStallModule extends BaseModuleConfig{
    public ParkingStallModule(){
        setModuleName(FacilioConstants.ContextNames.PARKING_STALL);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> parkingStall = new ArrayList<FacilioView>();
        parkingStall.add(getAllParkingStallView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PARKING_STALL);
        groupDetails.put("views", parkingStall);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllParkingStallView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Parking Stalls");
        allView.setModuleName(FacilioConstants.ContextNames.PARKING_STALL);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parkingStallModule = modBean.getModule(FacilioConstants.ContextNames.PARKING_STALL);

        FacilioForm parkingStallForm = new FacilioForm();
        parkingStallForm.setDisplayName("NEW PARKING STALL");
        parkingStallForm.setName("default_parking_stall_web");
        parkingStallForm.setModule(parkingStallModule);
        parkingStallForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        parkingStallForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> parkingStallFormFields = new ArrayList<>();
        parkingStallFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        parkingStallFormFields.add(new FormField("parkingType", FacilioField.FieldDisplayType.SELECTBOX, "Parking Type", FormField.Required.REQUIRED, 2, 2));
        parkingStallFormFields.add(new FormField("parkingMode", FacilioField.FieldDisplayType.SELECTBOX, "Parking Mode", FormField.Required.REQUIRED, 3, 2));
        parkingStallFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        parkingStallFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 5, 2));
        parkingStallFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 6, 2));
        parkingStallFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.OPTIONAL,"employee", 7, 2));

        FormSection parkingStallFormSection = new FormSection("Default", 1, parkingStallFormFields, false);
        parkingStallFormSection.setSectionType(FormSection.SectionType.FIELDS);
        parkingStallForm.setSections(Collections.singletonList(parkingStallFormSection));
        parkingStallForm.setIsSystemForm(true);
        parkingStallForm.setType(FacilioForm.Type.FORM);

        FacilioForm parkingStallPortalForm = new FacilioForm();
        parkingStallPortalForm.setDisplayName("NEW PARKING STALL");
        parkingStallPortalForm.setName("default_parking_stall_portal");
        parkingStallPortalForm.setModule(parkingStallModule);
        parkingStallPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        parkingStallPortalForm.setFields(parkingStallFormFields);
        parkingStallPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> parkingStallPortalFormFields = new ArrayList<>();
        parkingStallPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        parkingStallPortalFormFields.add(new FormField("parkingType", FacilioField.FieldDisplayType.SELECTBOX, "Parking Type", FormField.Required.REQUIRED, 2, 2));
        parkingStallPortalFormFields.add(new FormField("parkingMode", FacilioField.FieldDisplayType.SELECTBOX, "Parking Mode", FormField.Required.REQUIRED, 3, 2));
        parkingStallPortalFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        parkingStallPortalFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 5, 2));
        parkingStallPortalFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 6, 2));
        parkingStallPortalFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.OPTIONAL,"employee", 7, 2));

        FormSection parkingStallPortalFormSection = new FormSection("Default", 1, parkingStallPortalFormFields, false);
        parkingStallPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        parkingStallPortalForm.setSections(Collections.singletonList(parkingStallPortalFormSection));
        parkingStallPortalForm.setIsSystemForm(true);
        parkingStallPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> parkingStallModuleForms = new ArrayList<>();
        parkingStallModuleForms.add(parkingStallForm);
        parkingStallModuleForms.add(parkingStallPortalForm);

        return parkingStallModuleForms;
    }
}
