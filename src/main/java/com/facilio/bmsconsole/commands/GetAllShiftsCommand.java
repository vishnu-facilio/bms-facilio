package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllShiftsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ShiftContext> shifts = ShiftAPI.getAllShifts();
		context.put(FacilioConstants.ContextNames.SHIFTS, shifts);
		return false;
	}

}
