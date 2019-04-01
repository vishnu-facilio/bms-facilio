package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		form.setName(form.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		if (form.getLabelPositionEnum() == null) {
			form.setLabelPosition(LabelPosition.LEFT);
		}
		
		if (form.getFields() == null) {
			FacilioForm defaultForm = FormFactory.getDefaultForm(moduleName, form);
			List<FormField> formFields = new ArrayList<>();
			for (FormField f : defaultForm.getFields()) {
				FormField formField = FieldUtil.cloneBean(f, FormField.class);
				FacilioField field = modBean.getField(formField.getName(), moduleName);
				if (field != null) {
					f.setFieldId(field.getFieldId());
				}
				formFields.add(formField);
			}
			form.setFields(formFields);
		}
		
		FormsAPI.createForm(form, module);
		
		return false;
	}

}
