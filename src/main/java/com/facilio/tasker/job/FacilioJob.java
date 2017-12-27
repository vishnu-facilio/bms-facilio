package com.facilio.tasker.job;

import java.sql.SQLException;
import java.time.Instant;

import com.facilio.accounts.util.AccountUtil;
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
	
	private int retryExecutionCount = 1;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			AccountUtil.cleanCurrentAccount();
			retryExecutionCount++;
			
			if(jc.getTransactionTimeout() != -1) {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setTransactionTimeout(jc.getTransactionTimeout());
			}
			
			long orgId = jc.getOrgId();
			if(orgId != -1) {
				AccountUtil.setCurrentAccount(orgId);
			}
			long nextExecutionTime = getNextExecutionTime();
			jc.setNextExecutionTime(nextExecutionTime);
			
			execute(jc);
			
			if(nextExecutionTime != -1) {
				JobStore.updateNextExecutionTimeAndCount(jc.getJobId(), jc.getJobName(),  nextExecutionTime, jc.getCurrentExecutionCount()+1);
			}
			else {
				JobStore.setInActiveAndUpdateCount(jc.getJobId(), jc.getJobName(), jc.getCurrentExecutionCount()+1);
			}
		}
		catch(Exception e) {
			System.out.println("Exception occurred during execution of job : "+jc);
			e.printStackTrace();
			reschedule();
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
	
	private void reschedule() {
		if(retryExecutionCount <= executor.getMaxRetry()) {
			System.out.println("Rescheduling : "+jc.getJobId()+"::"+jc.getJobName()+" for the "+retryExecutionCount+" time.");
			executor.schedule(this, 1);
		}
		else {
			System.out.println("Max retry exceeded for : "+jc+".\nSo making it inactive");
			try {
				JobStore.setInActive(jc.getJobId(), jc.getJobName());
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
