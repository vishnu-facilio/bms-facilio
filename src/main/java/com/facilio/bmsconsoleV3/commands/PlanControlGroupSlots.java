package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.time.DateTimeUtil;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlScheduleSlot;

public class PlanControlGroupSlots extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControlGroupContext controlGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		long startTime = (long) context.getOrDefault(FacilioConstants.ContextNames.START_TIME, DateTimeUtil.getCurrenTime());
		
		long endTime = (long) context.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addMonths(startTime, 1));
		
		List<ControlScheduleSlot> slots = ControlScheduleUtil.planScheduleSlots(controlGroup, startTime, endTime);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS,slots);
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, slots);
		return false;
	}

}
