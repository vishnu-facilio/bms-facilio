package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
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
		
		if (form.getName() == null) {
			form.setName(form.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		} else {
			form.setName(form.getName().toLowerCase().replaceAll("[^a-zA-Z0-9_]+", ""));
		}
		
		
		FacilioForm existingForm = FormsAPI.getFormFromDB(form.getName(), module);
		if (existingForm != null) {
			throw new IllegalArgumentException("Form with this name already exists");
		}
		
		if (form.getLabelPositionEnum() == null) {
			form.setLabelPosition(LabelPosition.LEFT);
		}
		
		if (form.getSections() == null) {
			FacilioForm defaultForm = FormsAPI.getDefaultForm(moduleName, form);
			if (defaultForm != null && CollectionUtils.isNotEmpty(defaultForm.getSections())) {
				form.setSections(new ArrayList<>(defaultForm.getSections()));
				for(FormSection section: form.getSections()) {
					FormsAPI.setFieldDetails(modBean, section.getFields(), moduleName);
				}
			}
			// for custom modules
			else {
				List<FacilioField> allFields = modBean.getAllFields(moduleName);
				List<FormField> formFields = new ArrayList<>();
				for (FacilioField field : allFields) {
					if (field.isDefault()) {
						FormField formField = new FormField(field.getId(), field.getName(), field.getDisplayType(), field.getDisplayName(), Required.REQUIRED, 0, 1);
						formFields.add(formField);
					}
				}
				FormSection formSection = new FormSection("Untitled", 0, formFields, true);
				form.setSections(Collections.singletonList(formSection));
			}
		}
		
		FormsAPI.createForm(form, module);
		
		return false;
	}
	

}
