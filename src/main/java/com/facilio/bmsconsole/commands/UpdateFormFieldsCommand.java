package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateFormFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		
		FormsAPI.initFormFields(form);
		return false;
	}

}
