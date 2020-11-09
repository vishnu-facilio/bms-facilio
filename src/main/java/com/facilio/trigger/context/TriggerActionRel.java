package com.facilio.trigger.context;

import com.facilio.accounts.util.AccountUtil;

public class TriggerActionRel {
	
	public TriggerActionRel() {
		
	}
	
	public TriggerActionRel(long triggerId,long triggerActionId) {
		this.orgId = AccountUtil.getCurrentOrg().getId();
		this.triggerId = triggerId;
		this.triggerActionId = triggerActionId;
	}

	long id = -1;
	long orgId = -1;
	long triggerId = -1;
	long triggerActionId = -1;
	int executionOrder = -1;
	
	public int getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
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
	public long getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(long triggerId) {
		this.triggerId = triggerId;
	}
	public long getTriggerActionId() {
		return triggerActionId;
	}
	public void setTriggerActionId(long triggerActionId) {
		this.triggerActionId = triggerActionId;
	}
}
