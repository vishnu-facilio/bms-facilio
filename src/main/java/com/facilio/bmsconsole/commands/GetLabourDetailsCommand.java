package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.constants.FacilioConstants;

public class GetLabourDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			LabourContext labour = (LabourContext) context.get(FacilioConstants.ContextNames.RECORD);
			context.put(FacilioConstants.ContextNames.LABOUR, labour);
		}
		return false;
	}

}