package com.facilio.bmsconsole.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.connected.ResourceType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.point.Point;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CommissioningLogContext {
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

	private long agentId = -1;
	public long getAgentId(){
		return agentId;
	}

	public void setAgentId(long agentId) {
		FacilioAgent agent = new FacilioAgent();
		this.agent = agent;
		agent.setId(agentId);
		this.agentId = agentId;
	}

	private String agentName;
	public String getAgentName() {
		return agentName;
	}

	private FacilioAgent agent;
	@JSON(serialize = false)
	public FacilioAgent getAgent() {
		return agent;
	}
	public void setAgent(FacilioAgent agent) {
		this.agent = agent;
		if (agent != null) {
			this.agentName = agent.getDisplayName();
			this.agentId = agent.getId();
		}
	}

	private long publishedTime = -1;
	public long getPublishedTime() {
		return publishedTime;
	}
	public void setPublishedTime(long publishedTime) {
		this.publishedTime = publishedTime;
	}

	public boolean isPrefillMlData() {
		return prefillMlData;
	}

	public void setPrefillMlData(boolean prefillMlData) {
		this.prefillMlData = prefillMlData;
	}

	private boolean prefillMlData;

	@JsonIgnore
	private List<Point> pointList;
	@JSON(serialize = false)
	public List<Point> getPointList() {
		return pointList;
	}
	@JSON(serialize = false)
	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}

	@JsonIgnore
	private JSONArray points;
	public JSONArray getPoints() {
		return points;
	}
	public void setPoints(JSONArray points) {
		this.points = points;
	}

	@JSON(serialize = false)
	public String getPointJsonStr() {
		if (points != null) {
			return points.toString();
		}
		return null;
	}
	@JSON(serialize = false)
	public void setPointJsonStr(String pointsStr) throws ParseException {
		if (StringUtils.isNotEmpty(pointsStr)) {
			this.points = FacilioUtil.parseJsonArray(pointsStr);
		}
	}

	private JSONObject clientMeta;
	@JsonIgnore
	public JSONObject getClientMeta() {
		return clientMeta;
	}
	@JsonIgnore
	public void setClientMeta(JSONObject clientMeta) {
		this.clientMeta = clientMeta;
	}

	@JSON(serialize = false)
	public String getClientMetaStr() {
		if (clientMeta != null) {
			return clientMeta.toJSONString();
		}
		return null;
	}
	@JSON(serialize = false)
	public void setClientMetaStr(String clientMetaStr) throws ParseException {
		if (clientMetaStr != null) {
			this.clientMeta = FacilioUtil.parseJson(clientMetaStr);
		}
	}

	private long sysCreatedTime = -1;
	public long getSysCreatedTime() {
		return sysCreatedTime;
	}
	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	private long sysModifiedTime = -1;
	public long getSysModifiedTime() {
		return sysModifiedTime;
	}
	public void setSysModifiedTime(long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}


	private long sysCreatedBy = -1;
	@JsonIgnore
	public long getSysCreatedBy() {
		return sysCreatedBy;
	}
	@JsonIgnore
	public void setSysCreatedBy(long sysCreatedBy) {
		this.sysCreatedBy = sysCreatedBy;
	}

	/*private String createdByName;
	public String getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}*/
	private long sysModifiedBy = -1;
	@JsonIgnore
	public long getSysModifiedBy() {
		return sysModifiedBy;
	}
	@JsonIgnore
	public void setSysModifiedBy(long sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}

	/*private long modifiedByName = -1;
	public long getModifiedByName() {
		return modifiedByName;
	}
	public void setModifiedByName(long modifiedByName) {
		this.modifiedByName = modifiedByName;
	}*/

	private List<Long> controllerIds;
	@JsonIgnore
	public List<Long> getControllerIds() {
		return controllerIds;
	}
	@JsonIgnore
	public void setControllerIds(List<Long> controllerIds) {
		this.controllerIds = controllerIds;
	}

	private List<Map<String, Object>> controllers;

	public List<Map<String, Object>> getControllers() {
		return controllers;
	}

	public void setControllers(List<Map<String, Object>> controllers) {
		this.controllers = controllers;
		if (controllers != null && controllerIds == null && !isLogical()) {
			this.controllerIds = controllers.stream().map(controller -> (long)controller.get("id")).collect(Collectors.toList());
		}
	}

	private List<Map<String, Object>> headers;
	public List<Map<String, Object>> getHeaders() {
		return headers;
	}
	public void setHeaders(List<Map<String, Object>> headers) {
		this.headers = headers;
	}

	private FacilioControllerType controllerType;
	public int getControllerType() {
		if(controllerType != null) {
			return controllerType.asInt();
		}
		else {
			return -1;
		}
	}
	public FacilioControllerType getControllerTypeEnum() {
		return controllerType;
	}
	public void setControllerType(int type) {
		this.controllerType = FacilioControllerType.valueOf(type);
	}

	private Boolean logical;
	public Boolean getLogical() {
		return logical;
	}
	public void setLogical(Boolean logical) {
		this.logical = logical;
	}
	public boolean isLogical() {
		return logical == null ? false : logical;
	}
	private long moduleId = -1L;
	public long getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	private ResourceType readingScope;
	public int getReadingScope() {
		if (readingScope != null) {
			return readingScope.getIndex();
		}
		return -1;
	}
	public ResourceType getReadingScopeEnum(){
		return readingScope;
	}
	public void setReadingScope(int readingScope) {
		this.readingScope = ResourceType.valueOf(readingScope);
	}
}
