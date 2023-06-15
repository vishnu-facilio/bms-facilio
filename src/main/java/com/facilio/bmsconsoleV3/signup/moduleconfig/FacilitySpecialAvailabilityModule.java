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

public class FacilitySpecialAvailabilityModule extends BaseModuleConfig{

    public FacilitySpecialAvailabilityModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilitySpecialAvailabilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY);

        FacilioForm facilitySpecialAvailabilityForm = new FacilioForm();
        facilitySpecialAvailabilityForm.setDisplayName("Special Availability");
        facilitySpecialAvailabilityForm.setName("default_"+ FacilioConstants.ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY +"_web");
        facilitySpecialAvailabilityForm.setModule(facilitySpecialAvailabilityModule);
        facilitySpecialAvailabilityForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        facilitySpecialAvailabilityForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> facilitySpecialAvailabilityFormFields = new ArrayList<>();
        facilitySpecialAvailabilityFormFields.add(new FormField("remarks", FacilioField.FieldDisplayType.TEXTBOX, "Remarks", FormField.Required.REQUIRED,1, 1));
        facilitySpecialAvailabilityFormFields.add(new FormField("startDate", FacilioField.FieldDisplayType.DATE, "Date", FormField.Required.REQUIRED,2, 1));
//        facilitySpecialAvailabilityFormFields.add(new FormField("endDate", FacilioField.FieldDisplayType.DATE, "End Date", FormField.Required.REQUIRED,2, 3));
        facilitySpecialAvailabilityFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.TIME, "Start Time", FormField.Required.REQUIRED,3, 2));
        facilitySpecialAvailabilityFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.TIME, "End Time", FormField.Required.REQUIRED,3, 2));
        facilitySpecialAvailabilityFormFields.add(new FormField("cost", FacilioField.FieldDisplayType.NUMBER, "Cost", FormField.Required.OPTIONAL,4, 2));
        facilitySpecialAvailabilityFormFields.add(new FormField("cancelOnCostChange", FacilioField.FieldDisplayType.DECISION_BOX, "Cancel Booking On Cost Change", FormField.Required.OPTIONAL,4, 3));
        facilitySpecialAvailabilityFormFields.add(new FormField("specialType", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.REQUIRED,4, 3));
//        facilitySpecialAvailabilityForm.setFields(facilitySpecialAvailabilityFormFields);

        FormSection section = new FormSection("Default", 1, facilitySpecialAvailabilityFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        facilitySpecialAvailabilityForm.setSections(Collections.singletonList(section));
        facilitySpecialAvailabilityForm.setIsSystemForm(true);
        facilitySpecialAvailabilityForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(facilitySpecialAvailabilityForm);
    }
}
