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
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
		
		SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
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
		switch (this.role) {
		case FacilioConstants.Role.SUPER_ADMIN:
			return "Super Administrator";
		case FacilioConstants.Role.ADMIN:
			return "Administrator";
		case FacilioConstants.Role.AGENT:
			return "Agent";
		case FacilioConstants.Role.REQUESTER:
			return "Requester";
		}
		return "User";
	}
	
	public String getStatusAsString() {
		if (this.inviteAcceptStatus) {
			return "Active";
		}
		else {
			return "Pending";
		}
	}
}
