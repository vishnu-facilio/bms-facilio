package com.facilio.trigger.context;

import com.facilio.chain.FacilioContext;

public class TriggerAction {

	long id = -1;
	long orgId = -1;
	String name;
	Trigger_Action_Type actionType;
	long typeRefPrimaryId = -1;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getActionType() {
		if(actionType != null) {
			return actionType.getVal();
		}
		return -1;
	}
	public Trigger_Action_Type getActionTypeEnum() {
		return actionType;
	}
	public void setActionType(int actionType) {
		this.actionType = Trigger_Action_Type.getActionType(actionType);
	}
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
