package com.facilio.workflows.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ScheduledWorkflowContext {

	private long id = -1l;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1l;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	private WorkflowContext workflowContext;
	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}
	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}

	@Deprecated
	private long workflowId = -1l;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	private String timeZone = null;
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	private Boolean isActive;
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	private long startTime;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
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

	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	public void addAction(ActionContext action) {
		if (this.actions == null) {
			this.actions = new ArrayList<>();
		}
		this.actions.add(action);
	}

	@JsonIgnore
	@JSON(serialize = false)
	public ActionContext getScriptAction() {
		if (CollectionUtils.isNotEmpty(actions)) {
			for (ActionContext actionContext : actions) {
				if (actionContext.getActionTypeEnum() == ActionType.WORKFLOW_ACTION) {
					return actionContext;
				}
			}
		}
		return null;
	}
}
