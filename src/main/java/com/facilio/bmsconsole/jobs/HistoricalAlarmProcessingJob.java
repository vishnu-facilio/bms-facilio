package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;


public class HistoricalAlarmProcessingJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmProcessingJob.class.getName());
	
	public void execute(JobContext jc) throws Exception {
		
		try {
			FacilioChain chain = TransactionChainFactory.getExecuteHistoricalRuleAlarmProcessing();
			chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_ID, jc.getJobId());
			chain.execute();
		}
		catch(Exception historicalRuleAlarmProcessingException) {
			try {
				LOGGER.error("Error occurred while doing Historical Rule Alarm Processing Job" +historicalRuleAlarmProcessingException.toString());
				throw historicalRuleAlarmProcessingException;
			}
			catch(Exception transactionException) {
				LOGGER.error(transactionException.toString());
			}
		}
	}

	@Override
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during HistoricalRuleAlarmProcessing Job");
	 	super.handleTimeOut();
	}	
			
}