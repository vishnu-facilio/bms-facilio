package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledReadingRuleJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		
		Long readingRuleId = jc.getJobId();
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, readingRuleId);
		
		Chain scheduledChain = TransactionChainFactory.executeScheduledReadingRuleChain();
		
		scheduledChain.execute(context);
		
	}

}
