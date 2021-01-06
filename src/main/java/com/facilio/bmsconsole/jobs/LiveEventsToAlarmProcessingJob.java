package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class LiveEventsToAlarmProcessingJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(LiveEventsToAlarmProcessingJob.class.getName());
	
	public void execute(JobContext jc) throws Exception {
		FacilioChain chain = TransactionChainFactory.getExecuteLiveEventsToAlarmProcessingJob();
		chain.execute();		
	}

	@Override
	public void handleTimeOut() {
		LOGGER.info("Time out called during getExecuteLiveEventsToAlarmProcessingJob");
	 	super.handleTimeOut();
	}	
			
}
