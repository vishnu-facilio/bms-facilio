package com.facilio.bmsconsole.instant.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.InstantJob;

public class MigrateReadingDataJob extends InstantJob {
	
	private static final Logger LOGGER = LogManager.getLogger(MigrateReadingDataJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		try {
			
			FacilioChain chain = TransactionChainFactory.getMigrateReadingDataChain();
			chain.execute(context);
			
		} catch (Exception e) {
			LOGGER.error("Error occurred during execution of MigrateReadingDataJob", e);
			CommonCommandUtil.emailException("MigrateReadingDataJob", "Error occurred during execution of MigrateReadingDataJob", e);
		}

	}
	
	@Override
	public void handleTimeOut() {
		LOGGER.info("MigrateReadingDataJob timed out");
		super.handleTimeOut();
	}
	

}
