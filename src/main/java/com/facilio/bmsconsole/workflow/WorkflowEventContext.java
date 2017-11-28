package com.facilio.bmsconsole.workflow;

public class WorkflowEventContext {
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long eventId;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private ActivityType activityType;
	public int getActivityType() {
		if(activityType != null) {
			return activityType.getValue();
		}
		return -1;
	}
	public void setActivityType(int activityType) {
		this.activityType = ActivityType.valueOf(activityType);
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
}
