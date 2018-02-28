package com.facilio.bmsconsole.templates;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;

public class WorkorderTemplate extends Template {
	
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

	public WorkOrderContext getWorkorder() {
		if (getName() != null && !getName().isEmpty()) {
			Map<String, Object> woProp = new HashMap<>();
			
			woProp.put("subject", getName());
			woProp.put("description", description);
			woProp.put("duration", duration);
			if (statusId != -1) {
				woProp.put("status", FieldUtil.getLookedUpProp(statusId));
			}
			if (priorityId != -1) {
				woProp.put("priority", FieldUtil.getLookedUpProp(priorityId));
			}
			if (categoryId != -1) {
				woProp.put("category", FieldUtil.getLookedUpProp(categoryId));
			}
			if (typeId != -1) {
				woProp.put("type", FieldUtil.getLookedUpProp(typeId));
			}
			if (assignmentGroupId != -1) {
				woProp.put("assignmentGroup", FieldUtil.getLookedUpProp(assignmentGroupId));
			}
			if (assignedToId != -1) {
				woProp.put("assignedTo", FieldUtil.getLookedUpProp(assignedToId));
			}
			if (resourceId != -1) {
				woProp.put("resource", FieldUtil.getLookedUpProp(resourceId));
			}
			
			if (additionInfo != null) {
				woProp.putAll(additionInfo);
			}
			
			return FieldUtil.getAsBeanFromMap(woProp, WorkOrderContext.class);
		}
		return null;
	}
	
	public void setWorkorder(WorkOrderContext workorder) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (workorder != null) {
			setName(workorder.getSubject());
			description = workorder.getDescription();
			duration = workorder.getDuration();
			if (workorder.getStatus() != null) {
				statusId = workorder.getStatus().getId();
			}
			if (workorder.getPriority() != null) {
				priorityId = workorder.getPriority().getId();
			}
			if (workorder.getCategory() != null) {
				categoryId = workorder.getCategory().getId();
			}
			if (workorder.getType() != null) {
				typeId = workorder.getType().getId();
			}
			if (workorder.getAssignmentGroup() != null) {
				assignmentGroupId = workorder.getAssignmentGroup().getId();
			}
			if (workorder.getAssignedTo() != null) {
				assignedToId = workorder.getAssignedTo().getId();
			}
			if (workorder.getResource() != null) {
				resourceId = workorder.getResource().getId();
			}
			
			Map<String, Object> prop = FieldUtil.getAsProperties(workorder);
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
			additionInfo = new JSONObject();
			additionInfo.putAll(prop);
		}
	}
	
	private Map<String, List<TaskContext>> tasks;
	public Map<String, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, List<TaskContext>> tasks) {
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
