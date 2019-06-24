package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ScheduledWorkflowJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(ScheduledWorkflowJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		try {
			long id = jc.getJobId();
			
			ScheduledWorkflowContext scheduledWorkflowContext = WorkflowV2API.getScheduledWorkflowContext(id,true);
			
			WorkflowContext workflow = WorkflowUtil.getWorkflowContext(scheduledWorkflowContext.getWorkflowId());
			
			FacilioContext context = new FacilioContext();
			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			
			Chain chain = TransactionChainFactory.getExecuteWorkflowChain();
			
			chain.execute(context);
		}
		catch (Exception e) {
			CommonCommandUtil.emailException("Scheduled Workflow Failed", "Orgid -- "+AccountUtil.getCurrentOrg().getId() + " jobid -- "+jc.getJobId(), e);
			LOGGER.log(Priority.ERROR, e.getMessage(), e);
		}
	}
}
