package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DemoSingleRollUpYearlyJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(DemoSingleRollUpYearlyJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {		
		try{
        	LOGGER.info("DemoSingleRollUpYearlyCommand start orgid -- "+AccountUtil.getCurrentOrg().getId());
        	FacilioChain chain = TransactionChainFactory.demoSingleRollUpYearlyChain();
        	chain.execute();
        	LOGGER.info("DemoSingleRollUpYearlyCommand end orgid -- "+AccountUtil.getCurrentOrg().getId());
        }
        catch(Exception e){
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
        	LOGGER.error("DemoSingleRollUpYearlyCommand Error Mig"  +e+ "orgid -- "+AccountUtil.getCurrentOrg().getId());
        }			
	}

}
