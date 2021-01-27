package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

import con.facilio.control.ControlGroupContext;

public class ControlScheduleSlotCreationJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(ControlScheduleSlotCreationJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		
		try {
			JSONObject props= BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			
			long controlGroupId = (long) props.get(ControlScheduleUtil.CONTROL_GROUP_ID);
			
			ControlGroupContext controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupId);
			
			long startTime = (long) props.getOrDefault(FacilioConstants.ContextNames.START_TIME, DateTimeUtil.getDayStartTime());
			
			long endTime = (long) props.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addMonths(startTime, 1));
			
//			long endTime = (long) props.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addDays(startTime, 7));
			
			FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
			
			FacilioContext newContext = chain.getContext();
			
			newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
			newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
			newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
			
			chain.execute();
		}
		catch(Exception e) {
			LOGGER.error("ControlScheduleSlotCreationJob Failed", e);
		}
	}

	
}
