package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
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
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.opensymphony.xwork2.ActionContext;
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
	
	private Map<Long, String> roles;
	public Map<Long, String> getRoles() {
		return roles;
	}
	public void setRoles(Map<Long, String> roles) {
		this.roles = roles;
	}
	
	public String newUser() throws Exception {
		
		setSetup(SetupLayout.getNewUserLayout());
		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	public String addUser() throws Exception {
		
		// setting necessary fields
		user.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		user.setPassword("Test1@34");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		
		try {
			Chain addUser = FacilioChainFactory.getAddUserCommand();
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
			else{
				e.printStackTrace();
				System.out.println("........> Error");
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
		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	public String updateMyProfile() throws Exception{
		boolean status = UserAPI.updateUser(user, OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return SUCCESS;
	}
	
	public String updateUser() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);

		Command addUser = FacilioChainFactory.getUpdateUserCommand();
		addUser.execute(context);
				
		return SUCCESS;
	}

	public String changeStatus() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		Map params = ActionContext.getContext().getParameters();
		
System.out.println("User object is "+params+"\n"+ user);
		Command addUser = FacilioChainFactory.getChangeUserStatusCommand();
		addUser.execute(context);
		
		return SUCCESS;
	}
	


	private File avatar;
	
	public File getAvatar() {
		return avatar;
	}
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}
	
	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	
	private String avatarContentType;
	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private String avatarUrl;
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	
	public String uploadUserAvatar() throws Exception {
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(getAvatarFileName(), getAvatar(), getAvatarContentType());
		
		UserAPI.updateUserPhoto(userId, fileId, null);
		
		setAvatarUrl(fs.getPrivateUrl(fileId));
		
		return SUCCESS;
	}
	
	public String deleteUserAvatar() throws Exception {
		
		long photoId = UserInfo.getCurrentUser().getPhotoId();
		if (photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.deleteFile(photoId);
		}
		
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	
}