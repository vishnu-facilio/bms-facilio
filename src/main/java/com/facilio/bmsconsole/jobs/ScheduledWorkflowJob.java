package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2API;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.List;

public class ScheduledWorkflowJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(ScheduledWorkflowJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		try {
			long id = jc.getJobId();
			
			LOGGER.error("ScheduledWorkflowJob for ID - "+ id +" Started at "+System.currentTimeMillis());
			
			ScheduledWorkflowContext scheduledWorkflowContext = WorkflowV2API.getScheduledWorkflowContext(id,true);
			
			if(scheduledWorkflowContext != null) {
				
				List<ActionContext> actions = scheduledWorkflowContext.getActions();
				if (CollectionUtils.isNotEmpty(actions)) {
					for (ActionContext action : actions) {
						FacilioContext context = new FacilioContext();
						action.executeAction(null, context, null, null);
					}
				}

//				WorkflowContext workflow = WorkflowUtil.getWorkflowContext(scheduledWorkflowContext.getWorkflowId());
//
//				FacilioContext context = new FacilioContext();
//				context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
//
//				FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
//
//				chain.execute(context);
			}
			
			LOGGER.error("ScheduledWorkflowJob for ID - "+ id +" Completed at "+System.currentTimeMillis());
		}
		catch (Exception e) {
			CommonCommandUtil.emailException("Scheduled Workflow Failed", "Orgid -- "+AccountUtil.getCurrentOrg().getId() + " jobid -- "+jc.getJobId(), e);
			LOGGER.log(Priority.ERROR, e.getMessage(), e);
		}
	}
}
