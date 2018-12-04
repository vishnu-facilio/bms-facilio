package com.facilio.bmsconsole.templates;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;

public class TaskSectionTemplate extends Template {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;
	
	public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
		return pmIncludeExcludeResourceContexts;
	}
	public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
		this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
	}
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
	
	private InputType inputType;
	public int getInputType() {
		if(inputType != null) {
			return inputType.getVal();
		}
		return -1;
	}
	public void setInputType(int inputType) {
		this.inputType = InputType.valueOf(inputType);
	}
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}
	public InputType getInputTypeEnum() {
		return inputType;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingId) {
		this.readingFieldId = readingId;
	}
	
	private Boolean attachmentRequired;
	public Boolean getAttachmentRequired() {
		return attachmentRequired;
	}
	public void setAttachmentRequired(boolean attachmentRequired) {
		this.attachmentRequired = attachmentRequired;
	}
	public boolean isAttachmentRequired() {
		if(attachmentRequired != null) {
			return attachmentRequired.booleanValue();
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
		if(tasks == null && taskTemplates != null && !taskTemplates.isEmpty()) {
			for(TaskTemplate taskTemplate :taskTemplates) {
				addTasks(taskTemplate.getTask());
			}
		}
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	public void addTasks(TaskContext task) {
		
		this.tasks = this.tasks == null ? new ArrayList<>() : this.tasks;
		this.tasks.add(task);
	}
	
	private List<TaskTemplate> taskTemplates;
	
	public List<TaskTemplate> getTaskTemplates() {
		return taskTemplates;
	}
	public void setTaskTemplates(List<TaskTemplate> taskTemplates) {
		this.taskTemplates = taskTemplates;
	}
	public void addTaskTemplates(TaskTemplate taskTemplate) {
		this.taskTemplates = this.taskTemplates == null ? new ArrayList<>() : this.taskTemplates;  
		this.taskTemplates.add(taskTemplate);
	}
	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	Long assetCategoryId;
	Long spaceCategoryId;
	
	public Long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	public Long getSpaceCategoryId() {
		return spaceCategoryId;
	}
	public void setSpaceCategoryId(Long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
	}

	PMAssignmentType assignmentType;
	
	public int getAssignmentType() {
		if(assignmentType != null) {
			return assignmentType.getVal();
		}
		return -1;
	}
	public void setAssignmentType(int assignmentType) {
		this.assignmentType = PMAssignmentType.valueOf(assignmentType);
	}
	
	Long resourceId;
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}
