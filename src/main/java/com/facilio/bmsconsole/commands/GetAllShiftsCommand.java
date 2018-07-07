package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllShiftsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ShiftContext> shifts = ShiftAPI.getAllShifts();
		context.put(FacilioConstants.ContextNames.SHIFTS, shifts);
		return false;
	}

}
