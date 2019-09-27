package com.facilio.conditionalformatting.context;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ConditionalFormattingContext {

	List<String> applyToFields;
		
	Criteria criteria;
	
	WorkflowContext workflow;
	
	JSONObject actions;
	
	public List<String> getApplyToFields() {
		return applyToFields;
	}
	public void setApplyToFields(List<String> applyToFields) {
		this.applyToFields = applyToFields;
	}
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	public JSONObject getActions() {
		return actions;
	}
	public void setActions(JSONObject actions) {
		this.actions = actions;
	}
	
	public boolean evaluateWorkflowExpression (Map<String, Object> record, FacilioContext context) throws Exception {
		
		boolean workflowFlag = true;
		if (workflow != null) {
			workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(workflow, record);
		}
		return workflowFlag;
	}
	
	public boolean evaluateCriteria (Map<String, Object> record, FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		if(criteria != null && record != null) {
			criteriaFlag = criteria.computePredicate(record).evaluate(record);
		}
		return criteriaFlag;
	}
}
