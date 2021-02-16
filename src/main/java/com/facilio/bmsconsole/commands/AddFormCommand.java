package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

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
		} else if (module.isCustom()) {
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
				defaultForm = FormFactory.getForm(moduleName, defaultFormName);	// if form already present in factory
			}
			if (defaultForm == null) {
				defaultForm = FormsAPI.getDefaultForm(moduleName, form.getFormTypeEnum());
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
				FormSection section = new FormSection();
				section.addField(FormsAPI.getFormFieldFromFacilioField(modBean.getPrimaryField(moduleName), 1));
				if (FieldUtil.isSiteIdFieldPresent(module)) {
					section.addField(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
				}
				form.setSections(Collections.singletonList(section));
			}
		}
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
