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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VendorsNotesModule extends BaseModuleConfig{

    public VendorsNotesModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.VENDOR_NOTES);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorsNotesModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_NOTES);


        FacilioForm vendorNotesForm = new FacilioForm();
        vendorNotesForm.setName("default_vendorNotes_web");
        vendorNotesForm.setModule(vendorsNotesModule);
        vendorNotesForm.setDisplayName("Add Notes");
        vendorNotesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        vendorNotesForm.setShowInWeb(true);
        vendorNotesForm.setShowInMobile(true);
        vendorNotesForm.setHideInList(false);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(vendorsNotesModule.getName()));

        List<FormSection> sections = new ArrayList<FormSection>();

        FormSection configSection = new FormSection();
        configSection.setName("Default");
        configSection.setSectionType(FormSection.SectionType.FIELDS);
        configSection.setShowLabel(false);

        List<FormField> configFields = new ArrayList<>();

        int seq = 0;

        configFields.add(new FormField(fieldMap.get("body").getFieldId(), "body", FacilioField.FieldDisplayType.TEXTAREA, "Comment", FormField.Required.OPTIONAL, ++seq, 1));

        configSection.setFields(configFields);

        configSection.setSequenceNumber(1);

        sections.add(configSection);

        vendorNotesForm.setSections(sections);

        return Collections.singletonList(vendorNotesForm);
    }

}
