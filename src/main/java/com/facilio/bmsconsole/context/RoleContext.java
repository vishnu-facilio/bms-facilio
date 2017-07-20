package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RoleContext extends FacilioContext {
	
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
	
	private String permissions;
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	
	public boolean hasPermission(int permission)
	{
		if(name.equals(FacilioConstants.Role.ADMINISTRATOR))
		{
			return true;
		}
		return (Integer.parseInt(permissions, 16) & permission) == permission;
	}
}
