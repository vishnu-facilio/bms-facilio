package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.tasker.FacilioTimer;

import con.facilio.control.ControlGroupContext;

public class PlanControlGroupSlotsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioTimer.scheduleInstantJob("ControlScheduleSlotCreationJob",(FacilioContext) context);
		return false;
	}

}
