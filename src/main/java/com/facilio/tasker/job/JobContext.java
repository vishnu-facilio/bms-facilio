package com.facilio.tasker.job;

import com.cronutils.model.Cron;

public class JobContext {
	
	private long jobId = -1;
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String jobName;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	private boolean active;
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	private int transactionTimeout = -1;
	public int getTransactionTimeout() {
		return transactionTimeout;
	}
	public void setTransactionTimeout(int transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	private boolean isPeriodic;
	public boolean isPeriodic() {
		return isPeriodic;
	}
	public void setIsPeriodic(boolean isPeriodic) {
		this.isPeriodic = isPeriodic;
	}
	
	private int period = -1;
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public String getCronExpression() {
		if(cron != null) {
			return cron.asString();
		}
		return null;
	}
	public void setCronExpression(String cronExpression) {
		this.cron = CronUtil.parse(cronExpression);
	}
	
	private Cron cron;
	public Cron getCron() {
		return cron;
	}
	public void setCron(Cron cron) {
		this.cron = cron;
	}

	private long executionTime = -1;
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	private String executorName;
	public String getExecutorName() {
		return executorName;
	}
	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}
	
	private long endExecutionTime = -1;
	public long getEndExecutionTime() {
		return endExecutionTime;
	}
	public void setEndExecutionTime(long endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}
	
	private int maxExecution = -1;
	public int getMaxExecution() {
		return maxExecution;
	}
	public void setMaxExecution(int maxExecution) {
		this.maxExecution = maxExecution;
	}
	
	private int currentExecutionCount = 0;
	public int getCurrentExecutionCount() {
		return currentExecutionCount;
	}
	public void setCurrentExecutionCount(int currentExecutionCount) {
		this.currentExecutionCount = currentExecutionCount;
	}
	
	@Override
	public String toString() {
		return jobId+"::"+orgId+"::"+jobName+"::"+period+"::"+isPeriodic+"::"+executionTime;
	}
}
