package com.facilio.bmsconsole.context;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class UserContext extends FacilioContext {
	
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
		this.name = this.email.substring(0, email.indexOf("@"));
		this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
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
	
	public String getInvitedTimeStr() {
		long time = this.invitedTime * 1000;
		
		SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
		return sd.format(new Date(time));
	}
	
	private boolean inviteAcceptStatus;
	public boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}
	
	private int role = 0;
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
	public String getRoleAsString() {
		
		return FacilioConstants.Role.ALL_ROLES.get(this.role);
	}
	
	public String getStatusAsString() {
		if (this.inviteAcceptStatus) {
			return "Active";
		}
		else {
			return "Inactive";
		}
	}
}
