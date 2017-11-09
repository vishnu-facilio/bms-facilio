package com.facilio.events.context;

import java.util.Map;

import com.facilio.bmsconsole.criteria.Condition;

public class EventThresholdRule {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long eventThresholdRuleId = -1;
	public long getEventThresholdRuleId() {
		return eventThresholdRuleId;
	}
	public void setEventThresholdRuleId(long eventThresholdRuleId) {
		this.eventThresholdRuleId = eventThresholdRuleId;
	}
	
	private long eventRuleId = -1;
	public long getEventRuleId() {
		return eventRuleId;
	}
	public void setEventRuleId(long eventRuleId) {
		this.eventRuleId = eventRuleId;
	}
	
	private Boolean hasFilterCriteria;
	public Boolean getHasFilterCriteria() {
		return hasFilterCriteria;
	}
	public void setHasFilterCriteria(Boolean hasFilterCriteria) {
		this.hasFilterCriteria = hasFilterCriteria;
	}
	
	private Map<Integer, Condition> filterConditions;
	public Map<Integer, Condition> getFilterConditions() {
		return filterConditions;
	}
	public void setFilterConditions(Map<Integer, Condition> filterConditions) {
		this.filterConditions = filterConditions;
	}
	
	private String filterPattern;
	public String getFilterPattern() {
		return filterPattern;
	}
	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
	}
	
	private Long filterCriteriaId;
	public Long getFilterCriteriaId() {
		return filterCriteriaId;
	}
	public void setFilterCriteriaId(Long filterCriteriaId) {
		this.filterCriteriaId = filterCriteriaId;
	}
	
	private Integer filterCriteriaOccurs;
	public Integer getFilterCriteriaOccurs() {
		return filterCriteriaOccurs;
	}
	public void setFilterCriteriaOccurs(Integer filterCriteriaOccurs) {
		this.filterCriteriaOccurs = filterCriteriaOccurs;
	}
	
	private Integer filterCriteriaOverseconds;
	public Integer getFilterCriteriaOverseconds() {
		return filterCriteriaOverseconds;
	}
	public void setFilterCriteriaOverseconds(Integer filterCriteriaOverseconds) {
		this.filterCriteriaOverseconds = filterCriteriaOverseconds;
	}
	
	private Boolean hasClearCriteria;
	public Boolean getHasClearCriteria() {
		return hasClearCriteria;
	}
	public void setHasClearCriteria(Boolean hasClearCriteria) {
		this.hasClearCriteria = hasClearCriteria;
	}

	private Map<Integer, Condition> clearConditions;
	public Map<Integer, Condition> getClearConditions() {
		return clearConditions;
	}
	public void setClearConditions(Map<Integer, Condition> clearConditions) {
		this.clearConditions = clearConditions;
	}
	
	private String clearPattern;
	public String getClearPattern() {
		return clearPattern;
	}
	public void setClearPattern(String clearPattern) {
		this.clearPattern = clearPattern;
	}
	
	private Long clearCriteriaId;
	public Long getClearCriteriaId() {
		return clearCriteriaId;
	}
	public void setClearCriteriaId(Long clearCriteriaId) {
		this.clearCriteriaId = clearCriteriaId;
	}
	
	private Integer clearCriteriaOccurs;
	public Integer getClearCriteriaOccurs() {
		return clearCriteriaOccurs;
	}
	public void setClearCriteriaOccurs(Integer clearCriteriaOccurs) {
		this.clearCriteriaOccurs = clearCriteriaOccurs;
	}
	
	private Integer clearCriteriaOverseconds;
	public Integer getClearCriteriaOverseconds() {
		return clearCriteriaOverseconds;
	}
	public void setClearCriteriaOverseconds(Integer clearCriteriaOverseconds) {
		this.clearCriteriaOverseconds = clearCriteriaOverseconds;
	}
	
	private Integer ruleOrder;
	public Integer getRuleOrder() {
		return ruleOrder;
	}
	public void setRuleOrder(Integer ruleOrder) {
		this.ruleOrder = ruleOrder;
	}
}
