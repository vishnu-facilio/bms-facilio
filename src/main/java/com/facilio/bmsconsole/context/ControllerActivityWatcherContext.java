package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class ControllerActivityWatcherContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	private int level = -1;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("Controller Watcher [")
									.append("id : ").append(id).append(", ")
									.append("recordTime : ").append(recordTime).append(", ")
									.append("dataInterval : ").append(dataInterval).append(", ")
									.append("level : ").append(level).append(", ")
									.append("completionStatus : ").append(completionStatus)
									.append("]")
									;
		return builder.toString();
	}
}
