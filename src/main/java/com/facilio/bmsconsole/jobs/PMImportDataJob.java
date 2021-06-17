package com.facilio.bmsconsole.jobs;

import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class PMImportDataJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(PMImportDataJob.class.getName());
	
	@Override
	public void execute(JobContext jc){
		try{
			FacilioChain chain = TransactionChainFactory.getPMImportDataChain();
			chain.getContext().put(ImportAPI.ImportProcessConstants.JOB_ID, jc.getJobId());
			chain.execute();
		}catch(Exception e){
			LOGGER.severe("Error Occured in PMImportDataJob Job -- " + e.toString());
			e.printStackTrace();
		}
	}
}