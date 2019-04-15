package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.SingleSharingContext;

import java.util.List;

public class ApproverContext extends SingleSharingContext {
	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
}
