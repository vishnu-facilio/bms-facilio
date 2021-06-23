package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteFormCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		int count = FormsAPI.deleteForms(Collections.singletonList(formId));
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		
		return false;
	}

}
