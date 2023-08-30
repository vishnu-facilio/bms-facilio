package com.facilio.bmsconsole.templates;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.modules.FieldUtil;

public class TaskTemplate extends Template {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	
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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private boolean isPreRequest = false;

	public boolean isPreRequest() {
		return isPreRequest;
	}

	public void setPreRequest(boolean isPreRequest) {
		this.isPreRequest = isPreRequest;
	}
	public void setIsPreRequest(boolean isPreRequest) {
		this.isPreRequest = isPreRequest;
	}
	private TaskStatus status;
	public TaskStatus getStatusEnum() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = TaskStatus.valueOf(status);
	}
	
	private long priorityId = -1;
	public long getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(long priorityId) {
		this.priorityId = priorityId;
	}
	
	private long categoryId = -1;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
	private long typeId = -1;
	public long getTypeId() {
		return typeId;
	}
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}
	
	private long assignmentGroupId = -1;
	public long getAssignmentGroupId() {
		return assignmentGroupId;
	}
	public void setAssignmentGroupId(long assignmentGroupId) {
		this.assignmentGroupId = assignmentGroupId;
	}
	
	private long assignedToId = -1;
	public long getAssignedToId() {
		return assignedToId;
	}
	public void setAssignedToId(long assignedToId) {
		this.assignedToId = assignedToId;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long duration = -1;
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	private long parentTemplateId = -1;
	public long getParentTemplateId() {
		return parentTemplateId;
	}
	public void setParentTemplateId(long parentTemplateId) {
		this.parentTemplateId = parentTemplateId;
	}
	
	private long jobPlanId = -1;
	public long getJobPlanId() {
		return jobPlanId;
	}
	public void setJobPlanId(long jobPlanId) {
		this.jobPlanId = jobPlanId;
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
	
	private long sectionId = -1;
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
	
	private int sequence = -1;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	private AttachmentRequiredEnum attachmentRequiredEnum;
	public AttachmentRequiredEnum getAttachmentRequiredEnum() {
		return attachmentRequiredEnum;
	}
	
	public void setAttachmentRequiredInt(int attachmentRequired) {
		attachmentRequiredEnum = AttachmentRequiredEnum.valueOf(attachmentRequired);
	}
	public int getAttachmentRequiredInt() {
		if(attachmentRequiredEnum != null) {
			return attachmentRequiredEnum.getVal();
		}
		return -1;
	}
	
	public void setAttachmentRequired(boolean attachmentRequired) {
		if(attachmentRequired) {
			attachmentRequiredEnum = AttachmentRequiredEnum.TRUE;
		}
		else {
			attachmentRequiredEnum = AttachmentRequiredEnum.FALSE;
		}
	}
	
	public Boolean getAttachmentRequired() {
		if(attachmentRequiredEnum != null) {
			if(attachmentRequiredEnum.equals(AttachmentRequiredEnum.TRUE)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	public boolean isAttachmentRequired() {
		if(attachmentRequiredEnum != null) {
			if(attachmentRequiredEnum.equals(AttachmentRequiredEnum.TRUE)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	private JSONObject additionInfo;
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
	
	private long siteId = -1;
	
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
	
	public TaskContext getTask() {
		if (getName() != null && !getName().isEmpty()) {
			Map<String, Object> taskProp = new HashMap<>();
			
			taskProp.put("subject", getName());
			taskProp.put("description", description);
			taskProp.put("duration", duration);
			taskProp.put("readingFieldId", readingFieldId);
			taskProp.put("sequence", sequence);
			taskProp.put("attachmentRequired", isAttachmentRequired());
			taskProp.put("statusNew", getStatus());
			
			if (priorityId != -1) {
				taskProp.put("priority", FieldUtil.getEmptyLookedUpProp(priorityId));
			}
			if (categoryId != -1) {
				taskProp.put("category", FieldUtil.getEmptyLookedUpProp(categoryId));
			}
			if (typeId != -1) {
				taskProp.put("type", FieldUtil.getEmptyLookedUpProp(typeId));
			}
			if (assignmentGroupId != -1) {
				taskProp.put("assignmentGroup", FieldUtil.getEmptyLookedUpProp(assignmentGroupId));
			}
			if (assignedToId != -1) {
				taskProp.put("assignedTo", FieldUtil.getEmptyLookedUpProp(assignedToId));
			}
			if (resourceId != -1) {
				taskProp.put("resource", FieldUtil.getEmptyLookedUpProp(resourceId));
			}
			if (inputType != null) {
				taskProp.put("inputType", inputType.getVal());
			}
			if (siteId != -1) {
				taskProp.put("siteId", siteId);
			}
			
			taskProp.put("sectionId", sectionId);
			if (additionInfo != null) {
				taskProp.putAll(additionInfo);
			}
			taskProp.put("readingRules", readingRules);
			
			return FieldUtil.getAsBeanFromMap(taskProp, TaskContext.class);
		}
		return null;
	}

	public JobPlanTasksContext getAsJobPlanTask() {
		if (getName() != null && !getName().isEmpty()) {
			Map<String, Object> taskProp = new HashMap<>();

			taskProp.put("subject", getName());
			taskProp.put("description", description);
			taskProp.put("duration", duration);
			taskProp.put("readingFieldId", readingFieldId);
			taskProp.put("sequence", sequence);
			taskProp.put("attachmentRequired", isAttachmentRequired());
			taskProp.put("statusNew", getStatus());

			if (priorityId != -1) {
				taskProp.put("priority", FieldUtil.getEmptyLookedUpProp(priorityId));
			}
			if (categoryId != -1) {
				taskProp.put("category", FieldUtil.getEmptyLookedUpProp(categoryId));
			}
			if (typeId != -1) {
				taskProp.put("type", FieldUtil.getEmptyLookedUpProp(typeId));
			}
			if (assignmentGroupId != -1) {
				taskProp.put("assignmentGroup", FieldUtil.getEmptyLookedUpProp(assignmentGroupId));
			}
			if (assignedToId != -1) {
				taskProp.put("assignedTo", FieldUtil.getEmptyLookedUpProp(assignedToId));
			}
			if (resourceId != -1) {
				taskProp.put("resource", FieldUtil.getEmptyLookedUpProp(resourceId));
			}
			if (inputType != null) {
				taskProp.put("inputType", inputType.getVal());
			}
			if (siteId != -1) {
				taskProp.put("siteId", siteId);
			}

			taskProp.put("sectionId", sectionId);
			if (additionInfo != null) {
				taskProp.putAll(additionInfo);
			}
			taskProp.put("readingRules", readingRules);

			return FieldUtil.getAsBeanFromMap(taskProp, JobPlanTasksContext.class);
		}
		return null;
	}
	private List<ReadingRuleContext> readingRules;
	public List<ReadingRuleContext> getReadingRules() {
		return readingRules;
	}
	public void setReadingRules(List<ReadingRuleContext> readingRules) {
		this.readingRules = readingRules;
	}
	public void setTask(TaskContext task) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (task != null) {
			setName(task.getSubject());
			description = task.getDescription();
			inputType = task.getInputTypeEnum();
			readingFieldId = task.getReadingFieldId();
			sectionId = task.getSectionId();
			sequence = task.getSequence();
			setAttachmentRequired(task.isAttachmentRequired());
			status = task.getStatusNewEnum();
			if (task.getResource() != null) {
				resourceId = task.getResource().getId();
			}
			
			if (task.getSiteId() != -1) {
				siteId = task.getSiteId();
			}
			
			Map<String, Object> prop = FieldUtil.getAsProperties(task);
			prop.remove("id");
			prop.remove("subject");
			prop.remove("description");
			prop.remove("duration");
			prop.remove("status");
			prop.remove("priority");
			prop.remove("category");
			prop.remove("type");
			prop.remove("assignmentGroup");
			prop.remove("assignedTo");
			prop.remove("resource");
			prop.remove("inputType");
			prop.remove("readingFieldId");
			prop.remove("sectionId");
			prop.remove("sequence");
			prop.remove("attachmentRequired");
			prop.remove("siteId");
			additionInfo = new JSONObject();
			additionInfo.putAll(prop);
		}
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
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
	
	public enum AttachmentRequiredEnum {
		FALSE,
		TRUE,
		USE_PARENT,
		;
		
		public int getVal() {
			return ordinal();
		}
		
		public static AttachmentRequiredEnum valueOf(int val) {
			if(val > 0 && val <= values().length) {
				return values()[val];
			}
			return null;
		}
	}
	
}
