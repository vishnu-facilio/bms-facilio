package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;

public class AddTaskScheduleCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TaskContext taskContext = (TaskContext) context;
		ScheduleContext sc = taskContext.getSchedule();
		
		if(sc != null) {
			Chain addScheduleChain = FacilioChainFactory.getAddScheduleObjectChain();
			addScheduleChain.execute(sc);
			
			taskContext.setScheduleId(sc.getScheduleId());
		}
		
		return false;
	}
}
