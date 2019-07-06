package com.facilio.tasker.executor;

import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.SchedulerJobConf;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;
import com.facilio.tasker.job.JobTimeOutInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Executor implements Runnable {
	
	private static final int MAX_RETRY = 5;
	private static final Logger LOGGER = LogManager.getLogger(Executor.class.getName());
	private static final int JOB_TIMEOUT_BUFFER = 5000;

	private ScheduledExecutorService executor = null;
	private String name = null;
	private int bufferPeriod;
	private int maxRetry = MAX_RETRY;
	private final ConcurrentMap<String, JobTimeOutInfo> jobMonitor = new ConcurrentHashMap<>();

	public Executor(String name, int noOfThreads, int bufferPeriod) {
		// TODO Auto-generated constructor stub
		this(name, noOfThreads, bufferPeriod, -1);
	}
	
	public Executor(String name, int noOfThreads, int bufferPeriod, int maxRetry) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.bufferPeriod = bufferPeriod;
		if(maxRetry != -1) {
			this.maxRetry = maxRetry;
		}
		
		executor = Executors.newScheduledThreadPool(noOfThreads+1);
		executor.scheduleAtFixedRate(this, 0, bufferPeriod*1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run()
	{
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		currentThread.setName("Executor-"+this.name);
		try {
			handleTimeOut();
			long startTime = System.currentTimeMillis()/1000;
			long endTime = startTime+bufferPeriod;
			
			LOGGER.debug(name+"::"+startTime+"::"+endTime);
			
			List<JobContext> jobs = JobStore.getJobs(name, startTime, endTime, getMaxRetry());
			jobs.addAll(JobStore.getIncompletedJobs(name, startTime, endTime, getMaxRetry()));
			for(JobContext jc : jobs) {
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
		}
		finally {
			currentThread.setName(threadName);
		}
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
		Future f = executor.schedule (job, (jc.getExecutionTime() - (System.currentTimeMillis() / 1000)), TimeUnit.SECONDS);
		jobMonitor.put(jc.getJobKey(), new JobTimeOutInfo(jc.getExecutionTime()*1000, (jc.getTransactionTimeout() + JOB_TIMEOUT_BUFFER), f, job));
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
			JobTimeOutInfo info = itr.next().getValue();
			if (currentTime >= (info.getExecutionTime()+info.getTimeOut())) {
				if (info.getFuture().cancel(true)) {
					info.getFacilioJob().handleTimeOut();
					itr.remove();
				}
			}
		}
	}
	
}
