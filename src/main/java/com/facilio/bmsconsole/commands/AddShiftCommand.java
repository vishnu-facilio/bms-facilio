package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;


public class AddShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
//		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
//		shift.setBusinessHoursId(id);
		ShiftAPI.addShift(shift);
		
//		props.get("id");
//		List<BusinessHourContext> days = shift.getDays();
		
//		ShiftAPI.scheduleJobs(days);
		
		return false;
	}

}
