package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardSharingContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long dashboardId = -1;
	public long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
	private long orgUserId = -1;
	public long getOrgUserId() {
		return orgUserId;
	}
	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
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
	private boolean locked = false;
	public boolean getLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	private SharingType sharingType;
	public int getSharingType() {
		if(sharingType != null) {
			return sharingType.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getSharingTypeVal() {
		if(sharingType != null) {
			return sharingType.getStringVal();
		}
		return null;
	}
	public void setSharingType(int type) {
		this.sharingType = SharingType.typeMap.get(type);
	}
	public void setSharingType(SharingType type) {
		this.sharingType = type;
	}
	public SharingType getSharingTypeEnum() {
		return sharingType;
	}
	
	public static enum SharingType {
		
		USER(1, "User"),
		ROLE(2, "Role"),
		GROUP(3, "Group"),
		PORTAL(4,"Portal"),
		ALL_PORTAL_USER(5, "All_Portal_User")
		;
		
		private int intVal;
		private String strVal;
		
		private SharingType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, SharingType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SharingType> initTypeMap() {
			Map<Integer, SharingType> typeMap = new HashMap<>();
			
			for(SharingType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SharingType> getAllTypes() {
			return typeMap;
		}
	}
}
