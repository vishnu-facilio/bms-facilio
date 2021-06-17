package com.facilio.bmsconsole.jobs;

import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class ImportReadingJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ImportReadingJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			FacilioChain chain = TransactionChainFactory.getImportReadingJobChain();
			chain.getContext().put(ImportAPI.ImportProcessConstants.JOB_ID, jc.getJobId());
			chain.execute();
		} catch (Exception e) {
			try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
				LOGGER.severe("Error Occured in ImportReadingJob -- " + e.toString());
			} catch (Exception transactionException) {
				LOGGER.severe(transactionException.toString());
			}
		}

	}
}
