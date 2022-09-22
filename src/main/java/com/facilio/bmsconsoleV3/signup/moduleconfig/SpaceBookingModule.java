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
        spaceBookingModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

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


        FacilioForm spaceBookingModuleEmployeePortalForm = new FacilioForm();
        spaceBookingModuleEmployeePortalForm.setDisplayName("Space Booking Form");
        spaceBookingModuleEmployeePortalForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
        spaceBookingModuleEmployeePortalForm.setModule(spaceBookingModule);
        spaceBookingModuleEmployeePortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleEmployeePortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> spaceBookingModuleEmployeePortalFormFields = new ArrayList<>();
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.REQUIRED, "space", 5, 2));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleEmployeePortalFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));

        List<FormField> internalAttendeesPortalFormFields = new ArrayList<>();
        internalAttendeesPortalFormFields.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", FormField.Required.OPTIONAL,8, 1));

        List<FormField> externalAttendeesPortalFormFields = new ArrayList<>();
        externalAttendeesPortalFormFields.add(new FormField("externalAttendees", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "External Attendees", FormField.Required.OPTIONAL,9, 1));

        FormSection defaultSpaceBookingModulePortalFormFormSection = new FormSection("DETAILS", 1, spaceBookingModuleEmployeePortalFormFields, false);
        defaultSpaceBookingModulePortalFormFormSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection internalAttendeesPortalFormSection = new FormSection("INTERNAL ATTENDEES", 2, internalAttendeesPortalFormFields, true);
        internalAttendeesPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection externalAttendeesPortalFormSection = new FormSection("EXTERNAL ATTENDEES", 3, externalAttendeesPortalFormFields, true);
        externalAttendeesPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> spaceBookingModulePortalFormSection = new ArrayList<>();
        spaceBookingModulePortalFormSection.add(defaultSpaceBookingModulePortalFormFormSection);
        spaceBookingModulePortalFormSection.add(internalAttendeesPortalFormSection);
        spaceBookingModulePortalFormSection.add(externalAttendeesPortalFormSection);
        spaceBookingModuleEmployeePortalForm.setSections(spaceBookingModulePortalFormSection);


        FacilioForm spaceBookingModuleDeskBookingForm = new FacilioForm();
        spaceBookingModuleDeskBookingForm.setDisplayName("Desk Booking Form");
        spaceBookingModuleDeskBookingForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.DESK_BOOKING +"_web");
        spaceBookingModuleDeskBookingForm.setModule(spaceBookingModule);
        spaceBookingModuleDeskBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleDeskBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

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


        FacilioForm spaceBookingModuleDeskBookingPortalForm = new FacilioForm();
        spaceBookingModuleDeskBookingPortalForm.setDisplayName("Desk Booking Form");
        spaceBookingModuleDeskBookingPortalForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.DESK_BOOKING );
        spaceBookingModuleDeskBookingPortalForm.setModule(spaceBookingModule);
        spaceBookingModuleDeskBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleDeskBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> spaceBookingModuleDeskBookingPortalFormFields = new ArrayList<>();
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("desk", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Desk", FormField.Required.REQUIRED, "desks", 5, 2));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleDeskBookingPortalFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));

        FormSection spaceBookingModuleDeskBookingPortalFormSection = new FormSection("Default", 1, spaceBookingModuleDeskBookingPortalFormFields, false);
        spaceBookingModuleDeskBookingPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        spaceBookingModuleDeskBookingPortalForm.setSections(Collections.singletonList(spaceBookingModuleDeskBookingPortalFormSection));


        FacilioForm spaceBookingModuleParkingBookingForm = new FacilioForm();
        spaceBookingModuleParkingBookingForm.setDisplayName("Parking Booking Form");
        spaceBookingModuleParkingBookingForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.PARKING_BOOKING +"_web");
        spaceBookingModuleParkingBookingForm.setModule(spaceBookingModule);
        spaceBookingModuleParkingBookingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleParkingBookingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

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

        FacilioForm spaceBookingModuleParkingBookingPortalForm = new FacilioForm();
        spaceBookingModuleParkingBookingPortalForm.setDisplayName("Parking Booking Form");
        spaceBookingModuleParkingBookingPortalForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.PARKING_BOOKING);
        spaceBookingModuleParkingBookingPortalForm.setModule(spaceBookingModule);
        spaceBookingModuleParkingBookingPortalForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleParkingBookingPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> spaceBookingModuleParkingBookingPortalFormFields = new ArrayList<>();
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.REQUIRED,"people",3, 1));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("reservedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Reserved By", FormField.Required.REQUIRED,"people",4, 1));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("parkingStall", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parking", FormField.Required.REQUIRED, "parkingstall", 5, 2));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "Number Of Attendees", FormField.Required.REQUIRED,6, 1));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("bookingStartTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED,7, 2));
        spaceBookingModuleParkingBookingPortalFormFields.add(new FormField("bookingEndTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED,7, 3));

        FormSection spaceBookingModuleParkingBookingPortalFormSection = new FormSection("Default", 1, spaceBookingModuleParkingBookingPortalFormFields, false);
        spaceBookingModuleParkingBookingPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        spaceBookingModuleParkingBookingPortalForm.setSections(Collections.singletonList(spaceBookingModuleParkingBookingPortalFormSection));

        List<FacilioForm> spaceBookingModuleForms = new ArrayList<>();
        spaceBookingModuleForms.add(spaceBookingModuleForm);
        spaceBookingModuleForms.add(spaceBookingModuleEmployeePortalForm);
        spaceBookingModuleForms.add(spaceBookingModuleDeskBookingForm);
        spaceBookingModuleForms.add(spaceBookingModuleDeskBookingPortalForm);
        spaceBookingModuleForms.add(spaceBookingModuleParkingBookingForm);
        spaceBookingModuleForms.add(spaceBookingModuleParkingBookingPortalForm);

        return spaceBookingModuleForms;
    }
}
