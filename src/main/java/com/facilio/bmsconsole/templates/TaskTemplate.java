package com.facilio.bmsconsole.templates;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.modules.FieldUtil;

public class TaskTemplate extends Template {
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long statusId = -1;
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
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
		JSONParser parser = new JSONParser();
		additionInfo = (JSONObject) parser.parse(jsonStr);
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
			if (statusId != -1) {
				taskProp.put("status", FieldUtil.getLookedUpProp(statusId));
			}
			if (priorityId != -1) {
				taskProp.put("priority", FieldUtil.getLookedUpProp(priorityId));
			}
			if (categoryId != -1) {
				taskProp.put("category", FieldUtil.getLookedUpProp(categoryId));
			}
			if (typeId != -1) {
				taskProp.put("type", FieldUtil.getLookedUpProp(typeId));
			}
			if (assignmentGroupId != -1) {
				taskProp.put("assignmentGroup", FieldUtil.getLookedUpProp(assignmentGroupId));
			}
			if (assignedToId != -1) {
				taskProp.put("assignedTo", FieldUtil.getLookedUpProp(assignedToId));
			}
			if (resourceId != -1) {
				taskProp.put("resource", FieldUtil.getLookedUpProp(resourceId));
			}
			if (inputType != null) {
				taskProp.put("inputType", inputType.getVal());
			}
			taskProp.put("sectionId", sectionId);
			if (additionInfo != null) {
				taskProp.putAll(additionInfo);
			}
			
			return FieldUtil.getAsBeanFromMap(taskProp, TaskContext.class);
		}
		return null;
	}
	public void setTask(TaskContext task) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (task != null) {
			setName(task.getSubject());
			description = task.getDescription();
			duration = task.getDuration();
			inputType = task.getInputTypeEnum();
			readingFieldId = task.getReadingFieldId();
			sectionId = task.getSectionId();
			sequence = task.getSequence();
			attachmentRequired = task.isAttachmentRequired();
			if (task.getStatus() != null) {
				statusId = task.getStatus().getId();
			}
			if (task.getPriority() != null) {
				priorityId = task.getPriority().getId();
			}
			if (task.getCategory() != null) {
				categoryId = task.getCategory().getId();
			}
			if (task.getType() != null) {
				typeId = task.getType().getId();
			}
			if (task.getAssignmentGroup() != null) {
				assignmentGroupId = task.getAssignmentGroup().getId();
			}
			if (task.getAssignedTo() != null) {
				assignedToId = task.getAssignedTo().getId();
			}
			if (task.getResource() != null) {
				resourceId = task.getResource().getId();
			}
			
			Map<String, Object> prop = FieldUtil.getAsProperties(task);
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
			additionInfo = new JSONObject();
			additionInfo.putAll(prop);
		}
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

}
