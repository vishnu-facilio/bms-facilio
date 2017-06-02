package com.facilio.tasker.executor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class Executor implements Runnable {
	
	private ScheduledExecutorService executor = null;
	private String name = null;
	private int bufferPeriod;
	
	public Executor(String name, int noOfThreads, int bufferPeriod) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.bufferPeriod = bufferPeriod;
		
		executor = Executors.newScheduledThreadPool(noOfThreads+1);
		executor.scheduleAtFixedRate(this, 0, bufferPeriod*1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run()
	{
//		while (true)
//		{
			try {
				long startTime = System.currentTimeMillis()/1000;
				long endTime = startTime+bufferPeriod;
				
				System.out.println(name+"::"+startTime+"::"+endTime);
				
				List<JobContext> jobs = JobStore.getJobs(name, startTime, endTime);
				
				for(JobContext jc : jobs) {
					try {
						scheduleJob(jc);
					}
					catch(Exception e) {
						System.err.println("Unable to schedule job : "+jc.getJobName());
						e.printStackTrace();
					}
				}
				
//				Thread.sleep((endTime*1000)-System.currentTimeMillis());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
	}
	
	void scheduleJob(JobContext jc) throws InstantiationException, IllegalAccessException  {
		Class<? extends FacilioJob> jobClass = FacilioScheduler.JOBS_MAP.get(jc.getJobName());
		if(jobClass != null) {
			FacilioJob job = jobClass.newInstance();
			job.setJobContext(jc);
			System.out.println("Scheduling : "+jc);
			executor.schedule(job, (jc.getExecutionTime()-(System.currentTimeMillis()/1000)), TimeUnit.SECONDS);
		}
		else {
			System.err.println(String.format("No such Job with jobname : %s", jc.getJobName()));
		}
	}
	
}
