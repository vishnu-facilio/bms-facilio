package com.facilio.tasker.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.server.ServerInfo;
import com.facilio.service.FacilioService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.SchedulerJobConf;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;
import com.facilio.tasker.job.JobTimeOutInfo;

public class Executor implements Runnable {

	private static final int MAX_RETRY = 5;
	private static final Logger LOGGER = LogManager.getLogger(Executor.class.getName());
	private static final int JOB_TIMEOUT_BUFFER = 0;

	private ScheduledExecutorService executor = null;
	private String name = null;
	private int bufferPeriod;
	private int maxRetry = MAX_RETRY;
	private final ConcurrentMap<String, JobTimeOutInfo> jobMonitor = new ConcurrentHashMap<>();
	private List<Long> includedOrgs = null, excludedOrgs = null;

	public Executor(String name, int noOfThreads, int bufferPeriod, List<Long> includedOrgs, List<Long> excludedOrgs) {
		// TODO Auto-generated constructor stub
		this(name, noOfThreads, bufferPeriod, -1, includedOrgs, excludedOrgs);
	}
	
	public Executor(String name, int noOfThreads, int bufferPeriod, int maxRetry, List<Long> includedOrgs, List<Long> excludedOrgs) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.bufferPeriod = bufferPeriod;
		if(maxRetry != -1) {
			this.maxRetry = maxRetry;
		}
		this.includedOrgs = includedOrgs;
		this.excludedOrgs = excludedOrgs;
		
		executor = Executors.newScheduledThreadPool(noOfThreads+1);
		executor.scheduleAtFixedRate(this, 0, bufferPeriod*1000, TimeUnit.MILLISECONDS);
	}
	private int getNoOfFreeThreads(){
		int freeThreads = 0;
		if (executor instanceof ScheduledThreadPoolExecutor) {
			ScheduledThreadPoolExecutor implementation = (ScheduledThreadPoolExecutor) executor;
			freeThreads = implementation.getCorePoolSize() - implementation.getQueue().size();
		}
		return freeThreads;
	}
	@Override
	public void run()
	{
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		currentThread.setName("Executor-"+this.name);
		try {
			AccountUtil.cleanCurrentAccount();
			handleTimeOut();
			long startTime = System.currentTimeMillis()/1000;
			long endTime = startTime+bufferPeriod;
			
			LOGGER.debug(name+"::"+startTime+"::"+endTime);
			int freeThreads = getNoOfFreeThreads();
			LOGGER.info("Initial number of free threads  : "+freeThreads);
			List<JobContext> scheduledJobs = updateScheduledStatus(FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,()->JobStore.getIncompletedJobs(name, startTime, endTime, getMaxRetry(), includedOrgs, excludedOrgs,freeThreads)));
			scheduledJobs.addAll(updateScheduledStatus(FacilioService.runAsServiceWihReturn(FacilioConstants.Services.JOB_SERVICE,()->JobStore.getJobs(name, startTime, endTime, getMaxRetry(), includedOrgs, excludedOrgs,(freeThreads-scheduledJobs.size())))));
			LOGGER.info("Final Jobs to ready to execute count is  : "+(scheduledJobs.size()));
			for(JobContext jc : scheduledJobs) {
				try {
					scheduleJob(jc);
				}
				catch(Exception e) {
					LOGGER.error("Unable to schedule job : "+jc.getJobName());
					LOGGER.error("Exception occurred ", e);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred ", e);
			CommonCommandUtil.emailException("Executor", this.name+" - Executor Down", e);
		}
		finally {
			currentThread.setName(threadName);
		}
	}

	private List<JobContext> updateScheduledStatus ( List<JobContext> jobs ) {
		List<JobContext> scheduledJobs = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(jobs)) {
			for (JobContext job : jobs){
				int rowsUpdated = 0;
				String query = "update Jobs set STATUS = 4 where ORGID = ? AND JOBID = ? and JOBNAME= ? and EXECUTION_ERROR_COUNT = ?";
				try(Connection connection = FacilioConnectionPool.getInstance().getDirectConnection();
					PreparedStatement statement = connection.prepareStatement(query)){
					statement.setLong(1, job.getOrgId());
					statement.setLong(2, job.getJobId());
					statement.setString(3,job.getJobName());
					statement.setInt(4, job.getJobExecutionCount());
					rowsUpdated = statement.executeUpdate();
					if(rowsUpdated == 1){
						scheduledJobs.add(job);
					}
					LOGGER.debug("query : " + statement.toString());
				} catch (SQLException e) {
					LOGGER.error("Exception while updating Job " + job.getJobName() + "_" + job.getJobId(), e);
				}
				LOGGER.debug("Updated Job " + job.getJobName() + " " + rowsUpdated );
			}
		}
		LOGGER.info("Successfully scheduled updated Jobs "+scheduledJobs.size() +" out of "+ jobs.size());
		return scheduledJobs;
	}

	private void scheduleJob(JobContext jc) throws InstantiationException, IllegalAccessException  {
		SchedulerJobConf.Job schedulerJobs = FacilioScheduler.getSchedulerJob(jc.getJobName());
		if(schedulerJobs != null) {
			Class<? extends FacilioJob> jobClass = schedulerJobs.getClassObject();
			if (jobClass != null) {
				FacilioJob job = jobClass.newInstance();
				job.setJobContext(jc);
				job.setExecutor(this);

				LOGGER.debug("Scheduling : " + jc);
				schedule(job, jc);
			} else {
				LOGGER.info("No such Job with jobname : " + jc.getJobName());
			}
		}
	}

	public void jobEnd (String jobKey) {
		jobMonitor.remove(jobKey);
	}


	public void schedule(FacilioJob job, JobContext jc) {
		long delay = (jc.getExecutionTime() - (System.currentTimeMillis() / 1000));
		Future f = executor.schedule (job, delay, TimeUnit.SECONDS);
		if (delay > 0) { //To Handle jobs that has execution time in the past which are considered as immediate execution
			jobMonitor.put(jc.getJobKey(), new JobTimeOutInfo(jc.getExecutionTime() * 1000, (jc.getTransactionTimeout() + JOB_TIMEOUT_BUFFER), f, job));
		}
		else {
			jobMonitor.put(jc.getJobKey(), new JobTimeOutInfo(System.currentTimeMillis()+1000, (jc.getTransactionTimeout() + JOB_TIMEOUT_BUFFER), f, job));
		}
	}

	public void reSchedule(FacilioJob job, JobContext jc) {
		jobEnd(jc.getJobKey());
		Future f = executor.schedule (job, 1, TimeUnit.SECONDS);
		jobMonitor.put(jc.getJobKey(), new JobTimeOutInfo(System.currentTimeMillis()+1000, (jc.getTransactionTimeout() + JOB_TIMEOUT_BUFFER), f, job));
	}
	
	public void shutdown() {
		executor.shutdownNow();
	}
	
	public int getMaxRetry() {
		return maxRetry;
	}

	private void handleTimeOut() {
		Iterator<Map.Entry<String, JobTimeOutInfo>> itr = jobMonitor.entrySet().iterator();
		long currentTime = System.currentTimeMillis();
		while (itr.hasNext()) {
			Map.Entry<String, JobTimeOutInfo> entry = itr.next();
			JobTimeOutInfo info = entry.getValue();
			if (currentTime >= (info.getExecutionTime()+info.getTimeOut())) {
				if (info.getFuture().cancel(true)) {
					LOGGER.info("Timing out job : "+entry.getKey());
					info.getFacilioJob().handleTimeOut();
					itr.remove();
				}
			}
		}
	}
	
}
