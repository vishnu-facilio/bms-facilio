package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class NewUserAction extends ActionSupport {
	
	private List<String> roles;
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public String execute() throws Exception {
		
		roles = (List<String>) FacilioConstants.Role.ALL_ROLES;
		roles.remove(0);
		
		return SUCCESS;
	}
}
