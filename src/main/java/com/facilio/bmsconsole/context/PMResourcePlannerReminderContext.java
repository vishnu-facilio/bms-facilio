package com.facilio.bmsconsole.context;

public class PMResourcePlannerReminderContext {
	
	Long pmId;
	Long id;
	Long resourcePlannerId;
	Long reminderId;
	String reminderName;
	public String getReminderName() {
		return reminderName;
	}
	public void setReminderName(String reminderName) {
		this.reminderName = reminderName;
	}
	public Long getPmId() {
		return pmId;
	}
	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getResourcePlannerId() {
		return resourcePlannerId;
	}
	public void setResourcePlannerId(Long resourcePlannerId) {
		this.resourcePlannerId = resourcePlannerId;
	}
	public Long getReminderId() {
		return reminderId;
	}
	public void setReminderId(Long reminderId) {
		this.reminderId = reminderId;
	}
}
