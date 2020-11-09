package com.facilio.trigger.context;

public class TriggerLog {

	long id = -1;
	long orgId = -1;
	long triggerId = -1;
	long recordId = -1;
	long executionTime = -1;
	long triggerActionId = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(long triggerId) {
		this.triggerId = triggerId;
	}
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	public long getTriggerActionId() {
		return triggerActionId;
	}
	public void setTriggerActionId(long triggerActionId) {
		this.triggerActionId = triggerActionId;
	}
	
	
}
