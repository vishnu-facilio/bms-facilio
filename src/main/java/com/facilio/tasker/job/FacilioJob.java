package com.facilio.tasker.job;

import java.sql.SQLException;

import com.facilio.tasker.executor.Executor;
import com.facilio.transaction.FacilioTransactionManager;

public abstract class FacilioJob implements Runnable {

	private JobContext jc = null;
	public void setJobContext(JobContext jc) {
		this.jc = jc;
	}
	
	private Executor executor = null;
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	private int executionCount = 1;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		long nextExecutionTime = (System.currentTimeMillis()/1000)+jc.getPeriod();
		
		try {
			executionCount++;
			
			if(jc.getTransactionTimeout() != -1) {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setTransactionTimeout(jc.getTransactionTimeout());
			}
			
			execute(jc);
			
			if(jc.isPeriodic()) {
				JobStore.updateNextExecutionTime(jc.getJobId(), nextExecutionTime);
			}
			else {
				JobStore.setInActive(jc.getJobId());
			}
		}
		catch(Exception e) {
			System.out.println("Exception occurred during execution of job : "+jc);
			e.printStackTrace();
			reschedule();
		}
		
	}
	
	private void reschedule() {
		if(executionCount <= executor.getMaxRetry()) {
			System.out.println("Rescheduling : "+jc.getJobId()+"::"+jc.getJobName()+" for the "+executionCount+" time.");
			executor.schedule(this, 1);
		}
		else {
			System.out.println("Max retry exceeded for : "+jc+".\nSo making it inactive");
			try {
				JobStore.setInActive(jc.getJobId());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public abstract void execute(JobContext jc);
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
