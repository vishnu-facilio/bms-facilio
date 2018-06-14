package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {

	private static Logger log = LogManager.getLogger(UserAction.class.getName());
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

	public String portalUserList() throws Exception {
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(AccountUtil.getOrgBean().getOrgPortalUsers(AccountUtil.getCurrentOrg().getOrgId()));
		return SUCCESS;
	}

	public String deletePortalUser() throws Exception {
		System.out.println("### Delete portal user :"+user.getEmail());
		Connection conn = null;
		Statement statement = null;
		try	{
			Organization org = AccountUtil.getOrgBean().getPortalOrg(AccountUtil.getCurrentOrg().getDomain());
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			statement = conn.createStatement();
			String sql = "delete from faciliorequestors where PORTALID="+org.getPortalId() +" and  email = '"+ user.getEmail()+"';";
			System.out.println(sql);
			statement.execute(sql);
		} catch (SQLException | RuntimeException e) {
			log.info("Exception occurred ", e);
			result = "User cannot be deleted.";
			return ERROR;
		} finally {
			DBUtil.closeAll(conn, statement);
		}
		portalUserList();
		return SUCCESS;
	}

	public String deleteUser() throws Exception {
		System.out.println("### Delete user :"+user.getEmail());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		Map params = ActionContext.getContext().getParameters();
		
		System.out.println("User object is "+params+"\n"+ user);
		Command deleteUser = FacilioChainFactory.getDeleteUserCommand();
		deleteUser.execute(context);
		
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
	public String inviteRequester() throws Exception {
		
		// setting necessary fields
		long orgid = AccountUtil.getCurrentOrg().getOrgId();
		user.setOrgId(orgid);
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		try {
			AccountUtil.getUserBean().inviteRequester(orgid, user);
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
				log.info("Exception occurred ", e);
				addFieldError("email", "This user already exists in your organization.");
				System.out.println("........> Error");
			}
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addUser() throws Exception {
		
		// setting necessary fields
		user.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		HttpServletRequest request = ServletActionContext.getRequest();
		String value = LoginUtil.getUserCookie(request, "fc.authtype");
		user.setFacilioAuth("facilio".equals(value));

		if(user.getRoleId() <=0 )
		{
			addFieldError("role", "This user already exists in your organization.");
			return ERROR;

		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, accessibleSpace);
		
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
				log.info("Exception occurred ", e);
				addFieldError("email", "This user already exists in your organization.");
				System.out.println("........> Error");
			}
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addRequester() throws Exception {
		
		// setting necessary fields
		user.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		
		long requesterId = AccountUtil.getUserBean().addRequester(AccountUtil.getCurrentOrg().getOrgId(), user);
		user.setId(requesterId);
		
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
	
	private List<Long> accessibleSpace;
	public List<Long> getAccessibleSpace() {
		return accessibleSpace;
	}

	public void setAccessibleSpace(List<Long> accessibleSpace) {
		this.accessibleSpace = accessibleSpace;
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
		System.out.println("!@@!@!@!!!!!!!!!!! user"+user);
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
	
	private UserMobileSetting userMobileSetting;
	public UserMobileSetting getUserMobileSetting() {
		return userMobileSetting;
	}

	public void setUserMobileSetting(UserMobileSetting userMobileSetting) {
		this.userMobileSetting = userMobileSetting;
	}
	
	public String addMobileSetting() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, userMobileSetting);

		Command addUserMobileSettingCommand = FacilioChainFactory.getAddUserMobileSettingCommand();
		addUserMobileSettingCommand.execute(context);
				
		return SUCCESS;
	}
	public String removeMobileSetting() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, mobileInstanceId);

		Command addUserMobileSettingCommand = FacilioChainFactory.getDeleteUserMobileSettingCommand();
		addUserMobileSettingCommand.execute(context);
				
		return SUCCESS;
	}
	
	private String mobileInstanceId;
	public String getMobileInstanceId() {
		return mobileInstanceId;
	}
	public void setMobileInstanceId(String mobileInstanceId) {
		this.mobileInstanceId = mobileInstanceId;
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