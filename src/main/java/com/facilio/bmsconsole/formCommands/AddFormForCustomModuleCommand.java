package com.facilio.bmsconsole.formCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class AddFormForCustomModuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module.isCustom()) {
            List<FormField> formFields = new ArrayList<>();
            List<FormField> photoFields = new ArrayList<>();
//		List<FormField> siteFields = new ArrayList<>();
            List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);

            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
            FacilioField field = fieldsAsMap.get("name");
            if (field != null) {
                FormField formField = new FormField(field.getId(), field.getName(), field.getDisplayType(), field.getDisplayName(), Required.OPTIONAL, 0, 1);
                formFields.add(formField);
            }
            FacilioField photoField = fieldsAsMap.get("photo");
            if (photoField != null) {
                FormField formField = new FormField(photoField.getId(), photoField.getName(), photoField.getDisplayType(), photoField.getDisplayName(), Required.OPTIONAL, 0, 1);
                photoFields.add(formField);
            }

            FacilioField siteIdField = fieldsAsMap.get("siteId");
            if (siteIdField != null) {
                FormField formField = new FormField(siteIdField.getId(), siteIdField.getName(), siteIdField.getDisplayType(), siteIdField.getDisplayName(), Required.OPTIONAL, 0, 1);
                formFields.add(formField);
            }

            List<FormSection> sections = new ArrayList<>();
            FormSection photoSection = new FormSection("", 0, photoFields, false);
            sections.add(photoSection);
            FormSection formSection = new FormSection("Untitled", 1, formFields, true);
            sections.add(formSection);

            if (form.getSections() != null && CollectionUtils.isNotEmpty(form.getSections())) {
                form.setSections(form.getSections());
            }
            else {
                form.setSections((sections));
            }
            FormsAPI.createForm(form, module);
        }
        return false;
    }
}

