package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.time.DateTimeUtil;

public class PlanControlGroupSlotsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		boolean isPlanByJob = false;
		
		if(isPlanByJob) {
			JSONObject obj = new JSONObject();
			
			obj.put(ControlScheduleUtil.CONTROL_GROUP_ID, controlGroupContext.getId());
			
			BmsJobUtil.deleteJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob");
			
			BmsJobUtil.scheduleOneTimeJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob", 5, "facilio", obj);
		}

		else {
			
			controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId());
			long startTime = DateTimeUtil.getDayStartTime();
			
			long endTime = DateTimeUtil.addMonths(startTime, 1);
			
//			long endTime = DateTimeUtil.addDays(startTime, 7);
			
			FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
			
			FacilioContext newContext = chain.getContext();
			
			newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
			newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
			newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
			
			chain.execute();
		}
		return false;
	}

}
