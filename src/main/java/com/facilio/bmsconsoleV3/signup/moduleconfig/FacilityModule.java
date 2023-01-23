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

public class FacilityModule extends BaseModuleConfig{
    public FacilityModule(){
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> facility = new ArrayList<FacilioView>();
        facility.add(getAllFacilityView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        groupDetails.put("views", facility);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFacilityView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Facility");
        allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);

        FacilioForm facilityCreationForm = new FacilioForm();
        facilityCreationForm.setDisplayName("Facility");
        facilityCreationForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY +"_web");
        facilityCreationForm.setModule(facilityModule);
        facilityCreationForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityCreationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> facilityCreationFormDefaultFields = new ArrayList<>();
        facilityCreationFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        facilityCreationFormDefaultFields.add(new FormField("facilityType", FacilioField.FieldDisplayType.SELECTBOX, "Facility Type", FormField.Required.OPTIONAL,2, 2));
        facilityCreationFormDefaultFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",2, 3));
        facilityCreationFormDefaultFields.add(new FormField("location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.OPTIONAL, "basespace", 3, 2));
        FormField category = new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL,3, 3);
        category.setAllowCreate(true);
        facilityCreationFormDefaultFields.add(category);
        facilityCreationFormDefaultFields.add(new FormField("manager", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility Manager", FormField.Required.OPTIONAL, "people",4, 1));
        facilityCreationFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL,5, 1));
        facilityCreationFormDefaultFields.add(new FormField("userGuidance", FacilioField.FieldDisplayType.TEXTAREA, "User Guidance", FormField.Required.OPTIONAL,6, 1));
        facilityCreationFormDefaultFields.add(new FormField("usageCapacity", FacilioField.FieldDisplayType.NUMBER, "Usage Capacity", FormField.Required.REQUIRED,7, 2));
        facilityCreationFormDefaultFields.add(new FormField("bookingAdvancePeriodInDays", FacilioField.FieldDisplayType.NUMBER, "Booking Advance In Days", FormField.Required.REQUIRED,7, 3));
        facilityCreationFormDefaultFields.add(new FormField("maxSlotBookingAllowed", FacilioField.FieldDisplayType.NUMBER, "Max Slot Bookings Allowed Per Booking", FormField.Required.REQUIRED,8, 2));
        facilityCreationFormDefaultFields.add(new FormField("maxAttendeeCountPerBooking", FacilioField.FieldDisplayType.NUMBER, "Maximum attendee Count Per Booking", FormField.Required.OPTIONAL,8, 3));
        facilityCreationFormDefaultFields.add(new FormField("isAttendeeListNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Attendee List Needed", FormField.Required.OPTIONAL,9, 2));
        facilityCreationFormDefaultFields.add(new FormField("isMultiBookingPerSlotAllowed", FacilioField.FieldDisplayType.DECISION_BOX, "Is Multi Booking Allowed Per slot", FormField.Required.OPTIONAL,9, 3));
        FormField slotGeneratedUptoField = new FormField("slotGeneratedUpto", FacilioField.FieldDisplayType.DATETIME, "Slot Generated Upto", FormField.Required.OPTIONAL,9, 1);
        slotGeneratedUptoField.setHideField(true);
        facilityCreationFormDefaultFields.add(slotGeneratedUptoField);
        facilityCreationFormDefaultFields.add(new FormField("allowCancellationBefore", FacilioField.FieldDisplayType.NUMBER, "Allow Cancellation Before (Days)", FormField.Required.REQUIRED,10, 1));
        facilityCreationFormDefaultFields.add(new FormField("isChargeable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Chargeable", FormField.Required.OPTIONAL,11, 1));
        facilityCreationFormDefaultFields.add(new FormField("pricePerSlot", FacilioField.FieldDisplayType.NUMBER, "Price Per slot", FormField.Required.OPTIONAL,12, 2));
        facilityCreationFormDefaultFields.add(new FormField("securityDeposit", FacilioField.FieldDisplayType.NUMBER, "Security Deposit", FormField.Required.OPTIONAL,12, 3));
        facilityCreationFormDefaultFields.add(new FormField("slotDuration", FacilioField.FieldDisplayType.DURATION, "Slot Duration", FormField.Required.REQUIRED,13, 1));
        facilityCreationFormDefaultFields.add(new FormField("amenities", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Features / Amenties", FormField.Required.OPTIONAL,14, 1));

        List<FormField> slotAvailabilityFields = new ArrayList<>();
        slotAvailabilityFields.add(new FormField("facilityWeekdayAvailability", FacilioField.FieldDisplayType.FACILITY_AVAILABILITY, "Availability", FormField.Required.REQUIRED,15, 1));

        List<FormField> facilityCreationFormFields = new ArrayList<>();
        facilityCreationFormFields.addAll(facilityCreationFormDefaultFields);
        facilityCreationFormFields.addAll(slotAvailabilityFields);

//        facilityCreationForm.setFields(facilityCreationFormFields);

        FormSection defaultSection = new FormSection("DETAILS", 1, facilityCreationFormDefaultFields, false);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection checklistSection = new FormSection("AVAILABILITY", 2, slotAvailabilityFields, true);
        checklistSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(checklistSection);

        facilityCreationForm.setSections(sections);
        facilityCreationForm.setIsSystemForm(true);
        facilityCreationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(facilityCreationForm);
    }
}
