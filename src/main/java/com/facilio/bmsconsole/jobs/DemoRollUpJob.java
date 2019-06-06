/**
 * 
 */
package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author facilio
 *
 */
public class DemoRollUpJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		// TODO Auto-generated method stub

		long orgId=AccountUtil.getCurrentOrg().getId();
		long timeDuration = 1;
		try {
			FacilioContext context=new FacilioContext();
			context.put(ContextNames.DEMO_ROLLUP_EXECUTION_TIME,timeDuration);
			context.put(ContextNames.DEMO_ROLLUP_JOB_ORG,orgId);
			Chain demoRollupChain = TransactionChainFactory.demoRollUpChain();
				demoRollupChain.execute(context);
		}		
		catch(Exception e) {
			LOGGER.info("Exception occurred### in  DemoRollUpJob  ", e);
			CommonCommandUtil.emailException("DemoRoleUp", "DemoRoleUp Failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	
	}

	
}
