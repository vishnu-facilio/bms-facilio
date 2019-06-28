package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;

public class MultiRuleContext {

	private WorkflowRuleContext rule;
	private List<ActionContext> actions;
	public WorkflowRuleContext getRule() {
		return rule;
	}
	public void setRule(WorkflowRuleContext rule) {
		this.rule = rule;
	}
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	
}
