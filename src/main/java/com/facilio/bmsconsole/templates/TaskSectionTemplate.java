package com.facilio.bmsconsole.templates;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.TaskContext;

public class TaskSectionTemplate extends Template {
	
	private Boolean isEditable;
	public Boolean getIsEditable() {
		return isEditable;
	}
	public void setIsEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	public boolean isEditable() {
		if (isEditable != null) {
			return isEditable.booleanValue();
		}
		return false;
	}
	
	private long parentWOTemplateId = -1;
	public long getParentWOTemplateId() {
		return parentWOTemplateId;
	}
	public void setParentWOTemplateId(long parentWOTemplateId) {
		this.parentWOTemplateId = parentWOTemplateId;
	}

	private long sequenceNumber = -1;
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}

	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

}
