package com.facilio.trigger.context;

import com.facilio.chain.FacilioContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;

import java.util.List;

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
	public void setActionType(TriggerActionType type) {
		this.actionType = type;
	}

	private long typeRefPrimaryId = -1;
	public long getTypeRefPrimaryId() {
		return typeRefPrimaryId;
	}
	public void setTypeRefPrimaryId(long typeRefPrimaryId) {
		this.typeRefPrimaryId = typeRefPrimaryId;
	}

	private long triggerId = -1;
	public long getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(long triggerId) {
		this.triggerId = triggerId;
	}

	private int executionOrder = -1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}

	public void execute(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets) throws Exception {
		actionType.performAction(context, trigger, moduleName, record, changeSets, typeRefPrimaryId);
	}
}
