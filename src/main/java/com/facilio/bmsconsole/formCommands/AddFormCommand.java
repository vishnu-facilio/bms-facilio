package com.facilio.bmsconsole.formCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.facilio.bmsconsole.formFactory.FacilioFormChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddFormCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        String defaultFormName = null;
        if (form.getName() == null) {
            form.setName(form.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
        } else if (module !=null && module.isCustom()) {
            form.setName(form.getName().toLowerCase().replaceAll("[^a-zA-Z0-9_]+", ""));
        } else {
            defaultFormName = form.getName();
        }

        FacilioForm existingForm = FormsAPI.getFormFromDB(form.getName(), module);
        if (existingForm != null) {
            throw new IllegalArgumentException("Form with this name already exists");
        }

        if (form.getLabelPositionEnum() == null) {
            form.setLabelPosition(LabelPosition.LEFT);
        }

        if (form.getSections() == null && !module.isCustom()) {
            FacilioForm defaultForm = null;
            if (defaultFormName != null) {
                defaultForm = FormsAPI.getFormsFromDB(moduleName, defaultFormName);	// if form already present in factory
            }
            if (defaultForm == null) {
                defaultForm = FormsAPI.getDefaultForm(moduleName, form.getAppLinkName());
            }
            else {
                form = defaultForm;
            }
            if (defaultForm != null) {
                if (CollectionUtils.isNotEmpty(defaultForm.getSections())) {
                    form.setSections(new ArrayList<>(defaultForm.getSections()));
                    for(FormSection section: form.getSections()) {
                        FormsAPI.setFieldDetails(modBean, section.getFields(), moduleName);
                    }
                }
            }
            // For modules having no default form in form factory
            else {
                if(form.getSections()==null){
                FacilioChain newForm = FacilioFormChainFactory.getFormSectionChain();
                FacilioContext newFormContext =newForm.getContext();
                newFormContext.put(FacilioConstants.ContextNames.FORM,form);
                newForm.execute();
                form = (FacilioForm) newFormContext.get(FacilioConstants.ContextNames.FORM);}
//                FormSection section = new FormSection();
//                FormField primaryField = FormsAPI.getFormFieldFromFacilioField(modBean.getPrimaryField(moduleName), 1);
//                primaryField.setRequired(true);
//                section.addField(primaryField);
//                if (FieldUtil.isSiteIdFieldPresent(module)) {
//                    section.addField(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
//                }
//                form.setSections(Collections.singletonList(section));
            }
        }
        form.setPrimaryForm(Objects.equals(form.getDisplayName(), "Standard"));
        form.setIsSystemForm(form.getName().contains("default_") || form.getName().contains("_default") || form.getName().contains("multi_web_pm"));
        if (module.isCustom()) {
            List<FacilioField> fields = new ArrayList();
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
            context.put(FacilioConstants.ContextNames.MODULE, module);
        }
        else {
            FormsAPI.createForm(form, module);
        }

        return false;
    }


}
