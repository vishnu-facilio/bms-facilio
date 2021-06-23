package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			TemplateAPI.deleteTemplate(id);
		}
		else {
			throw new IllegalArgumentException("ID cannot be null during deletion/ updation of template");
		}
		return false;
	}

}
