package com.facilio.bmsconsoleV3.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.util.ControlScheduleUtil;

public class UpdateControlScheduleBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlScheduleContext schedule = (ControlScheduleContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
		
		if(schedule.getId() > 0) {
			
			FacilioChain updateBusinessHoursChain = TransactionChainFactory.updateBusinessHoursChain();
			FacilioContext newContext = updateBusinessHoursChain.getContext();
			newContext.put(FacilioConstants.ContextNames.BUSINESS_HOUR, schedule.getBusinessHoursContext());
			updateBusinessHoursChain.execute();
			
			schedule.setBusinessHour(schedule.getBusinessHoursContext().getId());
			
		}
		return false;
	}

}
