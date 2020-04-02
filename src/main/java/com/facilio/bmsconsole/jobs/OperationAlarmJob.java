package com.facilio.bmsconsole.jobs;


import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class OperationAlarmJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(OperationAlarmJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
    	try {
			LOGGER.info("Operation Alarm Job ---> started" + jc.getJobId());
			FacilioContext context = new FacilioContext();
			long hrsToCheckinMillis= 3600000;
			long endTime = System.currentTimeMillis();
			long startTime = System.currentTimeMillis() - hrsToCheckinMillis ;
			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
    		FacilioChain chain = TransactionChainFactory.getExecuteOperationAlarm();
    		chain.execute(context);
        }
    	catch(Exception operationAlarmJobException) {
    		try {
    			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
    			LOGGER.error("Error occurred" +operationAlarmJobException.toString());
    			throw operationAlarmJobException;			
    		}
    		catch(Exception transactionException) {
    			LOGGER.error(transactionException.toString());
    		}
    	}
    }
}
