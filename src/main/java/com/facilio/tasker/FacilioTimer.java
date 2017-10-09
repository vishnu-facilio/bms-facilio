package com.facilio.tasker;

import com.facilio.fw.OrgInfo;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class FacilioTimer {
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static long schedulePeriodicJob(String jobName, long delay, int period, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setPeriod(period);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		return JobStore.addJob(jc);
	}
	
	public static long scheduleOneTimeJob(String jobName, int delay, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(false);
		jc.setPeriod(delay);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		return JobStore.addJob(jc);
	}
	
	private static long getCurrentOrgId() {
		long orgId = -1;
		if(OrgInfo.getCurrentOrgInfo() != null) {
			orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		}
		
		return orgId;
	}
	
}
