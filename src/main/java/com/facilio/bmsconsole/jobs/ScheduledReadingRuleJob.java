package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledReadingRuleJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(ScheduledReadingRuleJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		
		try {
			Long readingRuleId = jc.getJobId();
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, readingRuleId);
			context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, DateTimeUtil.getHourStartTimeOf(jc.getExecutionTime() * 1000)); //TODO hourStartTime should be changed to direct execution time later
			
			Chain scheduledChain = TransactionChainFactory.executeScheduledReadingRuleChain();
			
			scheduledChain.execute(context);
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			CommonCommandUtil.emailException("ScheduledReadingRuleJob", "rule Calculation job failed for orgid : "+jc.getOrgId() +" id -- "+jc.getJobId(), e);
		}
	}

}
