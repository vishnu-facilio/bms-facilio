package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class EditUserAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		String userId = ServletActionContext.getRequest().getParameter("id");
		setUserId(Long.parseLong(userId));
		
		roles = (HashMap<Integer, String>) FacilioConstants.Role.ALL_ROLES.clone();
		roles.remove(0);
		
		Map<Integer, String> statusList =  new HashMap<Integer, String>();
		statusList.put(1, "Active");
		statusList.put(0, "Inactive");
		
		setStatusList(statusList);
		
		setUser(UserAPI.getUser(getUserId()));
		
		
		return SUCCESS;
	}
	
	private Map<Integer, String> roles;
	public Map<Integer, String> getRoles() {
		return roles;
	}
	public void setRoles(Map<Integer, String> roles) {
		this.roles = roles;
	}
	
	private Map<Integer, String> statusList;
	public Map<Integer, String> getStatusList() {
		return statusList;
	}
	public void setStatusList(Map<Integer, String> statusList) {
		this.statusList = statusList;
	}
	
	private UserContext user;
	public UserContext getUser() {
		return user;
	}
	public void setUser(UserContext user) {
		this.user = user;
	}
	
	
	private long userId;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
}
