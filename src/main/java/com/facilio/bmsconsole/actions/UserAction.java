package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;

import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.GroupMemberContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<UserContext> users = null;
	public List<UserContext> getUsers() {
		return users;
	}
	
	public void setUsers(List<UserContext> users) {
		this.users = users;
	}
	
	private long groupId = -1;
	public long getGroupId() {
		return this.groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String userPickList() throws Exception {
		
		if (getGroupId() > 0) {
			List<GroupMemberContext> memberList = (List<GroupMemberContext>) GroupAPI.getGroupMembers(getGroupId());
			this.users = new ArrayList<>();
			if (memberList != null) {
				for (GroupMemberContext member : memberList) {
					this.users.add(member);
				}
			}
		}
		else {
			setUsers(UserAPI.getUsersOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid()));
		}
		return SUCCESS;
	}
	
	public String userList() throws Exception 
	{
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(UserAPI.getUsersOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	private List<String> roles;
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public String newUser() throws Exception {
		
		setSetup(SetupLayout.getNewUserLayout());
		setRoles(FacilioConstants.Role.ALL_ROLES);
		
		return SUCCESS;
	}
	
	public String addUser() throws Exception {
		
		// setting necessary fields
		user.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		user.setPassword("Test1@34");
		long roleId = UserAPI.getRole(user.getRole().getName()).getRoleId();
		user.setRoleId(roleId);
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		
		try {
			Command addUser = FacilioChainFactory.getAddUserCommand();
			addUser.execute(context);
			setUserId(user.getUserId());
		}
		catch (Exception e) {
			if (e instanceof UsernameExistsException) {
				if (UserAPI.getUser(user.getEmail()).getOrgId() == OrgInfo.getCurrentOrgInfo().getOrgid()) {
					addFieldError("email", "This user already exists in your organization.");
				}
				else {
					addFieldError("email", "This user already exists in other organization.");
				}
			}
			else if (e instanceof InvalidParameterException) {
				addFieldError("phone", "Invalid phone number format.");
			}
			return ERROR;
		}
		return SUCCESS;
	}
	
	private UserContext user;
	public UserContext getUser() {
		return user;
	}
	public void setUser(UserContext user) {
		this.user = user;
	}
	
	private long userId;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String editUser() throws Exception {
				
		setSetup(SetupLayout.getEditUserLayout());
		setUser(UserAPI.getUserFromOrgUserId(getUserId()));
		setRoles(FacilioConstants.Role.ALL_ROLES);
		
		return SUCCESS;
	}
	
	public String updateUser() throws Exception {
		
		user.setRoleId(UserAPI.getRole(user.getRole().getName()).getRoleId());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);

		Command addUser = FacilioChainFactory.getUpdateUserCommand();
		addUser.execute(context);
		
		return SUCCESS;
	}

	public String changeStatus() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);

		Command addUser = FacilioChainFactory.getChangeUserStatusCommand();
		addUser.execute(context);
		
		return SUCCESS;
	}
}