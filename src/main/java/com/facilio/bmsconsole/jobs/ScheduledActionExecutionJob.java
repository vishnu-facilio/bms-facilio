package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledActionExecutionJob extends FacilioJob {
private static final Logger LOGGER = LogManager.getLogger(ScheduledActionExecutionJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			ScheduledActionAPI.executeScheduledAction(jc.getJobId());
		}
		catch (Exception e) {
			LOGGER.fatal("Error occurred during scheduled action execution for job : "+jc.getJobId(), e);
			CommonCommandUtil.emailException("ScheduledActionExecutionJob", "Error occurred during scheduled action execution for job : "+jc.getJobId(), e);
		}
	}
}
