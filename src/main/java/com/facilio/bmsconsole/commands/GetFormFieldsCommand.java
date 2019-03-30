package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetFormFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		List<FormField> formFields = new ArrayList<>(); 
		int i = 1;
		for (FacilioField field: fields) {
			if (FieldFactory.Fields.workorderRequestFieldInclude.contains(field.getName())) {
				// Temp hack
				if (moduleName.equals("workorderrequest")) {
					if (field.getName().equals("resource")) {
						formFields.add(new FormField(field.getFieldId(),field.getName(), FieldDisplayType.WOASSETSPACECHOOSER, field.getDisplayName(), Required.OPTIONAL, i++, 1));
					} else if (field.getName().equals("requester")) {
						formFields.add(new FormField(field.getFieldId(), field.getName(), FieldDisplayType.REQUESTER, field.getDisplayName(), Required.REQUIRED, ++i, 1));
					} else if (field.getName().equals("urgency")) {
						formFields.add(new FormField(field.getFieldId(), field.getName(), FieldDisplayType.URGENCY, field.getDisplayName(), Required.OPTIONAL, ++i, 1));
					} else {
						formFields.add(new FormField(field.getFieldId(), field.getName(), field.getDisplayType(), field.getDisplayName(), Required.OPTIONAL, ++i, 1));	
					}
				} else {
					formFields.add(new FormField(field.getFieldId(), field.getName(), field.getDisplayType(), field.getDisplayName(), Required.OPTIONAL, ++i, 1));
				}
			}
		}
		// Temp hack
		if (moduleName.equals("workorderrequest")) {
			formFields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, ++i, 1));
		}
		context.put(FacilioConstants.ContextNames.FORM_FIELDS, formFields);
		return false;
	}
}
