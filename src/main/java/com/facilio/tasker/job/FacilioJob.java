package com.facilio.tasker.job;

public abstract class FacilioJob implements Runnable {

	private JobContext jc = null;
	
	public void setJobContext(JobContext jc) {
		this.jc = jc;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		long nextExecutionTime = (System.currentTimeMillis()/1000)+jc.getPeriod();
		
		try {
			execute(jc);
			
			if(jc.isPeriodic()) {
				JobStore.updateNextExecutionTime(jc.getJobId(), nextExecutionTime);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public abstract void execute(JobContext jc);
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
