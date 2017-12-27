package com.facilio.tasker;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.tasker.executor.ScheduleInfo;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class FacilioTimer {
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static void scheduleJob(JobContext jc, long delay) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		jc.setExecutionTime(nextExecutionTime);
		
		JobStore.addJob(jc);
	}
	
	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName) throws Exception {
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setSchedule(schedule);
		jc.setActive(true);
		jc.setExecutionTime(schedule.nextExecutionTime(getStartTimeInSecond(startTime)));
		jc.setExecutorName(executorName);
		JobStore.addJob(jc);
	}
	
	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, int maxExecution) throws Exception {
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setSchedule(schedule);
		jc.setActive(true);
		jc.setExecutionTime(schedule.nextExecutionTime(getStartTimeInSecond(startTime)));
		jc.setExecutorName(executorName);
		jc.setMaxExecution(maxExecution);
		JobStore.addJob(jc);
	}
	
	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, long endTime) throws Exception {
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setSchedule(schedule);
		jc.setActive(true);
		jc.setExecutionTime(schedule.nextExecutionTime(getStartTimeInSecond(startTime)));
		jc.setExecutorName(executorName);
		jc.setEndExecutionTime(endTime/1000);
		JobStore.addJob(jc);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setPeriod(period);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		JobStore.addJob(jc);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName, int maxExecution) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setPeriod(period);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		jc.setMaxExecution(maxExecution);
		JobStore.addJob(jc);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName, long endTime) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(true);
		jc.setPeriod(period);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		jc.setEndExecutionTime(endTime/1000);
		JobStore.addJob(jc);
	}
	
	public static void scheduleOneTimeJob(long jobId, String jobName, int delay, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(false);
		jc.setPeriod(delay);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		JobStore.addJob(jc);
	}
	
	public static void scheduleOneTimeJob(long jobId, String jobName, long nextExecutionTime, String executorName) throws Exception {
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(false);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		JobStore.addJob(jc);
	}
	
	private static long getCurrentOrgId() {
		long orgId = -1;
		if(AccountUtil.getCurrentOrg() != null) {
			orgId = AccountUtil.getCurrentOrg().getOrgId();
		}
		
		return orgId;
	}
	
	private static long getStartTimeInSecond(long startTime) {
		long startTimeInSecond = startTime/1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time
		
		return startTimeInSecond;
	}
	
}
