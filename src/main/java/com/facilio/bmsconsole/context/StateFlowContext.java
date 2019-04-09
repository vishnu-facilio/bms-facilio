package com.facilio.bmsconsole.context;

import java.io.Serializable;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StateFlowContext implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private int sequence;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	private long defaultStateId;
	public long getDefaultStateId() {
		return defaultStateId;
	}
	public void setDefaultStateId(long defaultStateId) {
		this.defaultStateId = defaultStateId;
	}
	
	@JsonIgnore
	@JSON(serialize = false)
	public StateFlowRuleContext constructRule() {
		StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
		stateFlowRuleContext.setName(name + " _Rule");
		stateFlowRuleContext.setDescription(description);
		stateFlowRuleContext.setCriteria(criteria);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setActivityType(EventType.CREATE);
		event.setModuleId(moduleId);
		stateFlowRuleContext.setEvent(event);
		
		stateFlowRuleContext.setRuleType(RuleType.STATE_FLOW);
		
		stateFlowRuleContext.setId(getId());
		return stateFlowRuleContext;
	}
}
