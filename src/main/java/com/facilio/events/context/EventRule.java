package com.facilio.events.context;

import com.facilio.bmsconsole.criteria.Criteria;
import org.json.simple.JSONObject;

import java.util.List;

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
	
	private boolean ignoreEvent;
	public boolean getIgnoreEvent() {
		return ignoreEvent;
	}
	public void setIgnoreEvent(boolean ignoreEvent) {
		this.ignoreEvent = ignoreEvent;
	}
	public boolean isIgnoreEvent() {
		return this.ignoreEvent;
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
	
	private String coRelWorkflowXml;
	public String getCoRelWorkflowXml() {
		return coRelWorkflowXml;
	}
	public void setCoRelWorkflowXml(String coRelWorkflowXml) {
		this.coRelWorkflowXml = coRelWorkflowXml;
	}

	private long coRelWorkflowId = -1;
	public long getCoRelWorkflowId() {
		return coRelWorkflowId;
	}
	public void setCoRelWorkflowId(long coRelWorkflowId) {
		this.coRelWorkflowId = coRelWorkflowId;
	}

	private JSONObject coRelTransformTemplate;
	public JSONObject getCoRelTransformTemplate() {
		return coRelTransformTemplate;
	}
	public void setCoRelTransformTemplate(JSONObject coRelTransformTemplate) {
		this.coRelTransformTemplate = coRelTransformTemplate;
	}

	private long coRelTransformTemplateId = -1;
	public long getCoRelTransformTemplateId() {
		return coRelTransformTemplateId;
	}
	public void setCoRelTransformTemplateId(long coRelTransformTemplateId) {
		this.coRelTransformTemplateId = coRelTransformTemplateId;
	}

	private List<EventToAlarmFieldMapping> fieldMappings;
	public List<EventToAlarmFieldMapping> getFieldMappings() {
		return fieldMappings;
	}
	public void setFieldMappings(List<EventToAlarmFieldMapping> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
	
	private ColRelationAction colRelAction;
	public int getColRelAction() {
		if(colRelAction != null) {
			return colRelAction.getValue();
		}
		return -1;
	}
	public void setColRelAction(int colRelAction) {
		this.colRelAction = ColRelationAction.valueOf(colRelAction);
	}
	public void setColRelAction(ColRelationAction colRelAction) {
		this.colRelAction = colRelAction;
	}
	public ColRelationAction getColRelActionEnum() {
		return colRelAction;
	}
	
	public static enum ColRelationAction {
		IGNORE,
		TRANSFORM
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ColRelationAction valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}
}
