package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllShiftsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ShiftContext> shifts = ShiftAPI.getAllShifts();
		context.put(FacilioConstants.ContextNames.SHIFTS, shifts);
		return false;
	}

}
