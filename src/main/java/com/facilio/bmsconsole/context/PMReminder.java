package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.ActionContext;

public class PMReminder {
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
	
	private long pmId = -1;
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}
	
	private ReminderType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public ReminderType getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = REMINDER_TYPES[type - 1];
	}
	public void setType(ReminderType type) {
		this.type = type;
	}
	
	private long duration = -1; //In Seconds
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	private long actionId = -1;
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	
	private ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}

	private ReminderType[] REMINDER_TYPES = ReminderType.values();
	public static enum ReminderType {
		BEFORE,
		AFTER
		;
		
		public int getIntVal() {
			return ordinal()+1;
		}
	}
}
