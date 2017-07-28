package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class RoleAction extends ActionSupport {
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<RoleContext> roles = null;
	public List<RoleContext> getRoles() {
		return roles;
	}
	
	public void setRoles(List<RoleContext> roles) {
		this.roles = roles;
	}
	
	public String roleList() throws Exception  {
		
		setSetup(SetupLayout.getRolesListLayout());
		setRoles(UserAPI.getRolesOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	private RoleContext role;
	public RoleContext getRole() {
		return role;
	}
	public void setRole(RoleContext role) {
		this.role = role;
	}
	
	private long roleId;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	public String newRole() {
		
		setSetup(SetupLayout.getNewRoleLayout());
		
		return SUCCESS;
	}
}