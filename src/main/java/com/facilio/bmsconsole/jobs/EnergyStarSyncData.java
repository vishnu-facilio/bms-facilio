package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EnergyStarSyncData extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(EnergyStarSyncData.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		
		HistoricalLoggerContext logger = null;
		
		try {
			
			logger = HistoricalLoggerUtil.getHistoricalLoggerById(jc.getJobId());
			
			FacilioChain chain = TransactionChainFactory.doEnergyStarSyncChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER, logger);
			
			chain.execute();
			
			logger.setStatus(HistoricalLoggerContext.Status.RESOLVED.getIntVal());
			
			HistoricalLoggerUtil.updateHistoricalLogger(logger);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("Energy Star Sync data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star Sync data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
			
			logger.setStatus(HistoricalLoggerContext.Status.FAILED.getIntVal());
			
			HistoricalLoggerUtil.updateHistoricalLogger(logger);
		}
	}

}