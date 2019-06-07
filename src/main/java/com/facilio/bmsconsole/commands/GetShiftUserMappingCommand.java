package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetShiftUserMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		if (startTime == -1 || endTime == -1) {
			throw new IllegalArgumentException("Both start and end time is mandatory");
		}
		
		List<ShiftUserRelContext> shiftUserMapping = ShiftAPI.getShiftUserMapping(startTime, endTime);
		context.put(FacilioConstants.ContextNames.SHIFT_USER_MAPPING, shiftUserMapping);
		return false;
	}

}
