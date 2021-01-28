package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleGroupedSlot;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;

public class PlanControlGroupCommands extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		List<ControlScheduleGroupedSlot> plannedSlots = (List<ControlScheduleGroupedSlot>) context.get(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS);
		
		List<ControlScheduleSlot> unplannedSlots = (List<ControlScheduleSlot>) context.get(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS);
		
		List<ControlActionCommandContext> commands1 = ControlScheduleUtil.planCommandsForSchedules(plannedSlots, controlGroup);
		
		List<ControlScheduleSlot> routineSlots = new ArrayList<ControlScheduleSlot>();
		
		for(ControlScheduleSlot slot :unplannedSlots) {
			if(slot.getRoutine() != null) {
				routineSlots.add(slot);
			}
		}
		
		List<ControlActionCommandContext> commands2 = ControlScheduleUtil.planCommandsForRoutines(routineSlots);
		
		
		return false;
	}


}
