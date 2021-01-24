package com.facilio.bmsconsoleV3.context;

import java.util.List;

import com.facilio.v3.context.V3Context;

public class GroupInviteContextV3 extends V3Context {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	List<InviteVisitorContextV3> groupChildInvites;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<InviteVisitorContextV3> getGroupChildInvites() {
		return groupChildInvites;
	}
	public void setGroupChildInvites(List<InviteVisitorContextV3> groupChildInvites) {
		this.groupChildInvites = groupChildInvites;
	}
}
