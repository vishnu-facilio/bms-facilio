package com.facilio.tasker.job;

import com.facilio.chain.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.config.InstantJobConf;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class InstantJobExecutionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		InstantJob job = (InstantJob) context.remove(JobConstants.INSTANT_JOB);
		job.execute((FacilioContext) context);
		return false;
	}

}
