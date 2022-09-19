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

public class FacilityBookingExternalAttendeeModule extends BaseModuleConfig{

    public FacilityBookingExternalAttendeeModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE);
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilityBookingExternalAttendeeModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE);

        FacilioForm externalAttendeeForm = new FacilioForm();
        externalAttendeeForm.setDisplayName("External Attendee");
        externalAttendeeForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE +"_web");
        externalAttendeeForm.setModule(facilityBookingExternalAttendeeModule);
        externalAttendeeForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        externalAttendeeForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> externalAttendeeFormFields = new ArrayList<>();
        externalAttendeeFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        externalAttendeeFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL,2, 1));
        externalAttendeeFormFields.add(new FormField("Phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL,3, 1));
        externalAttendeeFormFields.add(new FormField("facilityBooking", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Booking", FormField.Required.REQUIRED,4, 1));
//        externalAttendeeForm.setFields(externalAttendeeFormFields);

        FormSection section = new FormSection("Default", 1, externalAttendeeFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        externalAttendeeForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(externalAttendeeForm);
    }

}
