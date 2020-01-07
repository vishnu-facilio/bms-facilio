package com.facilio.tasker.job;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.jobs.JobLogger;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.executor.Executor;
import com.facilio.util.SentryUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.time.Instant;

public abstract class FacilioJob implements Runnable {

    private static Logger LOGGER = LogManager.getLogger(FacilioJob.class.getName());
	private JobContext jc = null;
	public void setJobContext(JobContext jc) {
		this.jc = jc;
	}
	
	private Executor executor = null;
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	private int retryExecutionCount = 1;
	
	@Override
	public void run() {
		long startTime = 0L;
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		currentThread.setName(threadName + "-" + jc.getJobId()+"-"+ jc.getJobName());
		int status = 0;
		try {
			AccountUtil.cleanCurrentAccount();
			if ( JobStore.updateStartExecution(jc.getJobId(), jc.getJobName(), jc.getJobStartTime(), jc.getJobExecutionCount()) < 1 ) {
				executor.jobEnd(jc.getJobKey());
				return;
			}
			startTime = System.currentTimeMillis();
			LOGGER.debug("Starting job " + jc.getJobKey());
			retryExecutionCount++;

			long orgId = jc.getOrgId();
			if(orgId != -1) {
				AccountUtil.setCurrentAccount(orgId);
				AccountUtil.setReqUri(jc.getJobName());
				if (jc.getLoggerLevel() != -1) {
					AccountUtil.getCurrentAccount().setLoggerLevel(jc.getLoggerLevel());
				}
			}
			if (StringUtils.isNotEmpty(jc.getTimezone())) {
				AccountUtil.setTimeZone(jc.getTimezone());
			}
			jc.setNextExecutionTime(getNextExecutionTime());
			FacilioContext context = new FacilioContext();
			context.put(JobConstants.JOB_CONTEXT, jc);
			context.put(JobConstants.FACILIO_JOB, this);
			JobConstants.ChainFactory.jobExecutionChain(jc.getTransactionTimeout()).execute(context);
			status = 1;
			executor.jobEnd(jc.getJobKey());
		}
		catch(Exception e) {
			status = 2;
			LOGGER.error("Job execution failed for Job :"+jc.getJobId()+" : "+ jc.getJobName(),e);
			SentryUtil.handleSchedulerExceptions(jc,e);
			// CommonCommandUtil.emailException("FacilioJob", "Job execution failed for Job : "+jc.getJobId()+" : "+ jc.getJobName(), e);
//			reschedule();
		} finally {
			long timeTaken = (System.currentTimeMillis()-startTime);
			if(status == 0) {
				timeTaken = 1;
			}
			JobLogger.log(jc, timeTaken, status);
			AccountUtil.cleanCurrentAccount();
			if(status == 1) {
				updateNextExecutionTime();
			}
			LOGGER.debug("Job completed " +jc.getJobId()+"-"+ jc.getJobName() + " time taken : " + timeTaken);
			currentThread.setName(threadName);
		}
	}

	private long getNextExecutionTime() {
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

	private void updateNextExecutionTime() {
		try {
			if (jc.getNextExecutionTime() != -1) {
				JobStore.updateNextExecutionTimeAndCount(jc.getJobId(), jc.getJobName(), jc.getNextExecutionTime(), jc.getCurrentExecutionCount() + 1);
			} else {
				JobStore.setInActiveAndUpdateCount(jc.getJobId(), jc.getJobName(), jc.getCurrentExecutionCount() + 1);
			}
		} catch (SQLException e) {
			LOGGER.error("Exception while updating next execution time ", e);
		}
	}

	private void reschedule() {
		if(retryExecutionCount <= executor.getMaxRetry()) {
			LOGGER.error("Rescheduling : "+jc.getJobId()+"::"+jc.getJobName()+" for the "+retryExecutionCount+" time.");
			executor.reSchedule(this, jc);
		}
		else {
			LOGGER.error("Max retry exceeded for : "+jc+".\nSo making it inactive");
			CommonCommandUtil.emailException("FacilioJob", "Max retry exceeded for Job : "+jc.getJobId()+" : "+ jc.getJobName(), "Since max retries exceeded for job : "+jc.getJobId()+"-"+jc.getJobName()+", making it inactive.");
			try {
				JobStore.setInActive(jc.getJobId(), jc.getJobName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error ",e);
			}
		}
	}
	public abstract void execute(JobContext jc) throws Exception;
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

	public void handleTimeOut() {

	}

}
