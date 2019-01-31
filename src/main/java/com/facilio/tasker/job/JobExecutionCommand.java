package com.facilio.tasker.job;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class JobExecutionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioJob job = (FacilioJob) context.get(JobConstants.FACILIO_JOB);
		JobContext jc = (JobContext) context.get(JobConstants.JOB_CONTEXT);
		job.execute(jc);
		return false;
	}

}
