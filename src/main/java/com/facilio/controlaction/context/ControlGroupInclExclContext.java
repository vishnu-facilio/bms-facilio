package com.facilio.controlaction.context;

public class ControlGroupInclExclContext {
	long id = -1l;
	long orgId = -1l;
	long controlGroupId = -1l;
	long resourceId = -1l;
	Boolean isInclude;
	
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
	public long getControlGroupId() {
		return controlGroupId;
	}
	public void setControlGroupId(long controlGroupId) {
		this.controlGroupId = controlGroupId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public Boolean getIsInclude() {
		return isInclude;
	}
	public void setIsInclude(Boolean isInclude) {
		this.isInclude = isInclude;
	}
}
