package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		setRoles(AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRoles());
//	x	setGroups(AccountUtil.getGroupBean().getAllOrgGroups(AccountUtil.getCurrentOrg().getOrgId()));
		
		ActionContext.getContext().getValueStack().set("roles", getRoles());
		return SUCCESS;
	}
	private Map roleresponse = new HashMap();
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
			if (role.getName() == null)
			{
				setRoleResponse("message","Role Name Cannot be Null");
				return ERROR;
			}
 			FacilioContext context = new FacilioContext();
			role.setCreatedTime(System.currentTimeMillis());
			context.put(FacilioConstants.ContextNames.ROLE, getRole());
			context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());
			
			Command addRole = FacilioChainFactory.getAddRoleCommand();
			addRole.execute(context);
			setRoleId(role.getRoleId());
			
			return SUCCESS;
	}
	
	  public Map<String, Object> getRoleResponse() {
	        return roleresponse;
	    }
	  public void setRoleResponse(String key, Object roleresponse) {
	        this.roleresponse.put(key, roleresponse);
	    }
		public String deleteRole() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ROLE_ID, getRoleId());

		Command deleteRole = FacilioChainFactory.getDeleteRoleCommand();
		deleteRole.execute(context);
		
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