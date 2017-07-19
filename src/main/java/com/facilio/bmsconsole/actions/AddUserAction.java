package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Command;

import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class AddUserAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		String actionName = ActionContext.getContext().getName();
		
		UserContext context = new UserContext();
		
		Command cmd = null;
		if ("save".equalsIgnoreCase(actionName)) {
			cmd = FacilioChainFactory.getAddUserCommand();
			
			context.setName(getName());
			context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			context.setEmail(getEmail());
			context.setInviteAcceptStatus(true);
			context.setInvitedTime(System.currentTimeMillis());
			context.setRole(getRole());
			context.setPassword(getPassword());
		}
		else if ("update".equalsIgnoreCase(actionName)) {
			cmd = FacilioChainFactory.getUpdateUserCommand();
			
			context = UserAPI.getUser(getUserId());
			context.setInviteAcceptStatus(getStatus());
			context.setRole(getRole());
		}
		else if ("resetpassword".equalsIgnoreCase(actionName)) {
			
		}
		else if ("delete".equalsIgnoreCase(actionName)) {
			
		}
		
		try {
			cmd.execute(context);
			System.out.println("User ID : "+context.getUserId());
			setUserId(context.getUserId());
			return SUCCESS;
		}
		catch (Exception e) {
			if (e instanceof UsernameExistsException) {
				
				if (UserAPI.getUser(getEmail()) != null && (OrgInfo.getCurrentOrgInfo().getOrgid() == UserAPI.getUser(getEmail()).getOrgId())) {
					addFieldError("email", "This user already exists in your organization.");
				}
				else {
					addFieldError("email", "This user already exists in other organization.");
				}
			}
			return ERROR;
		}
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private String password;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	private String role;
	public String getRole() {
		return role;
	}
 	public void setRole(String role) {
 		this.role = role;
 	}
 	
 	private boolean status;
 	public boolean getStatus() {
		return status;
	}
 	public void setStatus(boolean status) {
 		this.status = status;
 	}
 	
 	private long userId;
 	public long getUserId() {
 		return userId;
 	}
 	public void setUserId(long userId) {
 		this.userId = userId;
 	}
}
