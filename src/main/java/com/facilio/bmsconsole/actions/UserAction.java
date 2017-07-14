package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
	
	public String userList() throws Exception 
	{
		users = UserAPI.getUsersOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
		return SUCCESS;
	}
	
	private List<UserContext> users = null;
	public List<UserContext> getUsers() {
		return users;
	}
	public void setUsers(List<UserContext> users) {
		this.users = users;
	}
}