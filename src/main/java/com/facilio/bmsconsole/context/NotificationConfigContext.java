package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class NotificationConfigContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long parentId;
	private String configModuleName;
	private long actionId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getConfigModuleName() {
		return configModuleName;
	}
	public void setConfigModuleName(String configModuleName) {
		this.configModuleName = configModuleName;
	}
	
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	
	private List<NotificationUserContext> notificationUsers;
	public List<NotificationUserContext> getNotificationUsers() {
		return notificationUsers;
	}
	public void setNotificationUsers(List<NotificationUserContext> notificationUsers) {
		this.notificationUsers = notificationUsers;
	}
	
	private ActionContext actionContext;
	public ActionContext getActionContext() {
		return actionContext;
	}
	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
	
	private Mode scheduleMode;
	public Mode getScheduleModeEnum() {
		return scheduleMode;
	}
	public int getScheduleMode() {
		if (scheduleMode != null) {
			return scheduleMode.getValue();
		}
		return -1;
	}
	public void setScheduleMode(int mode) {
		this.scheduleMode = Mode.valueOf(mode);
	}
	public void setScheduleMode(Mode mode) {
		this.scheduleMode = mode;
	}
	
	public static enum Mode {
		ONCE(),
		PERIODIC()
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Mode valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
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
	
	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
}
