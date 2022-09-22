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
import java.util.Collections;
import java.util.List;

public class ExternalAttendeeModule extends BaseModuleConfig{

    public ExternalAttendeeModule(){
        setModuleName(FacilioConstants.ContextNames.SpaceBooking.EXTERNAL_ATTENDEE);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule externalAttendeeModule = modBean.getModule(FacilioConstants.ContextNames.SpaceBooking.EXTERNAL_ATTENDEE);

        FacilioForm spaceBookingModuleExternalAttendeeForm = new FacilioForm();
        spaceBookingModuleExternalAttendeeForm.setDisplayName("External Attendee");
        spaceBookingModuleExternalAttendeeForm.setName("default_"+ FacilioConstants.ContextNames.SpaceBooking.EXTERNAL_ATTENDEE +"_web");
        spaceBookingModuleExternalAttendeeForm.setModule(externalAttendeeModule);
        spaceBookingModuleExternalAttendeeForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        spaceBookingModuleExternalAttendeeForm.setAppLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);

        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        fields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED,2, 1));
        fields.add(new FormField("Phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL,3, 1));

        FormSection spaceBookingModuleExternalAttendeeFormSection = new FormSection("Default", 1, fields, false);
        spaceBookingModuleExternalAttendeeFormSection.setSectionType(FormSection.SectionType.FIELDS);
        spaceBookingModuleExternalAttendeeForm.setSections(Collections.singletonList(spaceBookingModuleExternalAttendeeFormSection));

        return Collections.singletonList(spaceBookingModuleExternalAttendeeForm);
    }
}
