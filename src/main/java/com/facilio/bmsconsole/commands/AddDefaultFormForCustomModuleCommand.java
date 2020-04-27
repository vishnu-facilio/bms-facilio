package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class AddDefaultFormForCustomModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
		// creating Standard form for the new module
		FacilioForm form = new FacilioForm();
		String defaultFormName = FormFactory.getDefaultFormName(module.getName(), FormType.WEB);
		form.setName(defaultFormName);
		form.setDisplayName("Standard");
		form.setFormType(FormType.WEB);
		form.setLabelPosition(LabelPosition.LEFT);
		
		List<FormField> formFields = new ArrayList<>();
		List<FormField> photoFields = new ArrayList<>();
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		if (fields != null) {
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
		}
		List<FormSection> sections = new ArrayList<>();
		FormSection photoSection = new FormSection("", 0, photoFields, false);
		sections.add(photoSection);
		FormSection formSection = new FormSection("Untitled", 1, formFields, true);
		sections.add(formSection);
		
		form.setSections((sections));
		FormsAPI.createForm(form, module);
		context.put(FacilioConstants.ContextNames.FORM, form);
		return false;
	}

}
