package com.facilio.events.context;

public class EventProperty {
	
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
	
	private Boolean isEventEnabled;
	public Boolean getIsEventEnabled() {
		return isEventEnabled;
	}
	public void setIsEventEnabled(Boolean isEventEnabled) {
		this.isEventEnabled = isEventEnabled;
	}
	public boolean isEventEnabled() {
		if(isEventEnabled != null) {
			return isEventEnabled.booleanValue();
		}
		return false;
	}
	
	private String eventTopicName;
	public String getEventTopicName() {
		return eventTopicName;
	}
	public void setEventTopicName(String eventTopicName) {
		this.eventTopicName = eventTopicName;
	}
}
