package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.rule.ActionContext;

public class PMReminderAction {
	
	long id = -1;
	long orgId = -1;
	long reminderId = -1;
	long actionId = -1;
	
	ActionContext action;
	
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
	public long getReminderId() {
		return reminderId;
	}
	public void setReminderId(long reminderId) {
		this.reminderId = reminderId;
	}
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}
}
