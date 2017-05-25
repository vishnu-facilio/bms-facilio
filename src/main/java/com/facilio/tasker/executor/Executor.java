package com.facilio.tasker.executor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class Executor extends Thread {
	
	private Map<String, FacilioJob> jobsMap;
	private ScheduledExecutorService executor = null;
	private String name = null;
	
	public Executor(String name, int noOfThreads, Map<String, FacilioJob> jobsMap) {
		// TODO Auto-generated constructor stub
		this.jobsMap = jobsMap;
		this.name = name;
		executor = Executors.newScheduledThreadPool(noOfThreads);
	}
	
	final int BUFFER_PERIOD = 10*60;//10*60; //in Seconds
	
	@Override
	public void run()
	{
		while (true)
		{
		// 	select * from JobsTable where nextexecutiontime between currenttime and currenttime+10minutes
			
			try {
				long startTime = System.currentTimeMillis()/1000;
				long endTime = startTime+BUFFER_PERIOD;
				
				System.out.println("Executor::"+startTime+"::"+endTime);
				
				List<JobContext> jobs = JobStore.getJobs(name, startTime, endTime);
				
				for(JobContext jc : jobs) {
					try {
						scheduleJob(jc);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				Thread.sleep((endTime*1000)-System.currentTimeMillis());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void scheduleJob(JobContext jc) {
		FacilioJob job = jobsMap.get(jc.getJobName());
		if(job != null) {
			job.setJobContext(jc);
			System.out.println("Scheduling : "+jc);
			executor.schedule(job, (jc.getExecutionTime()-(System.currentTimeMillis()/1000)), TimeUnit.SECONDS);
		}
		else {
			System.err.println(String.format("No such Job with jobname : %s", jc.getJobName()));
		}
	}
	
}
