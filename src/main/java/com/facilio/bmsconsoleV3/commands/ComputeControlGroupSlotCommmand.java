package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;

public class ComputeControlGroupSlotCommmand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<ControlScheduleSlot> slots = (List<ControlScheduleSlot>) context.get(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS);
		
		Collections.sort(slots);
		
		slots = ControlScheduleUtil.slotsForDisplay(slots);
		
		Collections.sort(slots);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS, slots);
		return false;
	}

}
