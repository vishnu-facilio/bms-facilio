package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class FacilityBookingModule extends BaseModuleConfig{
    public FacilityBookingModule(){
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
    }
    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> facilityBooking = new ArrayList<FacilioView>();
        facilityBooking.add(getAllFacilityBookingView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        groupDetails.put("views", facilityBooking);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFacilityBookingView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Facility Booking");
        allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilityBookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

        FacilioForm facilityBookingForm = new FacilioForm();
        facilityBookingForm.setDisplayName("Booking");
        facilityBookingForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_web");
        facilityBookingForm.setModule(facilityBookingModule);
        facilityBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> facilityBookingFormDefaultFields = new ArrayList<>();
        facilityBookingFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilityBookingFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));
        facilityBookingFormDefaultFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,3, 1));

        List<FormField> timeSlotFields = new ArrayList<>();
        timeSlotFields.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> attendeeFields = new ArrayList<>();
        attendeeFields.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,5, 1));

        List<FormField> facilityBookingFormFields = new ArrayList<>();
        facilityBookingFormFields.addAll(facilityBookingFormDefaultFields);
        facilityBookingFormFields.addAll(timeSlotFields);
        facilityBookingFormFields.addAll(attendeeFields);
//        facilityBookingForm.setFields(facilityBookingFormFields);

        FormSection defaultSection = new FormSection("DETAILS", 1, facilityBookingFormDefaultFields, false);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection = new FormSection("TIME SLOTS", 2, timeSlotFields, false);
        timeSlotSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection attendeeSection = new FormSection("ATTENDEES", 3, attendeeFields, true);
        attendeeSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(timeSlotSection);
        sections.add(attendeeSection);

        facilityBookingForm.setSections(sections);
        facilityBookingForm.setIsSystemForm(true);
        facilityBookingForm.setType(FacilioForm.Type.FORM);

        FacilioForm facilityBookingPortalForm = new FacilioForm();
        facilityBookingPortalForm.setDisplayName("Booking");
        facilityBookingPortalForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_portal");
        facilityBookingPortalForm.setModule(facilityBookingModule);
        facilityBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> facilityBookingPortalFormDefaultFields = new ArrayList<>();
        facilityBookingPortalFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilityBookingPortalFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));
        facilityBookingPortalFormDefaultFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,3, 1));
        facilityBookingPortalFormDefaultFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 6, 1));

        List<FormField> timeSlotFields1 = new ArrayList<>();
        timeSlotFields1.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> attendeeFields1 = new ArrayList<>();
        attendeeFields1.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,5, 1));

        List<FormField> facilityBookingFormFields1 = new ArrayList<>();
        facilityBookingFormFields1.addAll(facilityBookingPortalFormDefaultFields);
        facilityBookingFormFields1.addAll(timeSlotFields1);
        facilityBookingFormFields1.addAll(attendeeFields1);
//        facilityBookingPortalForm.setFields(facilityBookingFormFields1);

        FormSection defaultSection1 = new FormSection("DETAILS", 1, facilityBookingPortalFormDefaultFields, false);
        defaultSection1.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection1 = new FormSection("TIME SLOTS", 2, timeSlotFields1, false);
        timeSlotSection1.setSectionType(FormSection.SectionType.FIELDS);

        FormSection attendeeSection1 = new FormSection("ATTENDEES", 3, attendeeFields1, true);
        attendeeSection1.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections1 = new ArrayList<>();
        sections1.add(defaultSection1);
        sections1.add(timeSlotSection1);
        sections1.add(attendeeSection1);

        facilityBookingPortalForm.setSections(sections1);
        facilityBookingPortalForm.setIsSystemForm(true);
        facilityBookingPortalForm.setType(FacilioForm.Type.FORM);


        FacilioForm facilityHotDeskBookingForm = new FacilioForm();
        facilityHotDeskBookingForm.setDisplayName("Hot Desk Booking");
        facilityHotDeskBookingForm.setName("hot_desk_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_web");
        facilityHotDeskBookingForm.setModule(facilityBookingModule);
        facilityHotDeskBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityHotDeskBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> facilityHotDeskBookingFormDefaultFields = new ArrayList<>();
        facilityHotDeskBookingFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilityHotDeskBookingFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));


        List<FormField> timeSlotFields2 = new ArrayList<>();
        timeSlotFields2.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> facilityBookingFormFields2 = new ArrayList<>();
        facilityBookingFormFields2.addAll(facilityHotDeskBookingFormDefaultFields);
        facilityBookingFormFields2.addAll(timeSlotFields2);
//        facilityHotDeskBookingForm.setFields(facilityBookingFormFields2);

        FormSection defaultSection2 = new FormSection("DETAILS", 1, facilityHotDeskBookingFormDefaultFields, false);
        defaultSection2.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection2 = new FormSection("TIME SLOTS", 2, timeSlotFields2, false);
        timeSlotSection2.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections2 = new ArrayList<>();
        sections2.add(defaultSection2);
        sections2.add(timeSlotSection2);

        facilityHotDeskBookingForm.setSections(sections2);
        facilityHotDeskBookingForm.setIsSystemForm(true);
        facilityHotDeskBookingForm.setType(FacilioForm.Type.FORM);


        FacilioForm facilitySpaceBookingForm = new FacilioForm();
        facilitySpaceBookingForm.setDisplayName("Space Booking");
        facilitySpaceBookingForm.setName("Space_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_web");
        facilitySpaceBookingForm.setModule(facilityBookingModule);
        facilitySpaceBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilitySpaceBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> facilitySpaceBookingFormDefaultFields = new ArrayList<>();
        facilitySpaceBookingFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilitySpaceBookingFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"user",2, 1));
        facilitySpaceBookingFormDefaultFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,3, 1));

        List<FormField> timeSlotFields3 = new ArrayList<>();
        timeSlotFields3.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> attendeeFields3 = new ArrayList<>();
        attendeeFields3.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,5, 1));

        List<FormField> facilitySpaceBookingFormFields = new ArrayList<>();
        facilitySpaceBookingFormFields.addAll(facilitySpaceBookingFormDefaultFields);
        facilitySpaceBookingFormFields.addAll(timeSlotFields3);
        facilitySpaceBookingFormFields.addAll(attendeeFields3);
//        facilitySpaceBookingForm.setFields(facilitySpaceBookingFormFields);

        FormSection defaultSection3 = new FormSection("DETAILS", 1, facilitySpaceBookingFormDefaultFields, false);
        defaultSection3.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection3 = new FormSection("TIME SLOTS", 2, timeSlotFields3, false);
        timeSlotSection3.setSectionType(FormSection.SectionType.FIELDS);

        FormSection attendeeSection3 = new FormSection("ATTENDEES", 3, attendeeFields3, true);
        attendeeSection3.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections3 = new ArrayList<>();
        sections3.add(defaultSection3);
        sections3.add(timeSlotSection3);
        sections3.add(attendeeSection3);

        facilitySpaceBookingForm.setSections(sections3);
        facilitySpaceBookingForm.setIsSystemForm(true);
        facilitySpaceBookingForm.setType(FacilioForm.Type.FORM);

        FacilioForm facilityHotDeskBookingPortalForm = new FacilioForm();
        facilityHotDeskBookingPortalForm.setDisplayName("Hot Desk Booking");
        facilityHotDeskBookingPortalForm.setName("hot_desk_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_portal");
        facilityHotDeskBookingPortalForm.setModule(facilityBookingModule);
        facilityHotDeskBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilityHotDeskBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> facilityHotDeskBookingPortalFormDefaultFields = new ArrayList<>();
        facilityHotDeskBookingPortalFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilityHotDeskBookingPortalFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));

        List<FormField> timeSlotFields4 = new ArrayList<>();
        timeSlotFields4.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> facilityHotDeskBookingPortalFormFields = new ArrayList<>();
        facilityHotDeskBookingPortalFormFields.addAll(facilityHotDeskBookingPortalFormDefaultFields);
        facilityHotDeskBookingPortalFormFields.addAll(timeSlotFields4);
//        facilityHotDeskBookingPortalForm.setFields(facilityHotDeskBookingPortalFormFields);

        FormSection defaultSection4 = new FormSection("DETAILS", 1, facilityHotDeskBookingPortalFormDefaultFields, false);
        defaultSection4.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection4 = new FormSection("TIME SLOTS", 2, timeSlotFields4, false);
        timeSlotSection4.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections4 = new ArrayList<>();
        sections4.add(defaultSection4);
        sections4.add(timeSlotSection4);

        facilityHotDeskBookingPortalForm.setSections(sections4);
        facilityHotDeskBookingPortalForm.setIsSystemForm(true);
        facilityHotDeskBookingPortalForm.setType(FacilioForm.Type.FORM);


        FacilioForm facilitySpaceBookingPortalForm = new FacilioForm();
        facilitySpaceBookingPortalForm.setDisplayName("Space Booking");
        facilitySpaceBookingPortalForm.setName("Space_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING +"_portal");
        facilitySpaceBookingPortalForm.setModule(facilityBookingModule);
        facilitySpaceBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilitySpaceBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> facilitySpaceBookingPortalFormDefaultFields = new ArrayList<>();
        facilitySpaceBookingPortalFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        facilitySpaceBookingPortalFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"user",2, 1));
        facilitySpaceBookingPortalFormDefaultFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,3, 1));

        List<FormField> timeSlotFields5 = new ArrayList<>();
        timeSlotFields5.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> attendeeFields5 = new ArrayList<>();
        attendeeFields5.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,5, 1));

        List<FormField> facilitySpaceBookingPortalFormFields = new ArrayList<>();
        facilitySpaceBookingPortalFormFields.addAll(facilitySpaceBookingPortalFormDefaultFields);
        facilitySpaceBookingPortalFormFields.addAll(timeSlotFields5);
        facilitySpaceBookingPortalFormFields.addAll(attendeeFields5);
//        facilitySpaceBookingPortalForm.setFields(facilitySpaceBookingPortalFormFields);

        FormSection defaultSection5 = new FormSection("DETAILS", 1, facilitySpaceBookingPortalFormDefaultFields, false);
        defaultSection5.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection5 = new FormSection("TIME SLOTS", 2, timeSlotFields5, false);
        timeSlotSection5.setSectionType(FormSection.SectionType.FIELDS);

        FormSection attendeeSection5 = new FormSection("ATTENDEES", 3, attendeeFields5, true);
        attendeeSection5.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections5 = new ArrayList<>();
        sections5.add(defaultSection5);
        sections5.add(timeSlotSection5);
        sections5.add(attendeeSection5);

        facilitySpaceBookingPortalForm.setSections(sections5);
        facilitySpaceBookingPortalForm.setIsSystemForm(true);
        facilitySpaceBookingPortalForm.setType(FacilioForm.Type.FORM);


        FacilioForm parkingBookingForm = new FacilioForm();
        parkingBookingForm.setDisplayName("Parking Booking");
        parkingBookingForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.PARKING_BOOKING +"_web");
        parkingBookingForm.setModule(facilityBookingModule);
        parkingBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        parkingBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> parkingBookingFormDefaultFields = new ArrayList<>();
        parkingBookingFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        parkingBookingFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));

        List<FormField> timeSlotFields6 = new ArrayList<>();
        timeSlotFields6.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> parkingBookingFormFields = new ArrayList<>();
        parkingBookingFormFields.addAll(parkingBookingFormDefaultFields);
        parkingBookingFormFields.addAll(timeSlotFields6);
//        parkingBookingForm.setFields(parkingBookingFormFields);

        FormSection defaultSection6 = new FormSection("DETAILS", 1, parkingBookingFormDefaultFields, false);
        defaultSection6.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection6 = new FormSection("TIME SLOTS", 2, timeSlotFields6, false);
        timeSlotSection6.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections6 = new ArrayList<>();
        sections6.add(defaultSection6);
        sections6.add(timeSlotSection6);

        parkingBookingForm.setSections(sections6);
        parkingBookingForm.setIsSystemForm(true);
        parkingBookingForm.setType(FacilioForm.Type.FORM);

        FacilioForm parkingBookingPortalForm = new FacilioForm();
        parkingBookingPortalForm.setDisplayName("Parking Booking");
        parkingBookingPortalForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.PARKING_BOOKING +"_portal");
        parkingBookingPortalForm.setModule(facilityBookingModule);
        parkingBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        parkingBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> parkingBookingPortalFormDefaultFields = new ArrayList<>();
        parkingBookingPortalFormDefaultFields.add(new FormField("facility", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Facility", FormField.Required.REQUIRED, "facility",1, 1));
        parkingBookingPortalFormDefaultFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", FormField.Required.REQUIRED,"user",2, 1));
        parkingBookingPortalFormDefaultFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 6, 1));

        List<FormField> timeSlotFields7 = new ArrayList<>();
        timeSlotFields7.add(new FormField("bookingslot", FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", FormField.Required.REQUIRED,4, 1));

        List<FormField> parkingBookingPortalFormFields = new ArrayList<>();
        parkingBookingPortalFormFields.addAll(parkingBookingPortalFormDefaultFields);
        parkingBookingPortalFormFields.addAll(timeSlotFields7);
//        parkingBookingPortalForm.setFields(parkingBookingPortalFormFields);

        FormSection defaultSection7 = new FormSection("DETAILS", 1, parkingBookingPortalFormDefaultFields, false);
        defaultSection7.setSectionType(FormSection.SectionType.FIELDS);

        FormSection timeSlotSection7 = new FormSection("TIME SLOTS", 2, timeSlotFields7, false);
        timeSlotSection7.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections7 = new ArrayList<>();
        sections7.add(defaultSection7);
        sections7.add(timeSlotSection7);

        parkingBookingPortalForm.setSections(sections7);
        parkingBookingPortalForm.setIsSystemForm(true);
        parkingBookingPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> facilityBookingModuleForms = new ArrayList<>();
        facilityBookingModuleForms.add(facilityBookingForm);
        facilityBookingModuleForms.add(facilityBookingPortalForm);
        facilityBookingModuleForms.add(facilityHotDeskBookingForm);
        facilityBookingModuleForms.add(facilitySpaceBookingForm);
        facilityBookingModuleForms.add(facilityHotDeskBookingPortalForm);
        facilityBookingModuleForms.add(facilitySpaceBookingPortalForm);
        facilityBookingModuleForms.add(parkingBookingForm);
        facilityBookingModuleForms.add(parkingBookingPortalForm);

        return facilityBookingModuleForms;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        FacilioModule module= ModuleFactory.getFacilityBookingModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,buildFacilityBookingPage(app, module, false,true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> buildFacilityBookingPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";

        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("bookinginfo", null, PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("bookinginfo", null, null)
                .addWidget("bookinginfowidget", null, PageWidget.WidgetType.BOOKING_INFO, "webbookinginfo_1_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("bookingslotinformation", null, null)
                .addWidget("bookingslotinformationwidget", "Slot Information", PageWidget.WidgetType.BOOKING_SLOT_INFORMATION, "flexiblewebbookinginfo_3", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("bookinginternalattendees", null, null)
                .addWidget("bookinginternalattendeeswidget", "Internal Attendee List", PageWidget.WidgetType.BOOKING_INTERNAL_ATTENDEES, "flexiblewebbookinginternalattendees_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("bookingexternalattendees", null, null)
                .addWidget("bookingexternalattendeeswidget", "External Attendee List", PageWidget.WidgetType.BOOKING_EXTERNAL_ATTENDEES, "flexiblewebbookingexternalattendees_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext cancelBooking = new SystemButtonRuleContext();
        cancelBooking.setName("Cancel Booking");
        cancelBooking.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        cancelBooking.setIdentifier("cancelBooking");
        cancelBooking.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());

        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,cancelBooking);

        //List Page System Buttons

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("New Booking");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,createButton);
        
    }
}
