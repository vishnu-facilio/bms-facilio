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

public class DemoAlarmPropagationJob extends FacilioJob{
	
	private static final Logger LOGGER = LogManager.getLogger(DemoAlarmPropagationJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {

		ZonedDateTime currentZdt = DateTimeUtil.getDateTime();
		try {
			LOGGER.info("DemoAlarmPropagationJob Started");
			FacilioChain context = TransactionChainFactory.demoAlarmPropagationChain();
			context.getContext().put(ContextNames.START_TIME, currentZdt);
			context.getContext().put(ContextNames.DEMO_ROLLUP_JOB_ORG, jc.getOrgId());
			context.execute();
			LOGGER.info("DemoAlarmPropagationJob Started Daily Demo Historical rule evaluation");
		} catch (Exception e) {
        	LOGGER.error("DemoAlarmPropagationJob Error -- "  +e+ " OrgId -- "+AccountUtil.getCurrentOrg().getId());
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
			CommonCommandUtil.emailException("DemoAlarmPropagationJob", "DemoAlarmPropagationJob Failed - Orgid -- " + jc.getOrgId(), e);
		}
	}

}
