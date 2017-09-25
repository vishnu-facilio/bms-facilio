package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.UserAPI;

public class UserContext {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long userId = 0;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private long orgUserId = 0;
	public long getOrgUserId() {
		return orgUserId;
	}
	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
	}
	
	public long getId() {
		return orgUserId;
	}
	public void setId(long id) {
		this.orgUserId = id;
	}
	
	private String name;
	public String getName() {
		if (this.name == null && this.email != null) {
			this.name = this.email.substring(0, email.indexOf("@"));
			this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
		}
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
	
	private long invitedTime = 0;
	public long getInvitedTime() {
		return invitedTime;
	}
	public void setInvitedTime(long invitedTime) {
		this.invitedTime = invitedTime;
	}
	
	private boolean userStatus;
	public boolean getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(boolean userStatus) {
		this.userStatus = userStatus;
	}
	
	private boolean inviteAcceptStatus;
	public boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}
	
	private long roleId;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private RoleContext role;
	public RoleContext getRole() throws Exception {
		if (this.role == null) {
			this.role = UserAPI.getRole(this.roleId);
		}
		return role;
	}
	public void setRole(RoleContext role) {
		this.role = role;
	}
	
	private String timezone;
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "UserContext [orgId=" + orgId + ", userId=" + userId + ", orgUserId=" + orgUserId + ", name=" + name
				+ ", email=" + email + ", password=" + password + ", invitedTime=" + invitedTime + ", userStatus="
				+ userStatus + ", inviteAcceptStatus=" + inviteAcceptStatus + ", roleId=" + roleId + ", role=" + role
				+ ", timezone=" + timezone + ", phone=" + phone + "]";
	}
	
}
