package com.facilio.tasker;

import com.facilio.tasker.FacilioTimer;

public abstract class FacilioJob implements Runnable {

	private JobContext jc = null;
	
	public void setJobContext(JobContext jc) {
		this.jc = jc;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(jc.isPeriodic()) {
			long nextExecutionTime = (System.currentTimeMillis()/1000)+jc.getPeriod();
			try {
				FacilioTimer.addJob(jc.getOrgId(), jc.getJobName(), jc.isPeriodic(), jc.getPeriod(), nextExecutionTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		execute(jc);
		
	}
	
	public abstract void execute(JobContext jc);
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
