package com.facilio.bmsconsole.context;

import com.facilio.constants.FacilioConstants;

public class RoleContext {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long roleId = 0;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long permissions;
	public long getPermissions() {
		return permissions;
	}
	public void setPermissions(long permissions) {
		this.permissions = permissions;
	}
	
	public boolean hasPermission(long permission)
	{
		if(name.equals(FacilioConstants.Role.ADMINISTRATOR))
		{
			return true;
		}
		return (permissions & permission) == permission;
	}
	
	public boolean hasPermission(String permissionValue) throws Exception {
		
		boolean hasAccess = false;
		
		String[] permissionArray = permissionValue.split(",");
		
		for (String permission : permissionArray) {
			
			permission = permission.trim();

			FacilioConstants.Permission permType = null;
			FacilioConstants.PermissionGroup permGroupType = null;
			
			try {
				permType = FacilioConstants.Permission.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			try {
				permGroupType = FacilioConstants.PermissionGroup.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			
			if (permType != null) {
				hasAccess = hasPermission(permType);
			}
			else if (permGroupType != null) {
				FacilioConstants.Permission permissionList[] = permGroupType.getPermission();
				for (FacilioConstants.Permission perm : permissionList) {
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
	
	public boolean hasPermission(FacilioConstants.Permission permission)
	{
		if(permissions == 0)
		{
			return true;
		}
		return (permissions & permission.getPermission()) == permission.getPermission();
	}
}
