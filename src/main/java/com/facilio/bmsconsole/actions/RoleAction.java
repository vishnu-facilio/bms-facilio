package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Command;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class RoleAction extends ActionSupport {
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<Role> roles = null;
	public List<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public String roleList() throws Exception  {
		
		setSetup(SetupLayout.getRolesListLayout());
		setRoles(AccountUtil.getRoleBean().getRoles(AccountUtil.getCurrentOrg().getOrgId()));
//	x	setGroups(AccountUtil.getGroupBean().getAllOrgGroups(AccountUtil.getCurrentOrg().getOrgId()));
		
		ActionContext.getContext().getValueStack().set("roles", getRoles());
		return SUCCESS;
	}
	
	private Role role;
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	private long roleId;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	public String addRole() throws Exception {
			// setting necessary fields
			role.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			FacilioContext context = new FacilioContext();
			role.setCreatedTime(System.currentTimeMillis());
			context.put(FacilioConstants.ContextNames.ROLE, getRole());
			context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());
			
			Command addRole = FacilioChainFactory.getAddRoleCommand();
			addRole.execute(context);
			setRoleId(role.getRoleId());
			
			return SUCCESS;
	}
	
	public String updateRole() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ROLE, getRole());
		context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());

		Command updateRole = FacilioChainFactory.getUpdateRoleCommand();
		updateRole.execute(context);
		
		return SUCCESS;
	}
	private List<Permissions> permissions;
	public List<Permissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}
}