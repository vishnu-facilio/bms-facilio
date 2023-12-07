package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class ScheduledRuleExecutionJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(ScheduledRuleExecutionJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(jc.getJobId());
			
			if (rule == null || !rule.isActive()) {
				return;
			}

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
			context.put(FacilioConstants.Job.JOB_CONTEXT, jc);
			FacilioChain executeRule = TransactionChainFactory.scheduledRuleExecutionChain();
			executeRule.execute(context);
			
		}
		catch (Exception e) {
			LOGGER.fatal("Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
			CommonCommandUtil.emailException("ScheduledRuleExecutionJob", "Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
		}
	}
}
