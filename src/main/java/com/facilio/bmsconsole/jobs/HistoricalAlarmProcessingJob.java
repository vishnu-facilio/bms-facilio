package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.common.JobConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class HistoricalAlarmProcessingJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmProcessingJob.class.getName());
	
	long jobId = -1;
	public void execute(JobContext jc) throws Exception {
		jobId = jc.getJobId();
		FacilioChain chain = TransactionChainFactory.getExecuteHistoricalRuleAlarmProcessing();
		chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_ID, jc.getJobId());
		chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_RETRY_COUNT, jc.getJobExecutionCount());
		chain.getContext().put(JobConstants.LOGGER_LEVEL, jc.getLoggerLevel());
		chain.execute();
	}

	@Override
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during HistoricalRuleAlarmProcessing JobId --"+jobId);
	 	super.handleTimeOut();
	}	
			
}