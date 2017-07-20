package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class RoleAction extends ActionSupport {
	
	public String roleList() throws Exception 
	{
		roles = UserAPI.getRolesOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
		return SUCCESS;
	}
	
	private List<RoleContext> roles = null;
	public List<RoleContext> getRoles() {
		return roles;
	}
	public void setRoles(List<RoleContext> roles) {
		this.roles = roles;
	}
}