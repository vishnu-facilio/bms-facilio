package com.facilio.controlaction.context;

public class ControlGroupSpace {
	long id = -1l;
	long orgId = -1l;
	long controlGroupId = -1l;
	long spaceId;
	
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
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
}
