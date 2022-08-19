package com.facilio.accounts.dto;

import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import lombok.Getter;
import lombok.Setter;
@Deprecated
public class GroupMember extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	private long memberId;
	private long groupId;
	private long ouid;
	private int memberRole;
	@Getter@Setter
	private V3PeopleContext people;
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
