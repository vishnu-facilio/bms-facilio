package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class AddShiftUserMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long orgUserId = (long) context.get(FacilioConstants.ContextNames.ORG_USER_ID);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		long shiftId = (long) context.get(FacilioConstants.ContextNames.SHIFT_ID);
		
		ShiftContext shift = ShiftAPI.getShift(shiftId);
		if (shift == null) {
			throw new IllegalArgumentException("Invalid shift");
		}
		
		ShiftAPI.addShiftUserMapping(shift.getId(), orgUserId, startTime, endTime);
		
		return false;
	}

}
