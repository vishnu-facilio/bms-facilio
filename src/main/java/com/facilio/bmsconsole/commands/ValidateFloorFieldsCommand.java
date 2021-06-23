package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class ValidateFloorFieldsCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.get(FacilioConstants.ContextNames.FLOOR);
		return false;
	}

}
