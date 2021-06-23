package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;


public class AddShiftCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
		shift.setDefaultShift(false);
		ShiftAPI.addShift(shift);
		
		
//		ShiftAPI.scheduleJobs(days);
		
		return false;
	}

}
