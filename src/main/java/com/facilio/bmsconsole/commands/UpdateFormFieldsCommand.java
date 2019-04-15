package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateFormFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		FormsAPI.deleteFormFields(form.getId());
		FormsAPI.addFormFields(form.getId(), form);
		return false;
	}

}
