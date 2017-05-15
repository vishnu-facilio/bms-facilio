package com.facilio.jobs.executor;

import java.util.Timer;

import com.facilio.jobs.FacilioJob;

public class Executor {
	
	public void initScheduler() throws Exception
	{
		Timer tim = new Timer();
		while (true)
		{
		// 	select * from JobsTable where nextexecutiontime between currenttime and currenttime+10minutes
			
			long time = 0; // compute from next execution time
			FacilioJob fj = new FacilioJob();
			
			tim.schedule(fj, time);


			Thread.sleep(6000000);
			
		}
	}

}
