package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.util.DBConf;
import com.facilio.time.DateTimeUtil;

public class PlanControlGroupSlots extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(PlanControlGroupSlots.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		long startTime = (long) context.getOrDefault(FacilioConstants.ContextNames.START_TIME, DateTimeUtil.getCurrenTime());
		
		
		LOGGER.error("DateTimeUtil.getZonedDateTime(startTime) --> "+DateTimeUtil.getZonedDateTime(startTime));
		
		LOGGER.error("getCurrentZoneId --> "+DBConf.getInstance().getCurrentZoneId());
		LOGGER.error("getCurrentLocale --> "+DBConf.getInstance().getCurrentLocale());
		
		
		long endTime = (long) context.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addMonths(startTime, 1));
		
		ControlScheduleUtil.deleteSlotsAndGroupedSlots(controlGroup,startTime,endTime);
		
		List<ControlScheduleSlot> slots = ControlScheduleUtil.planScheduleSlots(controlGroup, startTime, endTime);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS,slots);
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, slots);
		return false;
	}


}
