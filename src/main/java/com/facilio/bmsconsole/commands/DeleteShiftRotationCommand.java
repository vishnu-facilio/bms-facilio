package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteShiftRotationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		ShiftAPI.deleteShiftRotation(id);
		return false;
	}

}
