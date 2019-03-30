package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountUtil;
import com.opensymphony.xwork2.ActionSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetGroupMembersAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		List<GroupMember> users = AccountUtil.getGroupBean().getGroupMembers(getGroupId());
		members = new HashMap<>();
		
		if(users != null && users.size() > 0) {
			for(GroupMember user : users) {
				members.put(String.valueOf(user.getOuid()), user.getEmail());
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
