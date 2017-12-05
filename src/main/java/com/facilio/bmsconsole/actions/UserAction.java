package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
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
	
	private List<User> users = null;
	public List<User> getUsers() {
		return users;
	}
	
	public void setUsers(List<User> users) {
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
			List<GroupMember> memberList = (List<GroupMember>) AccountUtil.getGroupBean().getGroupMembers(getGroupId());
			this.users = new ArrayList<>();
			if (memberList != null) {
				for (GroupMember member : memberList) {
					this.users.add(member);
				}
			}
		}
		else {
			setUsers(AccountUtil.getOrgBean().getAllOrgUsers(AccountUtil.getCurrentOrg().getOrgId()));
		}
		return SUCCESS;
	}
	
	public String userList() throws Exception 
	{
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(AccountUtil.getOrgBean().getAllOrgUsers(AccountUtil.getCurrentOrg().getOrgId()));
		
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
//		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	public String addUser() throws Exception {
		
		// setting necessary fields
		user.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		
		try {
			Chain addUser = FacilioChainFactory.getAddUserCommand();
			addUser.execute(context);
			setUserId(user.getId());
		}
		catch (Exception e) {
			if (e instanceof AccountException) {
				AccountException ae = (AccountException) e;
				if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS)) {
					addFieldError("email", "This user already exists in your organization.");
				}
			}
			else {
				e.printStackTrace();
				addFieldError("email", "This user already exists in your organization.");
				System.out.println("........> Error");
			}
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String resendInvite() throws Exception {
		
		AccountUtil.getUserBean().resendInvite(getUserId());
		
		return SUCCESS;
	}
	
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
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
//		setUser(UserAPI.getUserFromOrgUserId(getUserId()));
//		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	public String updateMyProfile() throws Exception{
		boolean status = AccountUtil.getUserBean().updateUser(AccountUtil.getCurrentUser().getId(), user);
		
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
		
		AccountUtil.getUserBean().updateUserPhoto(userId, fileId);
		
		setAvatarUrl(fs.getPrivateUrl(fileId));
		
		return SUCCESS;
	}
	
	public String deleteUserAvatar() throws Exception {
		
		long photoId = AccountUtil.getCurrentUser().getPhotoId();
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