package com.facilio.accounts.dto;

import com.facilio.accounts.util.AccountConstants.GroupMemberRole;

public class GroupMember extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long memberId;
	private long groupId;
	private long ouid;
	private int memberRole;
	
	public long getMemberId() {
		return memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public long getId() {
		return ouid;
	}
	public void setId(long id) {
		this.ouid = id;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public int getMemberRole() {
		return memberRole;
	}
	public GroupMemberRole getMemberRoleEnum() {
		return GroupMemberRole.getGroupMemberRole(getMemberRole());
	}
	public void setMemberRole(int memberRole) {
		this.memberRole = memberRole;
	}
	public void setMemberRole(GroupMemberRole memberRole) {
		this.memberRole = memberRole.getMemberRole();
	}
}
