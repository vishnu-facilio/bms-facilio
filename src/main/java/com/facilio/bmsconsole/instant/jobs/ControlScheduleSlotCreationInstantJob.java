package com.facilio.bmsconsole.instant.jobs;

import java.util.List;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.tasker.job.InstantJob;
import com.facilio.time.DateTimeUtil;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlScheduleSlot;

public class ControlScheduleSlotCreationInstantJob extends InstantJob {

	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		long startTime = (long) context.getOrDefault(FacilioConstants.ContextNames.START_TIME, DateTimeUtil.getCurrenTime());
		
		long endTime = (long) context.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addMonths(startTime, 1));
		
		//long endTime = (long) context.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addDays(startTime, 7));
		
		FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
		
		FacilioContext newContext = chain.getContext();
		
		newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
		newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
		newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
		
		chain.execute();
	}

	
}
