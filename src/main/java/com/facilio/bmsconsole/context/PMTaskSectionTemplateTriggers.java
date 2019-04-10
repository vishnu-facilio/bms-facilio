package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class PMTaskSectionTemplateTriggers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long sectionId;
	long triggerId;
	long executeIfNotInTime;
	String triggerName;
	
	
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
	public long getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(long triggerId) {
		this.triggerId = triggerId;
	}
	public long getExecuteIfNotInTime() {
		return executeIfNotInTime;
	}
	public void setExecuteIfNotInTime(long executeIfNotInTime) {
		this.executeIfNotInTime = executeIfNotInTime;
	}
}
