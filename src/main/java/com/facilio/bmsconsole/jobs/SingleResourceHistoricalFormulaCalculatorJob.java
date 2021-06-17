package com.facilio.bmsconsole.jobs;

import com.facilio.services.factory.FacilioFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;


import com.facilio.bmsconsole.commands.TransactionChainFactory;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.BeanFactory;

import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class SingleResourceHistoricalFormulaCalculatorJob extends FacilioJob {
	
	private static final Logger LOGGER = Logger.getLogger(SingleResourceHistoricalFormulaCalculatorJob.class.getName());

		@Override
		public void execute(JobContext jc) throws Exception {
			try {
				FacilioChain chain = TransactionChainFactory.getExecuteHistoricalFormulaFieldCalculation();
				chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_FORMULA_FIELD_JOB_ID, jc.getJobId());
				chain.execute();
			}
			catch(Exception FormulaFieldException) {
				try {
					FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
					LOGGER.error("Error occurred while doing Historical Formula Field calculation Job" +FormulaFieldException.toString());
				}
				catch(Exception transactionException) {
					LOGGER.error(transactionException.toString());
				}
			}
		}
		
		@Override
		public void handleTimeOut() {
			// TODO Auto-generated method stub
			LOGGER.info("Time out called during HistoricalFormulaFieldCalculation");
		 	super.handleTimeOut();
		}

	}
	
	

		
	
	

