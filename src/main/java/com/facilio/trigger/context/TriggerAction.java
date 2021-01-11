package com.facilio.trigger.context;

import com.facilio.chain.FacilioContext;

public class TriggerAction {

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

	private TriggerActionType actionType;
	public int getActionType() {
		if(actionType != null) {
			return actionType.getVal();
		}
		return -1;
	}
	public TriggerActionType getActionTypeEnum() {
		return actionType;
	}
	public void setActionType(int actionType) {
		this.actionType = TriggerActionType.getActionType(actionType);
	}

	private long typeRefPrimaryId = -1;
	public long getTypeRefPrimaryId() {
		return typeRefPrimaryId;
	}
	public void setTypeRefPrimaryId(long typeRefPrimaryId) {
		this.typeRefPrimaryId = typeRefPrimaryId;
	}
	
	public Object execute(FacilioContext context) throws Exception {
		return actionType.performAction(context, typeRefPrimaryId);
	}
}
