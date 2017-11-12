package com.facilio.events.context;

import com.facilio.bmsconsole.criteria.Criteria;

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
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private Long ruleOrder;
	public Long getRuleOrder() {
		return ruleOrder;
	}
	public void setRuleOrder(Long ruleOrder) {
		this.ruleOrder = ruleOrder;
	}
	
	private Boolean ignoreEvent;
	public Boolean getIgnoreEvent() {
		return ignoreEvent;
	}
	public void setIgnoreEvent(Boolean ignoreEvent) {
		this.ignoreEvent = ignoreEvent;
	}
	
	private Long baseCriteriaId;
	public Long getBaseCriteriaId() {
		return baseCriteriaId;
	}
	public void setBaseCriteriaId(Long baseCriteriaId) {
		this.baseCriteriaId = baseCriteriaId;
	}
	
	private Criteria baseCriteria;
	public Criteria getBaseCriteria() {
		return baseCriteria;
	}
	public void setBaseCriteria(Criteria baseCriteria) {
		this.baseCriteria = baseCriteria;
	}
	
	private Boolean hasCustomizeRule;
	public Boolean getHasCustomizeRule() {
		return hasCustomizeRule;
	}
	public void setHasCustomizeRule(Boolean hasCustomizeRule) {
		this.hasCustomizeRule = hasCustomizeRule;
	}
	
	private Long customizeCriteriaId;
	public Long getCustomizeCriteriaId() {
		return customizeCriteriaId;
	}
	public void setCustomizeCriteriaId(Long customizeCriteriaId) {
		this.customizeCriteriaId = customizeCriteriaId;
	}
	
	private Criteria customizeCriteria;
	public Criteria getCustomizeCriteria() {
		return customizeCriteria;
	}
	public void setCustomizeCriteria(Criteria customizeCriteria) {
		this.customizeCriteria = customizeCriteria;
	}
	
	private Long alarmTemplateId;
	public Long getAlarmTemplateId() {
		return alarmTemplateId;
	}
	public void setAlarmTemplateId(Long alarmTemplateId) {
		this.alarmTemplateId = alarmTemplateId;
	}
	
	private Boolean hasThresholdRule;
	public Boolean getHasThresholdRule() {
		return hasThresholdRule;
	}
	public void setHasThresholdRule(Boolean hasThresholdRule) {
		this.hasThresholdRule = hasThresholdRule;
	}
	
	private Long createAlarmCriteriaId;
	public Long getCreateAlarmCriteriaId() {
		return createAlarmCriteriaId;
	}
	public void setCreateAlarmCriteriaId(Long createAlarmCriteriaId) {
		this.createAlarmCriteriaId = createAlarmCriteriaId;
	}
	
	private Criteria createAlarmCriteria;
	public Criteria getCreateAlarmCriteria() {
		return createAlarmCriteria;
	}
	public void setCreateAlarmCriteria(Criteria createAlarmCriteria) {
		this.createAlarmCriteria = createAlarmCriteria;
	}
	
	private Long createAlarmOccurs;
	public Long getCreateAlarmOccurs() {
		return createAlarmOccurs;
	}
	public void setCreateAlarmOccurs(Long createAlarmOccurs) {
		this.createAlarmOccurs = createAlarmOccurs;
	}
	
	private Long createAlarmOverseconds;
	public Long setCreateAlarmOverseconds() {
		return createAlarmOverseconds;
	}
	public void setCreateAlarmOverseconds(Long createAlarmOverseconds) {
		this.createAlarmOverseconds = createAlarmOverseconds;
	}
}
