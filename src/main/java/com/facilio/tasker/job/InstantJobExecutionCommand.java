package com.facilio.tasker.job;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;

public class InstantJobExecutionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		InstantJob job = (InstantJob) context.remove(JobConstants.INSTANT_JOB);
		job.execute((FacilioContext) context);
		return false;
	}

}
