package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateBreakCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BreakContext breakContext = (BreakContext) context.get(FacilioConstants.ContextNames.BREAK);
		if (breakContext != null) {
			ShiftAPI.addOrUpdateBreak(breakContext);
		}
		return false;
	}

}
