package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteBreakCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Long breakId = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (breakId != null) {
			ShiftAPI.deleteBreak(breakId);
		}
		return false;
	}

}
