package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AssignmentTemplate extends Template {

	
	long assignedGroupId = -1;
	long assignedUserId = -1;
	
	public long getAssignedGroupId() {
		return assignedGroupId;
	}
	public void setAssignedGroupId(long assignedGroupId) {
		this.assignedGroupId = assignedGroupId;
	}
	public long getAssignedUserId() {
		return assignedUserId;
	}
	public void setAssignedUserId(long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	Group assignedGroup;
	User assignedUser;
	
	public Group getAssignedGroup() {
		return assignedGroup;
	}
	public void setAssignedGroup(Group assignedGroup) {
		this.assignedGroup = assignedGroup;
	}
	public User getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.ASSIGNMENT.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.ASSIGNMENT;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("assignedUserId", assignedUserId);
		json.put("assignedGroupId", assignedGroupId);
		return json;
	}
}
