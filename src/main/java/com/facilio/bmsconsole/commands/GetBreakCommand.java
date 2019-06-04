package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetBreakCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Long breakId = (Long) context.get(FacilioConstants.ContextNames.ID);
		
		if (breakId != null) {
			BreakContext breakContext = ShiftAPI.getBreak(breakId);
			context.put(FacilioConstants.ContextNames.BREAK, breakContext);
		}
		return false;
	}

}
