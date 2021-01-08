package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;

import con.facilio.control.ControlScheduleContext;

public class AddControlScheduleBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleContext schedule = (ControlScheduleContext) ControlScheduleUtil.getObjectFormRecordMap(context, ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
		
        if(schedule.getBusinessHour() == null) {
        	
        	FacilioChain addBusinessHourChain = TransactionChainFactory.addBusinessHourChain();
        	
        	FacilioContext newContext = addBusinessHourChain.getContext();
        	newContext.put(FacilioConstants.ContextNames.BUSINESS_HOUR, schedule.getBusinessHoursContext());
        	newContext.put(FacilioConstants.ContextNames.RESOURCE_ID, -1l);
    		
    		addBusinessHourChain.execute();
    		
    		long businessHourId = (long) newContext.get(FacilioConstants.ContextNames.ID);
    		
    		schedule.setBusinessHour(businessHourId);
        }
        
		return false;
	}

}
