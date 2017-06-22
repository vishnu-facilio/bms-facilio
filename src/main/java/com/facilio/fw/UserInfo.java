package com.facilio.fw;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;

/**
 * 
 * Current user object.
 *
 * You can get current user object by UserInfo.getCurrentUser();
 * 
 * Also, you can check if the current user has 'admin' role by UserInfo.getCurrentUser().hasRole('admin')
 * 
 */

public class UserInfo {

	private long userId;
	private long orgId;
	private long orgUserId;
	private String email;
	private String username;
	private String name;
	private int role;
	private boolean isActive;

	private static ThreadLocal<UserInfo> userlocal = new ThreadLocal<UserInfo>();

	public UserInfo(String username) throws Exception
	{
		this.username = username;
		UserContext context = UserAPI.getUser(username);
		
		setUserId(context.getUserId());
		setOrgId(context.getOrgId());
		setOrgUserId(context.getOrgUserId());
		setEmail(context.getEmail());
		setUsername(context.getEmail());
		setName(context.getName());
		setRole(context.getRole());
		setActive(context.getInviteAcceptStatus());
	}

	public static UserInfo getCurrentUser()
	{
		return userlocal.get();
	}

	public static  void setCurrentUser(UserInfo userinfo)
	{
		userlocal.set(userinfo);
	}

	public static void cleanup()
	{
		userlocal.remove();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getOrgUserId() {
		return orgUserId;
	}

	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
	public boolean hasRole(int role) {
		if (this.role == role) {
			return true;
		}
		return false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}