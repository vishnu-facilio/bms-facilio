package com.facilio.accounts.dto;

import java.io.Serializable;

import com.facilio.accounts.util.AccountConstants;

public class Permissions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Permissions() {
		super();
	}
	private long roleId;
	private String moduleName;
	private long permission;
	
	public Permissions(String moduleName, long permission) {
		this.moduleName = moduleName;
		this.permission = permission;
	}
	
	public long getPermission() {
		return permission;
	}
	public void setPermission(long permission) {
		this.permission = permission;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public boolean hasPermission(long permission) {
		if (getPermission() == 0) {
			return true;
		}
		return (getPermission() & permission) == permission;
	}

	public boolean hasPermission(AccountConstants.ModulePermission permission) {
		return hasPermission(permission.getModulePermission());
	}

	public boolean hasPermission(String permissionValue) throws Exception {

		boolean hasAccess = false;
		String[] permissionArray = permissionValue.split(",");

		for (String permission : permissionArray) {

			permission = permission.trim();

			AccountConstants.ModulePermission permType = null;
			try {
				permType = AccountConstants.ModulePermission.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			if (permType != null) {
				hasAccess = hasPermission(permType);
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
