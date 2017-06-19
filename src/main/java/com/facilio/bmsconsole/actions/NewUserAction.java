package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class NewUserAction extends ActionSupport {
	
	private Map<Integer, String> roles;
	public Map<Integer, String> getRoles() {
		return roles;
	}
	public void setRoles(Map<Integer, String> roles) {
		this.roles = roles;
	}
	
	@Override
	public String execute() throws Exception {
		
		roles = (HashMap<Integer, String>) FacilioConstants.Role.ALL_ROLES.clone();
		roles.remove(0);
		
		return SUCCESS;
	}
}
