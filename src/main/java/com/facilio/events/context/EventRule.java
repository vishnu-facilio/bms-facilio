package com.facilio.events.context;

public class EventRule {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long eventRuleId = -1;
	public long getEventRuleId() {
		return eventRuleId;
	}
	public void setEventRuleId(long eventRuleId) {
		this.eventRuleId = eventRuleId;
	}
	
	private Boolean hasEventFilter;
	public Boolean getHasEventFilter() {
		return hasEventFilter;
	}
	public void setHasEventFilter(Boolean hasEventFilter) {
		this.hasEventFilter = hasEventFilter;
	}
	
	private long filterCriteriaId = -1;
	public long getFilterCriteriaId() {
		return filterCriteriaId;
	}
	public void setFilterCriteriaId(long filterCriteriaId) {
		this.filterCriteriaId = filterCriteriaId;
	}
	
	private Boolean hasCustomizeRule;
	public Boolean getHasCustomizeRule() {
		return hasCustomizeRule;
	}
	public void setHasCustomizeRule(Boolean hasCustomizeRule) {
		this.hasCustomizeRule = hasCustomizeRule;
	}
	
	private long customizeCriteriaId = -1;
	public long getCustomizeCriteriaId() {
		return customizeCriteriaId;
	}
	public void setCustomizeCriteriaId(long customizeCriteriaId) {
		this.customizeCriteriaId = customizeCriteriaId;
	}
	
	private Boolean hasThresholdRule;
	public Boolean getHasThresholdRule() {
		return hasThresholdRule;
	}
	public void setHasThresholdRule(Boolean hasThresholdRule) {
		this.hasThresholdRule = hasThresholdRule;
	}
}
