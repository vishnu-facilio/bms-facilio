package com.facilio.bmsconsole.context;

import java.util.List;

public class PermissionGroup extends Permission {
	String displayName;
	List<Permission> permissions;

	public PermissionGroup(String displayName, List<Permission> permissions) {
		this.displayName = displayName;
		this.permissions = permissions;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public PermissionGroup(){}
}