package com.facilio.bmsconsole.forms;

import java.util.List;

import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.workflows.context.WorkflowContext;

public class FormRuleActionContext {
	long id = -1;
	long orgId = -1;
	long formRuleId = -1;
	FormActionType actionType;
	FormRuleContext ruleContext;
	long workflowId = -1;
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

	public FormRuleContext getRuleContext() {
		return ruleContext;
	}

	public void setRuleContext(FormRuleContext ruleContext) {
		this.ruleContext = ruleContext;
	}
	List<FormRuleActionFieldsContext> formRuleActionFieldsContext;
	
	public List<FormRuleActionFieldsContext> getFormRuleActionFieldsContext() {
		return formRuleActionFieldsContext;
	}

	public void setFormRuleActionFieldsContext(List<FormRuleActionFieldsContext> formRuleActionFieldsContext) {
		this.formRuleActionFieldsContext = formRuleActionFieldsContext;
	}

	public void executeAction(FacilioContext facilioContext) throws Exception {
		facilioContext.put(FormRuleAPI.FORM_RULE_ACTION_CONTEXT, this);
		actionType.performAction(facilioContext);
	}
	
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
	public long getFormRuleId() {
		return formRuleId;
	}
	public void setFormRuleId(long formRuleId) {
		this.formRuleId = formRuleId;
	}
	public FormActionType getActionTypeEnum() {
		return actionType;
	}
	public int getActionType() {
		if(actionType != null) {
			return actionType.getVal();
		}
		return -1;
	}
	public void setActionType(int actionType) {
		this.actionType = FormActionType.getActionType(actionType);
	}
}
