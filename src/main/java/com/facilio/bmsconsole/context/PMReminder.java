package com.facilio.bmsconsole.context;

import java.util.List;

public class PMReminder {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
			return type.getValue();
		}
		return -1;
	}
	public ReminderType getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = ReminderType.valueOf(type);
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
		
	private List<PMReminderAction> reminderActions;

	public List<PMReminderAction> getReminderActions() {
		return reminderActions;
	}
	public void setReminderActions(List<PMReminderAction> reminderActions) {
		this.reminderActions = reminderActions;
	}


	public static enum ReminderType {
		BEFORE_EXECUTION,
		AFTER_EXECUTION,
		BEFORE_DUE,
		AFTER_DUE
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ReminderType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
