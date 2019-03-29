package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;

public class ScheduledAlarmTriggerJob extends FacilioJob {
	
	@Override
	public void execute(JobContext jc) throws Exception {
		
		Long readingRuleId = jc.getJobId();
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, readingRuleId);
		
		context.put(FacilioConstants.ContextNames.IS_READING_RULE_EXECUTE_FROM_JOB,true);
		
		Chain scheduledChain = TransactionChainFactory.executeScheduledAlarmTriggerChain();
		
		scheduledChain.execute(context);
		
	}

}
