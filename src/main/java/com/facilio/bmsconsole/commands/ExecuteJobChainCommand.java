package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class ExecuteJobChainCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long jobId = (Long)context.get(FacilioConstants.ContextNames.JOB);
		String jobName = (String)context.get(FacilioConstants.ContextNames.JOB_NAME);
		JobContext jc = JobStore.getJob(jobId, jobName);
		if(jobName.equals("ScheduledRuleExecution")) {
			WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(jc.getJobId(), true);
			
			if (!rule.isActive()) {
				return false;
			}
			
			FacilioContext context2 = new FacilioContext();
			context2.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
			context2.put(FacilioConstants.Job.JOB_CONTEXT, jc);
			Chain executeRule = TransactionChainFactory.scheduledRuleExecutionChain();
			executeRule.execute(context2);
		}
		
		return false;
	}

	
}
