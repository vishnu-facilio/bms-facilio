package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TaskContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_TASK_FIELDS = new String[] {"TASKID", "SUBJECT", "DESCRIPTION", "ASSIGNMENT_GROUP_ID", "ASSIGNED_TO_ID", "SCHEDULE_ID"};
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
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
	
	private long assignmentGroupId = 0;
	public long getAssignmentGroupId() {
		return assignmentGroupId;
	}
	public void setAssignmentGroupId(long assignmentGroupId) {
		this.assignmentGroupId = assignmentGroupId;
	}
	
	private long assignedToId = 0;
	public long getAssignedToId() {
		return assignedToId;
	}
	public void setAssignedToId(long assignedToId) {
		this.assignedToId = assignedToId;
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
	
	private int statusCode = 1;
	public int getStatusCode() {
		return statusCode;
	} 
	public void setStatusCode(int status) {
		this.statusCode = status;
	}
	
}
