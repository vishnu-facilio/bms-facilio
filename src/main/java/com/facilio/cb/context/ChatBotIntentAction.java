package com.facilio.cb.context;

import com.facilio.bmsconsole.workflow.rule.ActionContext;

public class ChatBotIntentAction {

	long id = -1;
	long orgId = -1;
	long intentId = -1;
	long actionId = -1;
	String response;
	
	ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
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
	public long getIntentId() {
		return intentId;
	}
	public void setIntentId(long intentId) {
		this.intentId = intentId;
	}
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
