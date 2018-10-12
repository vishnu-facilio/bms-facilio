package com.facilio.bmsconsole.context;

public class ControllerActivityWatcherContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long recordTime = -1;
	public long getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}
	
	private int dataInterval = -1;
	public int getDataInterval() {
		return dataInterval;
	}
	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
	}
	
	private Boolean completionStatus;
	public Boolean getCompletionStatus() {
		return completionStatus;
	}
	public void setCompletionStatus(Boolean completionStatus) {
		this.completionStatus = completionStatus;
	}
	public boolean isCompleted() {
		if (completionStatus != null) {
			return completionStatus.booleanValue();
		}
		return false;
	}
}
