package com.facilio.events.context;

public class EventProperty {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long eventPropertyId = -1;
	public long getEventPropertyId() {
		return eventPropertyId;
	}
	public void setEventPropertyId(long eventPropertyId) {
		this.eventPropertyId = eventPropertyId;
	}
	
	private Boolean hasEventRule;
	public Boolean getHasEventRule() {
		return hasEventRule;
	}
	public void setHasEventRule(Boolean hasEventRule) {
		this.hasEventRule = hasEventRule;
	}
	
	private Boolean hasMappingRule;
	public Boolean getHasMappingRule() {
		return hasMappingRule;
	}
	public void setHasMappingRule(Boolean hasMappingRule) {
		this.hasMappingRule = hasMappingRule;
	}
}
