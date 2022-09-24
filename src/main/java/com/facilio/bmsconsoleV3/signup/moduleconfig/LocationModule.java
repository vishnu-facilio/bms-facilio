package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class LocationModule extends BaseModuleConfig{

    public LocationModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.LOCATION);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule locationModule = modBean.getModule(FacilioConstants.ContextNames.LOCATION);

        FacilioForm locationForm = new FacilioForm();
        locationForm.setDisplayName("NEW LOCATION");
        locationForm.setName("web_default");
        locationForm.setModule(locationModule);
        locationForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        locationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> locationFormFields = new ArrayList<>();
        locationFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        locationFormFields.add(new FormField("street", FacilioField.FieldDisplayType.TEXTBOX, "Street", FormField.Required.OPTIONAL, 2, 1));
        locationFormFields.add(new FormField("city", FacilioField.FieldDisplayType.TEXTBOX, "City", FormField.Required.OPTIONAL, 3, 1));
        locationFormFields.add(new FormField("state", FacilioField.FieldDisplayType.TEXTBOX, "State / Province", FormField.Required.OPTIONAL, 4, 1));
        locationFormFields.add(new FormField("zip", FacilioField.FieldDisplayType.TEXTBOX, "Zip / Postal Code", FormField.Required.OPTIONAL, 5, 1));
        locationFormFields.add(new FormField("country", FacilioField.FieldDisplayType.SELECTBOX, "Country", FormField.Required.OPTIONAL, 6, 1));
        locationFormFields.add(new FormField("lat", FacilioField.FieldDisplayType.DECIMAL, "Latitude", FormField.Required.OPTIONAL, 7, 1));
        locationFormFields.add(new FormField("lng", FacilioField.FieldDisplayType.DECIMAL, "Longitude", FormField.Required.OPTIONAL, 8, 1));
//        locationForm.setFields(locationFormFields);

        FormSection section = new FormSection("Default", 1, locationFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        locationForm.setSections(Collections.singletonList(section));
        locationForm.setIsSystemForm(true);
        locationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(locationForm);
    }
}
