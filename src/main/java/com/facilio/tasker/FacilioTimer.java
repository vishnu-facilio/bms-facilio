package com.facilio.tasker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.facilio.service.FacilioService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class FacilioTimer {
	
	private static final Logger LOGGER = LogManager.getLogger(FacilioTimer.class.getName());
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName) throws Exception {
		long nextExecutionTime = schedule.nextExecutionTime(getStartTimeInSecond(startTime));
		scheduleJob(jobId, jobName, nextExecutionTime, -1, schedule, executorName, -1, -1);
	}
	
	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, int maxExecution) throws Exception {
		long nextExecutionTime = schedule.nextExecutionTime(getStartTimeInSecond(startTime));
		scheduleJob(jobId, jobName, nextExecutionTime, -1, schedule, executorName, maxExecution, -1);
	}
	
	public static void scheduleCalendarJob(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, long endTime) throws Exception {
		long nextExecutionTime = schedule.nextExecutionTime(getStartTimeInSecond(startTime));
		scheduleJob(jobId, jobName, nextExecutionTime, -1, schedule, executorName, -1, endTime);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		scheduleJob(jobId, jobName, nextExecutionTime, period, null, executorName, -1, -1);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName, int maxExecution) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		scheduleJob(jobId, jobName, nextExecutionTime, period, null, executorName, maxExecution, -1);
	}
	
	public static void schedulePeriodicJob(long jobId, String jobName, long delay, int period, String executorName, long endTime) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		scheduleJob(jobId, jobName, nextExecutionTime, period, null, executorName, -1, endTime);
	}
	
	private static void scheduleJob(long jobId, String jobName, long nextExecutionTime, int period, ScheduleInfo schedule, String executorName, int maxExecution, long endTime) throws Exception {
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		
		if(period != -1) {
			jc.setPeriod(period);
			jc.setIsPeriodic(true);
		}
		else if (schedule != null) {
			jc.setSchedule(schedule);
			if(schedule.getFrequencyTypeEnum() != FrequencyType.DO_NOT_REPEAT) {
				jc.setIsPeriodic(true);
			}
		}
		else {
			throw new IllegalArgumentException("Either period or schedule info has to be set for repeating Jobs");
		}
		if (maxExecution != -1) {
			jc.setMaxExecution(maxExecution);
		}
		if(endTime != -1) {
			jc.setEndExecutionTime(endTime/1000);
		}

		if (AccountUtil.getCurrentAccount() != null) {
			jc.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
		}
		FacilioService.runAsService(() -> JobStore.addJob(jc));
	}

	public static void scheduleInstantJob(String jobName, FacilioContext context){
		context.put(InstantJobConf.getJobNameKey(), jobName);
		context.put(InstantJobConf.getAccountKey(), AccountUtil.getCurrentAccount());
		
//		StringJoiner size = new StringJoiner(",\n");
//		for (Object key : context.keySet()) {
//			size.add(new StringBuilder().append(key).append(" : ").append(SizeOf.sizeOf(context.get(key))).toString());
//		}
//		LOGGER.info(new StringBuilder().append("Adding instant job : ").append(jobName).append("\n Size : \n").append(size.toString()));
		LOGGER.debug("Adding instant job : "+jobName);
		if (!ObjectQueue.sendMessage(InstantJobConf.getInstantJobQueue(), context)) {
			throw new IllegalArgumentException("Unable to add instant job to queue");
		}
		
	}

	public static void scheduleOneTimeJob(long jobId, String jobName, int delayInSec, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delayInSec;
		scheduleOneTimeJob(jobId, jobName, nextExecutionTime, executorName);
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
		if (AccountUtil.getCurrentAccount() != null) {
			jc.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
		}
		FacilioService.runAsService(() -> JobStore.addJob(jc));
	}
	
	public static void deleteJob(long jobId, String jobName) throws Exception {
		FacilioService.runAsService(() -> JobStore.deleteJob(jobId, jobName));
	}
	
	public static void deleteJobs(List<Long> jobIds, String jobName) throws Exception {
		FacilioService.runAsService(() -> JobStore.deleteJobs(jobIds, jobName));
	}
	
	public static JobContext getJob(long jobId, String jobName) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> JobStore.getJob(jobId, jobName));
	}
	
	public static List<JobContext> getJobs(List<Long> jobIds, String jobName) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> JobStore.getJobs(jobIds, jobName));
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
