package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormField;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;

import java.util.Collections;

public class ValidateFormFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FormField formField = (FormField) context.get(FacilioConstants.ContextNames.FORM_FIELD);
		
		if(formField == null)
		{
			throw new IllegalArgumentException("Form field is empty");
		}
		
		FacilioField baseField = formField.getField();
		if(baseField != null)
		{
			formField.setFieldId(baseField.getFieldId());	
			formField.setDisplayName(baseField.getDisplayName());	
			formField.setName(baseField.getName());	
			context.put(FacilioConstants.ContextNames.MODULE_FIELD, baseField);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(baseField));
		}
		else
		{
			throw new IllegalArgumentException("Field data is empty");
		}
		return false;
	}

}
