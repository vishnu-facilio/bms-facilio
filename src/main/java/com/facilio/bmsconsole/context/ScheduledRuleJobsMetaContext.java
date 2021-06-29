package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class ScheduledRuleJobsMetaContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	long id;
	long moduleId;
	long ruleId;
	long recordId;
	long executionTime;
	Boolean isActive;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
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
	public boolean isActive() {
		if (isActive != null) {
			return isActive.booleanValue();
		}
		return false;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsActive() {
		return isActive;
	}
}
