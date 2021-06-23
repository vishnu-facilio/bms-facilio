package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteBreakCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long breakId = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (breakId != null) {
			ShiftAPI.deleteBreak(breakId);
		}
		return false;
	}

}
