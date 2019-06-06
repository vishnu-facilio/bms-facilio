package com.facilio.bmsconsole.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AddFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
		}
		
		FormsAPI.createForm(form, module);
		
		return false;
	}
	

}
