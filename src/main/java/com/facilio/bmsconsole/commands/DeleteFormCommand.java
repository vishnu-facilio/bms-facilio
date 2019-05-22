package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		int count = FormsAPI.deleteForms(Collections.singletonList(formId));
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		
		return false;
	}

}
