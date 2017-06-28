package com.facilio.bmsconsole.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class AddTaskAction extends ActionSupport {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		TaskContext context = new TaskContext();
		context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.setParent(getParent());
		context.setSubject(getSubject());
		context.setAssignmentGroupId(getAssignmentGroup());
		context.setAssignedToId(getAssignedTo());
		context.setDescription(getDescription());
		for(Map.Entry<String, String> entry : customFields.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		
		if(getScheduleStart() != 0) {
			ScheduleContext scheduleContext = new ScheduleContext();
			scheduleContext.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			scheduleContext.setScheduledStart(getScheduleStart());
			scheduleContext.setEstimatedEnd(getEstimatedEnd());
			scheduleContext.setActualWorkStart(getActualWorkStart());
			scheduleContext.setActualWorkEnd(getActualWorkEnd());
			
			context.setSchedule(scheduleContext);
		}
		
		Chain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);
		setTaskId(context.getTaskId());
		
		return SUCCESS;
	}
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
	private long parent;
	public long getParent() {
		return parent;
	}
	public void setParent(long parent) {
		this.parent = parent;
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
 	
 	private long assignmentGroup = 0;
	public long getAssignmentGroup() {
		return assignmentGroup;
	}
	public void setAssignmentGroup(long assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}
 	
 	private long assignedTo;
	public long getAssignedTo() {
		return assignedTo;
	}
 	public void setAssignedTo(long assignedTo) {
 		this.assignedTo = assignedTo;
 	}
	
	private Map<String, String> customFields = new HashMap<>();
 	public Map<String, String> getCustomFields() {
 		return customFields;
 	}
 	public String getCustomFields(String key) {
 		return customFields.get(key);
 	}
 	public void setCustomFields(String key, String value) {
 		customFields.put(key, value);
 	}
 	
 	public long scheduleStart = 0;
 	public long getScheduleStart() {
 		return scheduleStart;
 	}
 	public void setScheduleStart(String scheduleStart) {
 		if(scheduleStart != null && !scheduleStart.isEmpty()) {
 			try {
				this.scheduleStart = DATE_FORMAT.parse(scheduleStart).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
 	
 	public long estimatedEnd = 0;
 	public long getEstimatedEnd() {
 		return estimatedEnd;
 	}
 	public void setEstimatedEnd(String estimatedEnd) {
 		if(estimatedEnd != null && !estimatedEnd.isEmpty()) {
 			try {
				this.estimatedEnd = DATE_FORMAT.parse(estimatedEnd).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
 	
 	public long actualWorkStart = 0;
 	public long getActualWorkStart() {
 		return actualWorkStart;
 	}
 	public void setActualWorkStart(String actualWorkStart) {
 		if(actualWorkStart != null && !actualWorkStart.isEmpty()) {
 			try {
				this.actualWorkStart = DATE_FORMAT.parse(actualWorkStart).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
 	
 	public long actualWorkEnd = 0;
 	public long getActualWorkEnd() {
 		return actualWorkEnd;
 	}
 	public void setActualWorkEnd(String actualWorkEnd) {
 		if(actualWorkEnd != null && !actualWorkEnd.isEmpty()) {
 			try {
				this.actualWorkEnd = DATE_FORMAT.parse(actualWorkEnd).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
}
