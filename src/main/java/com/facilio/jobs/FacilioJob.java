package com.facilio.jobs;

import java.util.TimerTask;

public class FacilioJob extends TimerTask {

	private JobContext jc = null;
	public void setJobContext(JobContext jc)
	{
		this.jc = jc;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		execute(null);
		
		// Update last executed time for this jobid
	}
	
	public void execute(JobContext jc)
	{
		
	}

}
