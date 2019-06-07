package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllBreaksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<BreakContext> breakList = ShiftAPI.getBreakList();
		context.put(FacilioConstants.ContextNames.BREAK_LIST, breakList);
		return false;
	}

}
