package com.facilio.bmsconsole.group.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.GroupMemberContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.opensymphony.xwork2.ActionSupport;

public class ViewGroupAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		setGroup(GroupAPI.getGroup(getGroupId()));
		setMembers(GroupAPI.getGroupMembers(getGroupId()));
		return SUCCESS;
	}
	
	private GroupContext group;
	public GroupContext getGroup() {
		return group;
	}
	public void setGroup(GroupContext group) {
		this.group = group;
	}
	
	private List<GroupMemberContext> members;
	public List<GroupMemberContext> getMembers() {
		return members;
	}
	public void setMembers(List<GroupMemberContext> members) {
		this.members = members;
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
}
