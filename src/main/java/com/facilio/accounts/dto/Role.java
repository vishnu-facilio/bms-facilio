package com.facilio.accounts.dto;

import com.facilio.accounts.util.AccountConstants;

public class Role {

	private long roleId;
	private long orgId;
	private String name;
	private String description;
	private String permissionStr;

	public long getRoleId() {
		return roleId;
	}
	public long getId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
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
	public String getPermissionStr() {
		return permissionStr;
	}
	public long getPermissions() {
		if (permissionStr != null) {
			return Long.parseLong(permissionStr);
		}		
		return 0;
	}
	public void setPermissionStr(String permissionStr) {
		this.permissionStr = permissionStr;
	}
	public void setPermissions(long permissions) {
		this.permissionStr = permissions + "";
	}

	public boolean hasPermission(long permission) {
		if (getPermissions() == 0) {
			return true;
		}
		return (getPermissions() & permission) == permission;
	}

	public boolean hasPermission(AccountConstants.Permission permission) {
		return hasPermission(permission.getPermission());
	}

	public boolean hasPermission(String permissionValue) throws Exception {

		boolean hasAccess = false;
		String[] permissionArray = permissionValue.split(",");

		for (String permission : permissionArray) {

			permission = permission.trim();

			AccountConstants.Permission permType = null;
			AccountConstants.PermissionGroup permGroupType = null;

			try {
				permType = AccountConstants.Permission.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			try {
				permGroupType = AccountConstants.PermissionGroup.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}

			if (permType != null) {
				hasAccess = hasPermission(permType);
			}
			else if (permGroupType != null) {
				AccountConstants.Permission permissionList[] = permGroupType.getPermission();
				for (AccountConstants.Permission perm : permissionList) {
					hasAccess = hasPermission(perm);
					if (hasAccess) {
						break;
					}
				}
			}
			else {
				throw new Exception("Invalid permission type: "+permission);
			}

			if (hasAccess) {
				break;
			}
		}
		return hasAccess;
	}
}