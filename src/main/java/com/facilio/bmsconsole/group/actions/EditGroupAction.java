package com.facilio.bmsconsole.group.actions;

import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class EditGroupAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		String groupId = ServletActionContext.getRequest().getParameter("id");
		setGroupId(Long.parseLong(groupId));
		setGroup(GroupAPI.getGroup(getGroupId()));
		
		setUserList(UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid()));
		setSelectedUserList(GroupAPI.getGroupMembersMap(getGroupId()));
		
		return SUCCESS;
	}
	
	private Map<Long, String> userList;
	public Map<Long, String> getUserList() {
		return userList;
	}
	public void setUserList(Map<Long, String> userList) {
		this.userList = userList;
	}
	
	private Map<Long, String> selectedUserList;
	public Map<Long, String> getSelectedUserList() {
		return selectedUserList;
	}
	public void setSelectedUserList(Map<Long, String> selectedUserList) {
		this.selectedUserList = selectedUserList;
	}
	
	private GroupContext group;
	public GroupContext getGroup() {
		return group;
	}
	public void setGroup(GroupContext group) {
		this.group = group;
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
}
