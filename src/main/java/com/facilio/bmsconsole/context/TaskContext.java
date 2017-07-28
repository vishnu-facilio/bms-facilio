package com.facilio.bmsconsole.context;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;

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
	
	private TaskStatusContext status;
	public TaskStatusContext getStatus() {
		return status;
	} 
	public void setStatus(TaskStatusContext status) {
		this.status = status;
	}
	
	public List<TaskStatusContext> getStatuses() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain statusListChain = FacilioChainFactory.getTaskStatusListChain();
		statusListChain.execute(context);
		
		return (List<TaskStatusContext>) context.get(FacilioConstants.ContextNames.TASK_STATUS_LIST);
	}
}
