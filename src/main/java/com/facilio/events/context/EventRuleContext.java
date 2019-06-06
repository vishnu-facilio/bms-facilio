package com.facilio.events.context;

import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.db.criteria.Criteria;
import com.facilio.workflows.context.WorkflowContext;
import net.minidev.json.JSONObject;

public class EventRuleContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
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

	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	private int executionOrder = -1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	private SuccessAction successAction;
	public int getSuccessAction() {
		if (successAction != null) {
			return successAction.getValue();
		}
		return -1;
	}
	public void setSuccessAction(int successAction) {
		this.successAction = SuccessAction.valueOf(successAction);
	}
	public SuccessAction getSuccessActionEnum() {
		return successAction;
	}
	public void setSuccessAction(SuccessAction successAction) {
		this.successAction = successAction;
	}
	
	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}
	
	private long transformTemplateId = -1;
	public long getTransformTemplateId() {
		return transformTemplateId;
	}
	public void setTransformTemplateId(long transformTemplateId) {
		this.transformTemplateId = transformTemplateId;
	}
	
	private JSONObject transformJson;
	public JSONObject getTransformJson() {
		return transformJson;
	}
	public void setTransformJson(JSONObject transformJson) {
		this.transformJson = transformJson;
	}

	private JSONTemplate transformTemplate;
	public JSONTemplate getTransformTemplate() {
		return transformTemplate;
	}
	public void setTransformTemplate(JSONTemplate transformTemplate) {
		this.transformTemplate = transformTemplate;
	}

	public static enum SuccessAction {
		IGNORE,
		TRANSFORM
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static SuccessAction valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
