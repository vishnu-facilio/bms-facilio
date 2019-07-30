package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
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
		form.setName("standard_" + module.getName());
		form.setDisplayName("Standard");
		form.setFormType(FormType.WEB);
		form.setLabelPosition(LabelPosition.LEFT);
		
		List<FormField> formFields = new ArrayList<>();
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		if (fields != null) {
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			FacilioField field = fieldsAsMap.get("name");
			if (field != null) {
				FormField formField = new FormField(field.getId(), field.getName(), field.getDisplayType(), field.getDisplayName(), Required.OPTIONAL, 0, 1);
				formFields.add(formField);
			}
		}
		FormSection formSection = new FormSection("Untitled", 0, formFields, true);
		form.setSections(Collections.singletonList(formSection));
		
		FormsAPI.createForm(form, module);
		context.put(FacilioConstants.ContextNames.FORM, form);
		return false;
	}

}
