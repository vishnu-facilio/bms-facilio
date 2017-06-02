package com.facilio.tasker;

import com.facilio.fw.OrgInfo;
import com.facilio.tasker.job.JobStore;

public class FacilioTimer {
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static long schedulePeriodicJob(String jobName, long delay, int period, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return JobStore.addJob(getCurrentOrgId(), jobName, true, period, nextExecutionTime, executorName);
	}
	
	public static long scheduleOneTimeJob(String jobName, int delay, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return JobStore.addJob(getCurrentOrgId(), jobName, false, delay, nextExecutionTime, executorName);
	}
	
	private static long getCurrentOrgId() {
		long orgId = -1;
		if(OrgInfo.getCurrentOrgInfo() != null) {
			orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		}
		
		return orgId;
	}
	
}
