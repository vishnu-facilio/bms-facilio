package com.facilio.bmsconsole.templates;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PMTaskSectionTemplateTriggers;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;

public class TaskSectionTemplate extends Template {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isPreRequestSection = false;

	public boolean isPreRequestSection() {
		return isPreRequestSection;
	}

	public void setPreRequestSection(boolean isPreRequestSection) {
		this.isPreRequestSection = isPreRequestSection;
	}
	List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;
	
	public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
		return pmIncludeExcludeResourceContexts;
	}
	public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
		this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
	}

	@Getter
	@Setter
	private int pmIncludeExcludeCount;
	
	private List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers;
	
	public List<PMTaskSectionTemplateTriggers> getPmTaskSectionTemplateTriggers() {
		return pmTaskSectionTemplateTriggers;
	}
	public void setPmTaskSectionTemplateTriggers(List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers) {
		this.pmTaskSectionTemplateTriggers = pmTaskSectionTemplateTriggers;
	}
	
	public void addPmTaskSectionTemplateTriggers(PMTaskSectionTemplateTriggers pmTaskSectionTemplateTrigger) {
		this.pmTaskSectionTemplateTriggers = this.pmTaskSectionTemplateTriggers == null ? new ArrayList<>() :this.pmTaskSectionTemplateTriggers;
		this.pmTaskSectionTemplateTriggers.add(pmTaskSectionTemplateTrigger);
	}
	
	public List<PMTriggerContext> getPmTriggerContexts() {
		return pmTriggerContexts;
	}
	public void setPmTriggerContexts(List<PMTriggerContext> pmTriggerContexts) {
		this.pmTriggerContexts = pmTriggerContexts;
	}
	
	public void addPmTriggerContext(PMTriggerContext pmTriggerContext) {
		this.pmTriggerContexts = pmTriggerContexts == null ? new ArrayList<>() : this.pmTriggerContexts;
		this.pmTriggerContexts.add(pmTriggerContext);
	}
	
	private List<PMTriggerContext> pmTriggerContexts;
	
	
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
	
	private long jobPlanId = -1;
	public long getJobPlanId() {
		return jobPlanId;
	}
	public void setJobPlanId(long jobPlanId) {
		this.jobPlanId = jobPlanId;
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
	
	private JSONObject additionInfo;
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
	
	public void addAdditionInfo(String key, Object value) {
		if(this.additionInfo == null) {
			this.additionInfo =  new JSONObject();
		}
		this.additionInfo.put(key,value);
	}
	
	public String getAdditionalInfoJsonStr() {
		if(additionInfo != null) {
			return additionInfo.toJSONString();
		}
		return null;
	}
	public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
		if(jsonStr != null) {
			JSONParser parser = new JSONParser();
			additionInfo = (JSONObject) parser.parse(jsonStr);
		}
	}
	public void setOptions(List<String> options) {
		if(options != null && !options.isEmpty()) {
			addAdditionInfo("options",options);
		}
	}
}
