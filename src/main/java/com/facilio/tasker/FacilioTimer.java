package com.facilio.tasker;

import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.service.FacilioService;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

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
		jc.setAddedTime(System.currentTimeMillis());
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
		FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.addJob(jc));
		FacilioService.runAsService(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.addJob(jc));
	}

	private static final String DEFAULT_INSTANT_JOB_EXECUTOR = "default";
	public static void scheduleInstantJob(String jobName, FacilioContext context) throws Exception {
		LOGGER.debug("Adding instant job : "+jobName);
		FacilioInstantJobScheduler.addInstantJob(DEFAULT_INSTANT_JOB_EXECUTOR, jobName, context);
	}

	public static void scheduleInstantJob(String executorName, String jobName, FacilioContext context) throws Exception {
		LOGGER.debug("Adding instant job : "+jobName);
		FacilioInstantJobScheduler.addInstantJob(executorName, jobName, context);
	}

	public static void scheduleOneTimeJobWithDelay(long jobId, String jobName, int delayInSec, String executorName) throws Exception {
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delayInSec;
		scheduleOneTimeJobWithTimestampInSec(jobId, jobName, nextExecutionTime, executorName);
	}
	
	public static void scheduleOneTimeJobWithTimestampInSec(long jobId, String jobName, long nextExecutionTime, String executorName) throws Exception {
		
		JobContext jc = new JobContext();
		jc.setJobId(jobId);
		jc.setOrgId(getCurrentOrgId());
		jc.setJobName(jobName);
		jc.setIsPeriodic(false);
		jc.setActive(true);
		jc.setExecutionTime(nextExecutionTime);
		jc.setExecutorName(executorName);
		jc.setAddedTime(System.currentTimeMillis());
		if (AccountUtil.getCurrentAccount() != null) {
			jc.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
		}
		FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.addJob(jc));
		FacilioService.runAsService(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.addJob(jc));
	}
	
	public static void deleteJob(long jobId, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.deleteJob(orgId, jobId, jobName));
		FacilioService.runAsService(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.deleteJob(orgId, jobId, jobName));
	}
	
	public static void deleteJobs(List<Long> jobIds, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.deleteJobs(orgId, jobIds, jobName));
		FacilioService.runAsService(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.deleteJobs(orgId, jobIds, jobName));
	}
	
	public static JobContext getJob(long jobId, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.getJob(orgId, jobId, jobName));
	}
	
	public static List<JobContext> getJobs(List<Long> jobIds, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.getJobs(orgId, jobIds, jobName));
	}

	public static int activateJob(long jobId, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		FacilioService.runAsServiceWihReturn(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.setStatusForJob(orgId, jobId, jobName, true));
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.setStatusForJob(orgId, jobId, jobName, true));
	}

	public static int inActivateJob(long jobId, String jobName) throws Exception {
		long orgId = getCurrentOrgId();
		FacilioService.runAsServiceWihReturn(FacilioConstants.Services.TEMP_JOBS,() -> JobStore.setStatusForJob(orgId, jobId, jobName, false));
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,() -> JobStore.setStatusForJob(orgId, jobId, jobName, false));
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

	public static void scheduleInstantJobInPostTransaction(String jobName, FacilioContext instantJobContext) throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddInstantJobChain();
		FacilioContext context = chain.getContext();
		context.putAll(instantJobContext);

		context.put(FacilioConstants.ContextNames.INSTANT_JOB_NAME, jobName);
		chain.execute();
	}
}
