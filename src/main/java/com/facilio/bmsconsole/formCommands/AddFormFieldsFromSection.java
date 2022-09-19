package com.facilio.bmsconsole.formCommands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddFormFieldsFromSection extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

        List<FormSection> newSections = new ArrayList<>();
        for (FormSection section : form.getSections()) {
            List<FormField> formFields = FormsAPI.getFormFieldsFromSections(Collections.singletonList(section));
            List<FormField> newFormFields = new ArrayList<>();
            for (FormField formField : formFields) {
                if (formField.getName() != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioField field = modBean.getField(formField.getName(), form.getModule().getName());
                    if (field != null) {
                        formField.setField(field);
                        formField.setName(field.getName());
                        formField.setFieldId(field.getId());
                    }
                    newFormFields.add(formField);
                }
            }
            section.setFields(newFormFields);
            newSections.add(section);
        }
        form.setSections(newSections);

        return false;
    }
}
