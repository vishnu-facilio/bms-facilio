package com.facilio.events.context;

import java.util.List;

import org.json.simple.JSONObject;

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
	
	public long getId() {
		return getEventRuleId();
	}
	public void setId(long id) {
		this.setEventRuleId(id);
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long ruleOrder = -1;
	public long getRuleOrder() {
		return ruleOrder;
	}
	public void setRuleOrder(long ruleOrder) {
		this.ruleOrder = ruleOrder;
	}

	private long baseCriteriaId = -1;
	public long getBaseCriteriaId() {
		return baseCriteriaId;
	}
	public void setBaseCriteriaId(long baseCriteriaId) {
		this.baseCriteriaId = baseCriteriaId;
	}
	
	private Criteria baseCriteria;
	public Criteria getBaseCriteria() {
		return baseCriteria;
	}
	public void setBaseCriteria(Criteria baseCriteria) {
		this.baseCriteria = baseCriteria;
	}
	
	private Boolean ignoreEvent;
	public Boolean getIgnoreEvent() {
		return ignoreEvent;
	}
	public void setIgnoreEvent(Boolean ignoreEvent) {
		this.ignoreEvent = ignoreEvent;
	}
	public boolean isIgnoreEvent() {
		if(ignoreEvent != null) {
			return ignoreEvent.booleanValue();
		}
		return false;
	}

//	private Boolean hasCustomizeRule;
//	public Boolean getHasCustomizeRule() {
//		return hasCustomizeRule;
//	}
//	public void setHasCustomizeRule(Boolean hasCustomizeRule) {
//		this.hasCustomizeRule = hasCustomizeRule;
//	}
//	public boolean hasCustomizeRule() {
//		if(hasCustomizeRule != null) {
//			return hasCustomizeRule.booleanValue();
//		}
//		return false;
//	}
	
	private long transformCriteriaId = -1;
	public long getTransformCriteriaId() {
		return transformCriteriaId;
	}
	public void setTransformCriteriaId(long transformCriteriaId) {
		this.transformCriteriaId = transformCriteriaId;
	}

	private Criteria transformCriteria;
	public Criteria getTransformCriteria() {
		return transformCriteria;
	}
	public void setTransformCriteria(Criteria transformCriteria) {
		this.transformCriteria = transformCriteria;
	}
	
//	private Boolean hasThresholdRule;
//	public Boolean getHasThresholdRule() {
//		return hasThresholdRule;
//	}
//	public void setHasThresholdRule(Boolean hasThresholdRule) {
//		this.hasThresholdRule = hasThresholdRule;
//	}
//	public boolean hasThresholdRule() {
//		if(hasThresholdRule != null) {
//			return hasThresholdRule.booleanValue();
//		}
//		return false;
//	}
	
	private long transformAlertTemplateId = -1;
	public long getTransformAlertTemplateId() {
		return transformAlertTemplateId;
	}
	public void setTransformAlertTemplateId(long transformAlertTemplateId) {
		this.transformAlertTemplateId = transformAlertTemplateId;
	}
	
	private JSONObject transformTemplate;
	public JSONObject getTransformTemplate() {
		return transformTemplate;
	}
	public void setTransformTemplate(JSONObject transformTemplate) {
		this.transformTemplate = transformTemplate;
	}

	private long thresholdCriteriaId = -1;
	public long getThresholdCriteriaId() {
		return thresholdCriteriaId;
	}
	public void setThresholdCriteriaId(long thresholdCriteriaId) {
		this.thresholdCriteriaId = thresholdCriteriaId;
	}

	private Criteria thresholdCriteria;
	public Criteria getThresholdCriteria() {
		return thresholdCriteria;
	}
	public void setThresholdCriteria(Criteria thresholdCriteria) {
		this.thresholdCriteria = thresholdCriteria;
	}

	private int thresholdOccurs = -1;
	public int getThresholdOccurs() {
		return thresholdOccurs;
	}
	public void setThresholdOccurs(int thresholdOccurs) {
		this.thresholdOccurs = thresholdOccurs;
	}

	private int thresholdOverSeconds = -1;
	public int getThresholdOverSeconds() {
		return thresholdOverSeconds;
	}
	public void setThresholdOverSeconds(int thresholdOverSeconds) {
		this.thresholdOverSeconds = thresholdOverSeconds;
	}
	
	private List<EventToAlarmFieldMapping> fieldMappings;
	public List<EventToAlarmFieldMapping> getFieldMappings() {
		return fieldMappings;
	}
	public void setFieldMappings(List<EventToAlarmFieldMapping> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
}
