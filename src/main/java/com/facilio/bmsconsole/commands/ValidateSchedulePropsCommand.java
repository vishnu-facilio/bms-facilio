package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduleContext;

public class ValidateSchedulePropsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ScheduleContext scheduleContext = (ScheduleContext) context;
		
		if(scheduleContext.getOrgId() == -1) {
			throw new IllegalArgumentException("Invalid Org id");
		}
		
		if(scheduleContext.getScheduledStart() == -1) {
			throw new IllegalArgumentException("Invalid start time");
		}
		
		if(scheduleContext.getEstimatedEnd() == -1) {
			throw new IllegalArgumentException("Invalid end time");
		}
		
		return false;
	}
}
