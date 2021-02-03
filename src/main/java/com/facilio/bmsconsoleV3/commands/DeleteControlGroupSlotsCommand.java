package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.time.DateTimeUtil;

public class DeleteControlGroupSlotsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long startTime = DateTimeUtil.getDayStartTime();
		
		long endTime = DateTimeUtil.addMonths(startTime, 1);
		
		String currentModule = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, currentModule);
		
		ControlScheduleUtil.deleteSlotsAndGroupedSlots(controlGroupContext,startTime,endTime);
		return false;
	}

}
