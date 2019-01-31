package com.facilio.tasker.job;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateNextExecutionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JobContext jc = (JobContext) context.get(JobConstants.JOB_CONTEXT);
		if(jc.getNextExecutionTime() != -1) {
			JobStore.updateNextExecutionTimeAndCount(jc.getJobId(), jc.getJobName(),  jc.getNextExecutionTime(), jc.getCurrentExecutionCount()+1);
		} else {
			JobStore.setInActiveAndUpdateCount(jc.getJobId(), jc.getJobName(), jc.getCurrentExecutionCount()+1);
		}
		return false;
	}

}
