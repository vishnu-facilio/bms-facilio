package com.facilio.bmsconsole.jobs;

import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;



public class HistoricalVMEnergyDataCalculatorJob extends FacilioJob {
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalVMEnergyDataCalculatorJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			FacilioChain chain = TransactionChainFactory.getExecuteHistoricalVMCalculation();
			chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_VM_JOB_ID, jc.getJobId());
			chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_VM_JOB, jc.getJobName());
			chain.execute();
		}
		catch(Exception VMException) {
			try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
				LOGGER.severe("Error occurred while doing historical calculation Job" +VMException.toString());
			}
			catch(Exception transactionException) {
				LOGGER.severe(transactionException.toString());
			}
		}
	}
	
	@Override
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during HistoricalVMValculation");
	 	super.handleTimeOut();
	}

}


	



