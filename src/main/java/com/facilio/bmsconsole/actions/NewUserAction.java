package com.facilio.bmsconsole.actions;

import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

public class NewUserAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Long, String> roles;
	public Map<Long, String> getRoles() {
		return roles;
	}
	public void setRoles(Map<Long, String> roles) {
		this.roles = roles;
	}
	
	@Override
	public String execute() throws Exception {
		
//		roles = UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return SUCCESS;
	}
}
