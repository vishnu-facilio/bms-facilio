package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TaskContext extends ModuleBaseWithCustomFields {
	
	private long parentId = 0;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private String parentModuleLinkName;
	public String getParentModuleLinkName() {
		return parentModuleLinkName;
	}
	public void setParentModuleLinkName(String parentModuleLinkName) {
		this.parentModuleLinkName = parentModuleLinkName;
	}
	
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private GroupContext assignmentGroup;
	public GroupContext getAssignmentGroup() {
		return assignmentGroup;
	}
	public void setAssignmentGroup(GroupContext assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}
	
	private UserContext assignedTo;
	public UserContext getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(UserContext assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	private long scheduleId = 0;
	public long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	private ScheduleContext schedule;
	public ScheduleContext getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleContext schedule) {
		this.schedule = schedule;
	}
	
	private TicketStatusContext status;
	public TicketStatusContext getStatus() {
		return status;
	} 
	public void setStatus(TicketStatusContext status) {
		this.status = status;
	}
	
}
