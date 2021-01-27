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
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		FacilioContext contextNew = new FacilioContext();
		
		controlGroupContext.setControlSchedule(ControlScheduleUtil.getControlSchedule(controlGroupContext.getControlSchedule().getId()));
		contextNew.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, controlGroupContext);
		FacilioTimer.scheduleInstantJob("ControlScheduleSlotCreationJob",contextNew);
		return false;
	}

}
