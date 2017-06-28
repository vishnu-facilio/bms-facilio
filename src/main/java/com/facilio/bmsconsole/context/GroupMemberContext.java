package com.facilio.bmsconsole.context;

public class GroupMemberContext extends UserContext {

	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private long memberId;
	public long getMemberId() {
		return memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	
	private int memberRole;
	public int getMemberRole() {
		return memberRole;
	}
	public String getMemberRoleAsString() {
		switch (memberRole) {
		case 1:
			return "Member";
		case 2:
			return "Admin";
		default:
			return "Member";
		}
	}
	public void setMemberRole(int memberRole) {
		this.memberRole = memberRole;
	}
}
