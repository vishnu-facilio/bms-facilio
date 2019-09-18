package com.facilio.bmsconsole.workflow.rule;

import java.util.List;

import com.facilio.bmsconsole.context.SingleSharingContext;

public class ApproverContext extends SingleSharingContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
}
