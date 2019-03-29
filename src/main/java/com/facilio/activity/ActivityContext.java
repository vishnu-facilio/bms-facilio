package com.facilio.activity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActivityContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private ActivityType type;
	public ActivityType getTypeEnum() {
		return type;
	}
	public void setType(ActivityType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = ActivityType.getActivityType(type);
	}
	
	private User doneBy;
	public User getDoneBy() {
		return doneBy;
	}
	public void setDoneBy(User doneBy) {
		this.doneBy = doneBy;
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
		try {
			info = (JSONObject) parser.parse(infoStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(info);
	}
	
	@JsonIgnore
	public String getMessage() {
		if (this.type != null && info != null) {
			return type.constructMessage(info);
		}
		return null;
	}

}
