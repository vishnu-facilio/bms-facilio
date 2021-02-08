package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class ControlCommandExecutionCreateScheduleJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(ControlCommandExecutionCreateScheduleJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			LOGGER.info("ControlCommandExecutionCreateScheduleJob started -- "+jc.getJobId());
			long startTime = jc.getExecutionTime();
			long endTime = DateTimeUtil.addMinutes(startTime*1000, 30);
			
			 FacilioChain chain = TransactionChainFactoryV3.getControlCommandExecutionCreateScheduleChain();
			 FacilioContext context = chain.getContext();
			 
			 context.put(FacilioConstants.ContextNames.START_TIME, startTime*1000);
			 context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			 
			 chain.execute();
			 LOGGER.info("ControlCommandExecutionCreateScheduleJob completed -- "+jc.getJobId());
		}
		catch(Exception e) {
			LOGGER.error("ControlCommandExecutionCreateScheduleJob Failed", e);
		}
	}

}
