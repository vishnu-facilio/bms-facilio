package com.facilio.tasker;

import com.facilio.tasker.job.JobStore;

public class FacilioTimer {
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static long schedulePeriodicJob(String jobName, long delay, int period) throws Exception
	{
		String orgId = "test1";
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return JobStore.addJob(orgId, jobName, true, period, nextExecutionTime);
	}
	
	public static long scheduleOneTimeJob(String jobName, int delay) throws Exception {
		String orgId = "test1";
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return JobStore.addJob(orgId, jobName, false, delay, nextExecutionTime);
	}
	
}
