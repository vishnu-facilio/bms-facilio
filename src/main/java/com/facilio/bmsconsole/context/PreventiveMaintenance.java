package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.tasker.executor.ScheduleInfo;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PreventiveMaintenance {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public boolean isActive() {
		if(status != null) {
			return status.booleanValue();
		}
		return false;
	}
	
	private User createdBy;
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	private long createdById = -1;
	public long getCreatedById() {
		return createdById;
	}
	public void setCreatedById(long createdById) {
		this.createdById = createdById;
	}

	private User modifiedBy;
	public User getModifiedBy() {
		if(modifiedBy != null) {
			return modifiedBy;
		}
		else {
			return createdBy;
		}
	}
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	private long modifiedByid = -1;
	public long getModifiedByid() {
		return modifiedByid;
	}
	public void setModifiedByid(long modifiedByid) {
		this.modifiedByid = modifiedByid;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long lastModifiedTime = -1;
	public long getLastModifiedTime() {
		if(lastModifiedTime != -1) {
			return lastModifiedTime;
		}
		else {
			return createdTime;
		}
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	private long assignedToid = -1;
	public long getAssignedToid() {
		return assignedToid;
	}
	public void setAssignedToid(long assignedToid) {
		this.assignedToid = assignedToid;
	}
	
	private long assignmentGroupId = -1;
	public long getAssignmentGroupId() {
		return assignmentGroupId;
	}
	public void setAssignmentGroupId(long assignmentGroupId) {
		this.assignmentGroupId = assignmentGroupId;
	}

	private long typeId = -1;
	public long getTypeId() {
		return typeId;
	}
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long nextExecutionTime = -1;
	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime*1000;
	}

	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	@JsonSetter("endExecutionTime")
	public void setEndTime(long endTime) {
		this.endTime = endTime*1000;
	}

	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}
	
	public String getScheduleJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}
	public void setScheduleJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
	}
	
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	@JsonSetter("maxExecution")
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	public String getScheduleMsg() {
		if(schedule != null) {
			return schedule.getDescription(startTime);
		}
		return null;
	}
	
	private ReadingRuleContext readingRule;
	public ReadingRuleContext getReadingRule() {
		return readingRule;
	}
	public void setReadingRule(ReadingRuleContext readingRule) {
		this.readingRule = readingRule;
	}
	
	private long readingRuleId = -1;
	public long getReadingRuleId() {
		return readingRuleId;
	}
	public void setReadingRuleId(long readingRuleId) {
		this.readingRuleId = readingRuleId;
	}

	private List<WorkOrderContext> workorders;
	public List<WorkOrderContext> getWorkorders() {
		return workorders;
	}
	public void setWorkorders(List<WorkOrderContext> workorders) {
		this.workorders = workorders;
	}
}
