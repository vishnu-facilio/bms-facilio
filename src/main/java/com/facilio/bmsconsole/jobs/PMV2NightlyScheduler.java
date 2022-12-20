package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import lombok.extern.log4j.Log4j;

@Log4j
public class PMV2NightlyScheduler extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			
			FacilioChain chain = TransactionChainFactoryV3.PMV2NightlyJobChain(); 
			chain.execute();
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
