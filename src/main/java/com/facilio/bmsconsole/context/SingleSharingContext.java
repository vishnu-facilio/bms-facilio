package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.GroupMember;

import java.util.List;

public class SingleSharingContext {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	private long fieldId = -1;

	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}


	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long userId = -1;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private long roleId = -1;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private long groupId = -1;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private List<GroupMember> groupMembers;
	public List<GroupMember> getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(List<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	private SharingType type;
	public SharingType getTypeEnum() {
		return type;
	}
	public void setType(SharingType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = SharingType.valueOf(type);
	}

	public static enum SharingType {
		USER,
		ROLE,
		GROUP
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static SharingType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("Parent ID : ")
									.append(parentId)
									.append("::Type : ")
									.append(type)
									.append("::");
		
		if (type != null) {
			switch (type) {
				case USER:
					builder.append(userId);
					break;
				case ROLE:
					builder.append(roleId);
					break;
				case GROUP:
					builder.append(groupId);
					break;
			}
		}
		
		return builder.toString();
	}
}
