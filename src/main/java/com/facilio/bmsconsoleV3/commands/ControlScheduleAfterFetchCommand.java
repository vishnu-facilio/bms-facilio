package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.util.ControlScheduleUtil;

public class ControlScheduleAfterFetchCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<ControlScheduleContext> schedules = (List<ControlScheduleContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(context.get(FacilioConstants.ContextNames.MODULE_NAME)));
		
		for(ControlScheduleContext schedule : schedules) {
			schedule.setBusinessHoursContext(BusinessHoursAPI.getBusinessHours(Collections.singletonList(schedule.getBusinessHour())).get(0));
		}
		return false;
	}

}
