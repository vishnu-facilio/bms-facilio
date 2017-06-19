package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.opensymphony.xwork2.ActionSupport;

public class ViewUserAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		setUser(UserAPI.getUser(getUserId()));
		return SUCCESS;
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
