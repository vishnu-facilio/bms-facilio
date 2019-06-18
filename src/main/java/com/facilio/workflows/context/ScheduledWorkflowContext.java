package com.facilio.workflows.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ScheduledWorkflowContext {
	
	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}
	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}
	long id = -1l;
	long orgid = -1l;
	WorkflowContext workflowContext;
	long workflowId = -1l;
	String timeZone = null;
	Boolean isActive;
	long startTime;
	
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
	
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
}
