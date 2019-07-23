package com.facilio.tasker.job;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;

public class JobExecutionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioJob job = (FacilioJob) context.get(JobConstants.FACILIO_JOB);
		JobContext jc = (JobContext) context.get(JobConstants.JOB_CONTEXT);
		job.execute(jc);
		return false;
	}

}
