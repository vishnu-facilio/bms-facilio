package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;


public class GenerateFormSectionForSubFormCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

        if(form!=null){
            FormSection formSection = new FormSection();
            formSection.setName("Subform");
            formSection.setShowLabel(true);
            formSection.setSubFormId(form.getId());
            formSection.setSectionType(FormSection.SectionType.SUB_FORM);
            context.put(FacilioConstants.ContextNames.FORM_SECTION,formSection);
        }

        return false;
    }
}
