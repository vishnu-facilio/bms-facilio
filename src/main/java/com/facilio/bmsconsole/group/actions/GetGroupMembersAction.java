package com.facilio.bmsconsole.group.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.GroupMemberContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.opensymphony.xwork2.ActionSupport;

public class GetGroupMembersAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		List<GroupMemberContext> users = GroupAPI.getGroupMembers(groupId);
		members = new HashMap<>();
		
		if(users != null && users.size() > 0) {
			for(GroupMemberContext user : users) {
				members.put(String.valueOf(user.getOrgUserId()), user.getEmail());
			}
		}
		
		return SUCCESS;
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private Map<String, String> members;
	public Map<String, String> getMembers() {
		return members;
	}
	public void setMembers(Map<String, String> members) {
		this.members = members;
	}

}
