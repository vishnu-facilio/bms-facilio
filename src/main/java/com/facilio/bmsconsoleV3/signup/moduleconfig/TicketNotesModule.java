package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TicketNotesModule extends BaseModuleConfig {

    public TicketNotesModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.TICKET_NOTES);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule ticketNotesModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_NOTES);

        FacilioForm ticketNotesForm = new FacilioForm();
        ticketNotesForm.setName("default_ticketnotes_web");
        ticketNotesForm.setModule(ticketNotesModule);
        ticketNotesForm.setDisplayName("Add Notes");
        ticketNotesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        ticketNotesForm.setShowInWeb(true);
        ticketNotesForm.setShowInMobile(true);
        ticketNotesForm.setHideInList(false);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ticketNotesModule.getName()));

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

        ticketNotesForm.setSections(sections);

        return Collections.singletonList(ticketNotesForm);
    }
}
