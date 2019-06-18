package com.facilio.workflows.context;

public class ScheduledWorkflowContext {
	
	long id = -1l;
	long orgid = -1l;
	long workflowId = -1l;
	String timeZone = null;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
}
