package com.facilio.bmsconsole.templates;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PrerequisiteApproversContext;
import com.facilio.modules.FieldUtil;

public class PrerequisiteApproversTemplate extends Template {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
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
	
	private SharingType sharingType;
	public SharingType getSharingTypeEnum() {
		return sharingType;
	}
	public void setSharingType(SharingType sharingType) {
		this.sharingType = sharingType;
	}
	public int getSharingType() {
		if (sharingType != null) {
			return sharingType.getValue();
		}
		return -1;
	}
	public void setSharingType(int type) {
		this.sharingType = SharingType.valueOf(type);
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
	
	public PrerequisiteApproversContext getPrerequisiteApprover() {
		Map<String, Object> approverProp = new HashMap<>();
		if (sharingType != null) {
			approverProp.put("userId", userId);
			approverProp.put("roleId", roleId);
			approverProp.put("groupId", groupId);
			approverProp.put("sharingType", getSharingType());
			return FieldUtil.getAsBeanFromMap(approverProp, PrerequisiteApproversContext.class);
		}
		return null;
	}
	
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
