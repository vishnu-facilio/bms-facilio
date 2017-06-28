package com.facilio.bmsconsole.group.actions;

import java.util.Map;

import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class NewGroupAction extends ActionSupport {
	
	private Map<Long, String> userList;
	public Map<Long, String> getUserList() {
		return userList;
	}
	public void setUserList(Map<Long, String> userList) {
		this.userList = userList;
	}
	
	@Override
	public String execute() throws Exception {
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		userList = UserAPI.getOrgUsers(orgId);
		
		return SUCCESS;
	}
}
