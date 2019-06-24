package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class NotificationUserContext{
	
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
	private long configId;
	private User toUser;
	public long getConfigId() {
		return configId;
	}
	public void setConfigId(long configId) {
		this.configId = configId;
	}
	
	public long getToUserId(){
		if(toUser != null) {
			return toUser.getId();
		}
		return -1;
	}
	public void setToUserId(long ouId)  {
		this.toUser.setOuid(ouId);
	}
	public User getToUser() {
		return toUser;
	}
	public void setToUser(User toUser) {
		this.toUser = toUser;
	}
	
	

}
