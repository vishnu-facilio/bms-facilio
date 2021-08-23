package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.common.JobConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HistoricalRuleEventRunJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRuleEventRunJob.class.getName());
	
	long jobId = -1;
	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			jobId = jc.getJobId();
			FacilioChain chain = TransactionChainFactory.getExecuteHistoricalEventRuleCalculation();
			chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_EVENT_RULE_JOB_ID, jc.getJobId());
			chain.getContext().put(JobConstants.LOGGER_LEVEL, jc.getLoggerLevel());
			chain.execute();
		}
		catch(Exception historicalRuleException) {
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
			LOGGER.error("Error occurred in HistoricalRuleEventRunJob: " +historicalRuleException.toString());					
		}
	}
	
	@Override
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during HistoricalRuleEventRunJob JobId --" + jobId);
	 	super.handleTimeOut();
	}	

}

