package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DemoRollUpYearlyJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpYearlyJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {

		ZonedDateTime currentZdt = DateTimeUtil.getDateTime();
		try {
			LOGGER.info("DemorollupYearlyJob started in facilioJob");
			FacilioChain context = TransactionChainFactory.demoRollUpYearlyChain();
			context.getContext().put(ContextNames.START_TIME, currentZdt);
			context.getContext().put(ContextNames.DEMO_ROLLUP_JOB_ORG, jc.getOrgId());
			context.execute();
		} catch (Exception e) {
			CommonCommandUtil.emailException("DemoRollUpYearly", "DemoRollUpYearly Failed - orgid -- " + jc.getOrgId(), e);
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
		}
	}
	
}
