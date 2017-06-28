package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.constants.FacilioConstants;

public class ValidateSchedulePropsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ScheduleContext scheduleContext = (ScheduleContext) context.get(FacilioConstants.ContextNames.SCHEDULE_OBJECT);
		
		if(scheduleContext != null) {
			if(scheduleContext.getOrgId() <= 0) {
				throw new IllegalArgumentException("Invalid Org id");
			}
			
			if(scheduleContext.getScheduledStart() <= 0) {
				throw new IllegalArgumentException("Invalid start time");
			}
			
			if(scheduleContext.getEstimatedEnd() <= 0) {
				throw new IllegalArgumentException("Invalid end time");
			}
		}
		
		return false;
	}
}
