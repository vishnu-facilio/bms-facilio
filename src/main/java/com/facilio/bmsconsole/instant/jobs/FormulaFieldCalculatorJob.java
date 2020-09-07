package com.facilio.bmsconsole.instant.jobs;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.JobContext;

public class FormulaFieldCalculatorJob extends InstantJob {
	
	private static final Logger LOGGER = Logger.getLogger(FormulaFieldCalculatorJob.class.getName());
	public void execute(FacilioContext context) throws Exception {
		try {
			FacilioChain chain = TransactionChainFactory.getExecuteFormulaFieldJobCalculationCommand();
			chain.getContext().put(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID, (long) context.get(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID));
			chain.getContext().put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, (List<Integer>) context.get(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES));
			chain.execute();
		}
		catch(Exception FormulaResourceException) {
			try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
				LOGGER.error("Error occurred while doing Formula Resource Calculation Job" +FormulaResourceException.toString());
			}
			catch(Exception transactionException) {
				LOGGER.error(transactionException.toString());
			}
		}
	}
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during Formula Resource Calculation");
	 	super.handleTimeOut();
	}
}
