package com.facilio.tasker.job;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.config.InstantJobConf;

public class InstantJobExecutionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		InstantJob job = (InstantJob) context.remove(JobConstants.INSTANT_JOB);
		job.execute((FacilioContext) context);
        ObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), job.getReceiptHandle());
		return false;
	}

}
