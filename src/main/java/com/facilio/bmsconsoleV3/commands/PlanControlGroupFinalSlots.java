package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleContext.Mode;
import com.facilio.control.ControlScheduleGroupedSlot;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;

public class PlanControlGroupFinalSlots extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlGroupContext controlGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		List<ControlScheduleSlot> slots = (List<ControlScheduleSlot>) context.get(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS);
		
		Collections.sort(slots);
		
		List<ControlScheduleGroupedSlot> plannedSlots = null;
		if(controlGroup.getControlSchedule().getModeEnum() == Mode.IGNORE_NORMAL_SCHEDULE) {
			
			plannedSlots = ControlScheduleUtil.mergeSlotsForIgnoreNormalScheduleMode(slots);
		}
		else {
			plannedSlots = ControlScheduleUtil.planGroupedSlots(slots);
		}
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS,plannedSlots);
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_SCHEDULE_PLANNED_SLOTS_MODULE_NAME, plannedSlots);
		
		return false;
	}

}
