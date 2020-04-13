package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HistoricalOperationalAlarmProcessingJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmProcessingJob.class.getName());

    long jobId = -1;
    public void execute(JobContext jc) throws Exception {
        jobId = jc.getJobId();
        FacilioChain chain = TransactionChainFactory.getExecuteHistoricalOperationalAlarmProcessing();
        chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_OPERATIONAL_ALARM_PROCESSING_JOB_ID, jc.getJobId());
        chain.execute();
    }

    @Override
    public void handleTimeOut() {
        // TODO Auto-generated method stub
        LOGGER.info("Time out called during HistoricalRuleAlarmProcessing JobId --"+jobId);
        super.handleTimeOut();
    }
}
