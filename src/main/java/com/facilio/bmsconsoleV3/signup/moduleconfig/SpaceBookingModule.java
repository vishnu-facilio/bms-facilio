package com.facilio.bmsconsoleV3.signup.moduleconfig;

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

public class SpaceBookingModule extends BaseModuleConfig{

    public SpaceBookingModule(){
        setModuleName(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);

        FacilioForm spaceBookingModuleForm = new FacilioForm();
        spaceBookingModuleForm.setDisplayName("Space Booking Form");
        spaceBookingModuleForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING +"_web");
        spaceBookingModuleForm.setModule(spaceBookingModule);
        spaceBookingModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> spaceBookingModuleFormFields = new ArrayList<>();
        spaceBookingModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleFormFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.REQUIRED, "space", 5, 2));
        spaceBookingModuleFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));
//		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 9, 1));

        List<FormField> internalAttendeesFormFields = new ArrayList<>();
        internalAttendeesFormFields.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,8, 1));

        List<FormField> externalAttendeesFormFields = new ArrayList<>();
        externalAttendeesFormFields.add(new FormField("externalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "External Attendees", FormField.Required.OPTIONAL,9, 1));

        FormSection defaultSpaceBookingModuleFormFormSection = new FormSection("DETAILS", 1, spaceBookingModuleFormFields, false);
        defaultSpaceBookingModuleFormFormSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection internalAttendeesFormSection = new FormSection("INTERNAL ATTENDEES", 2, internalAttendeesFormFields, true);
        internalAttendeesFormSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection externalAttendeesFormSection = new FormSection("EXTERNAL ATTENDEES", 3, externalAttendeesFormFields, true);
        externalAttendeesFormSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> spaceBookingModuleFormSection = new ArrayList<>();
        spaceBookingModuleFormSection.add(defaultSpaceBookingModuleFormFormSection);
        spaceBookingModuleFormSection.add(internalAttendeesFormSection);
        spaceBookingModuleFormSection.add(externalAttendeesFormSection);
        spaceBookingModuleForm.setSections(spaceBookingModuleFormSection);
        spaceBookingModuleForm.setIsSystemForm(true);
        spaceBookingModuleForm.setType(FacilioForm.Type.FORM);




        FacilioForm spaceBookingModuleDeskBookingForm = new FacilioForm();
        spaceBookingModuleDeskBookingForm.setDisplayName("Desk Booking Form");
        spaceBookingModuleDeskBookingForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.DESK_BOOKING +"_web");
        spaceBookingModuleDeskBookingForm.setModule(spaceBookingModule);
        spaceBookingModuleDeskBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleDeskBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> spaceBookingModuleDeskBookingFormFields = new ArrayList<>();
        spaceBookingModuleDeskBookingFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("desk", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Desk", FormField.Required.REQUIRED, "desks", 5, 2));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleDeskBookingFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));

        FormSection spaceBookingModuleDeskBookingFormSection = new FormSection("Default", 1, spaceBookingModuleDeskBookingFormFields, false);
        spaceBookingModuleDeskBookingFormSection.setSectionType(FormSection.SectionType.FIELDS);
        spaceBookingModuleDeskBookingForm.setSections(Collections.singletonList(spaceBookingModuleDeskBookingFormSection));
        spaceBookingModuleDeskBookingForm.setIsSystemForm(true);
        spaceBookingModuleDeskBookingForm.setType(FacilioForm.Type.FORM);




        FacilioForm spaceBookingModuleParkingBookingForm = new FacilioForm();
        spaceBookingModuleParkingBookingForm.setDisplayName("Parking Booking Form");
        spaceBookingModuleParkingBookingForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.PARKING_BOOKING +"_web");
        spaceBookingModuleParkingBookingForm.setModule(spaceBookingModule);
        spaceBookingModuleParkingBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleParkingBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> spaceBookingModuleParkingBookingFormFields = new ArrayList<>();
        spaceBookingModuleParkingBookingFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("parkingStall", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parking", FormField.Required.REQUIRED, "parkingstall", 5, 2));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleParkingBookingFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));

        FormSection spaceBookingModuleParkingBookingFormSection = new FormSection("Default", 1, spaceBookingModuleParkingBookingFormFields, false);
        spaceBookingModuleParkingBookingFormSection.setSectionType(FormSection.SectionType.FIELDS);
        spaceBookingModuleParkingBookingForm.setSections(Collections.singletonList(spaceBookingModuleParkingBookingFormSection));
        spaceBookingModuleParkingBookingForm.setIsSystemForm(true);
        spaceBookingModuleParkingBookingForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> spaceBookingModuleForms = new ArrayList<>();
        spaceBookingModuleForms.add(spaceBookingModuleForm);
        spaceBookingModuleForms.add(spaceBookingModuleDeskBookingForm);
        spaceBookingModuleForms.add(spaceBookingModuleParkingBookingForm);

        return spaceBookingModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllSpaceBookingViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSpaceBookingViews() {

        List<SortField> sortFields = Collections.singletonList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Space Booking");
        allView.setModuleName(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}