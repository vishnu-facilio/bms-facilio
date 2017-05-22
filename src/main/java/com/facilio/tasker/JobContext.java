package com.facilio.tasker;

public class JobContext {
	
	public JobContext(long jobId, String orgId, String jobName, int period, boolean isPeriodic, long executionTime) {
		// TODO Auto-generated constructor stub
		this.jobId = jobId;
		this.orgId = orgId;
		this.jobName = jobName;
		this.period = period;
		this.isPeriodic = isPeriodic;
		this.executionTime = executionTime;
	}
	
	private long jobId ;
	
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	private String orgId;
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	private String jobName;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	private boolean isPeriodic;
	
	public boolean isPeriodic() {
		return isPeriodic;
	}
	public void setIsPeriodic(boolean isPeriodic) {
		this.isPeriodic = isPeriodic;
	}
	
	private int period;
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	private long executionTime;
	
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	@Override
	public String toString() {
		return jobId+"::"+orgId+"::"+jobName+"::"+period+"::"+isPeriodic+"::"+executionTime;
	}
}
