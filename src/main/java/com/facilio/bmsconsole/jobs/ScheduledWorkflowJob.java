package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2API;

public class ScheduledWorkflowJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(ScheduledWorkflowJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		
		long id = jc.getJobId();
		
		ScheduledWorkflowContext scheduledWorkflowContext = WorkflowV2API.getScheduledWorkflowContext(id);
	}

	
}
