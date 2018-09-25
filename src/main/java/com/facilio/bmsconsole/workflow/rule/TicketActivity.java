package com.facilio.bmsconsole.workflow.rule;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TicketActivity {
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

	private long ticketId = -1;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	private long modifiedBy = -1;
	public long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private ActivityType activityType;
	public int getActivityType() {
		if(activityType != null) {
			return activityType.getValue();
		}
		return -1;
	}
	public void setActivityType(int activityType) {
		this.activityType = ActivityType.valueOf(activityType);
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	public String getMessage() {
		if (this.activityType != null && info != null) {
			return activityType.getMessage(info);
		}
		return null;
	}
	
	private JSONObject info;
	public JSONObject getInfo() {
		return info;
	}
	public void setInfo(JSONObject info) {
		this.info = info;
	}
	
	public String getInfoJsonStr() {
		if(info != null) {
			return info.toJSONString();
		}
		return null;
	}
	public void setInfoJsonStr(String infoStr) throws ParseException {
		JSONParser parser = new JSONParser();
		info = (JSONObject) parser.parse(infoStr);
	}
}
