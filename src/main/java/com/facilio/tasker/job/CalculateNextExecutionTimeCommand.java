package com.facilio.tasker.job;

import java.time.Instant;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;

public class CalculateNextExecutionTimeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		JobContext jc = (JobContext) context.get(JobConstants.JOB_CONTEXT);
		long nextExecutionTime = getNextExecutionTime(jc);
		jc.setNextExecutionTime(nextExecutionTime);
		return false;
	}
	
	private long getNextExecutionTime(JobContext jc) {
		if(jc.isPeriodic() && (jc.getMaxExecution() == -1 || jc.getCurrentExecutionCount()+1 < jc.getMaxExecution())) {
			long nextExecutionTime = -1; 
			if(jc.getPeriod() != -1) {
				nextExecutionTime = (Instant.now().getEpochSecond())+jc.getPeriod();
			}
			else if(jc.getSchedule() != null) {
				nextExecutionTime = jc.getSchedule().nextExecutionTime(jc.getExecutionTime());
				if(nextExecutionTime == jc.getExecutionTime()) {// One time job
					return -1;
		 		}
				while(nextExecutionTime <= Instant.now().getEpochSecond()) {
					nextExecutionTime = jc.getSchedule().nextExecutionTime(nextExecutionTime);
				}
			}
			if(jc.getEndExecutionTime() == -1 || nextExecutionTime <= jc.getEndExecutionTime()) {
				return nextExecutionTime;
			}
		}
		return -1;
	}

}
