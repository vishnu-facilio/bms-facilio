package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DigestConfigContext {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(DigestConfigContext.class.getName());
	private Long scheduledActionId;
		public Long getScheduledActionId() {
		return scheduledActionId;
	}

	public void setScheduledActionId(Long scheduledActionId) {
		this.scheduledActionId = scheduledActionId;
	}

	private long orgId = -1;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long createdTime = -1l;
	private long modifiedTime = -1l;

	public long getModifiedTime() {
		return modifiedTime;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	private List<DigestUserContext> digestUsers;

	public List<DigestUserContext> getDigestUsers() {
		return digestUsers;
	}

	public void setDigestUsers(List<DigestUserContext> digestUsers) {
		this.digestUsers = digestUsers;
	}

	public Boolean isActive;

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean active) {
		this.isActive = active;
	}

	public boolean isActive() {
		if (isActive != null) {
			return isActive.booleanValue();
		}
		return false;
	}

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getScheduleJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}

	public void setScheduleJson(String jsonString)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		this.schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(jsonString), ScheduleInfo.class);
	}

	private ScheduleInfo schedule;

	public ScheduleInfo getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}
	
	private ScheduledActionContext scheduledAction;
	public ScheduledActionContext getScheduledAction() {
		return scheduledAction;
	}

	public void setScheduledAction(ScheduledActionContext scheduledAction) {
		this.scheduledAction = scheduledAction;
	}
	
	private Integer defaultTemplateId;
	public Integer getDefaultTemplateId() {
		return defaultTemplateId;
	}

	public void setDefaultTemplateId(Integer defaultTemplateId) {
		this.defaultTemplateId = defaultTemplateId;
	}
	
	private DigestMailTemplateMapContext mapContext;
	public DigestMailTemplateMapContext getMapContext() {
		return mapContext;
	}

	public void setMapContext(DigestMailTemplateMapContext mapContext) {
		this.mapContext = mapContext;
	}
	
	private DigestScope scope;
	public DigestScope getScopeEnum() {
		return scope;
	}
	public int getScope() {
		if (scope != null) {
			return scope.getValue();
		}
		return -1;
	}
	public void setScope(int scope) {
		this.scope = DigestScope.valueOf(scope);
	}
	public void setScope(DigestScope scope) {
		this.scope = scope;
	}
	
	public static enum DigestScope {
		ORG(),
		SITE()
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static DigestScope valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
 
	private long siteId;
	private long dashboardId;
	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
}
