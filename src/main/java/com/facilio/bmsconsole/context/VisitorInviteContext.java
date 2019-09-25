package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorInviteContext extends ModuleBaseWithCustomFields{
	
	private static final long serialVersionUID = 1L;

	private String inviteName;
	private long expectedStartTime = -1;
	private long expectedEndTime = -1;
	private long expectedDuration = -1;
	
	private User inviteHost;
	

	public String getInviteName() {
		return inviteName;
	}

	public void setInviteName(String inviteName) {
		this.inviteName = inviteName;
	}

	public User getInviteHost() {
		return inviteHost;
	}

	public void setInviteHost(User inviteHost) {
		this.inviteHost = inviteHost;
	}

	public long getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(long expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public long getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(long expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public long getExpectedDuration() {
		return expectedDuration;
	}

	public void setExpectedDuration(long expectedDuration) {
		this.expectedDuration = expectedDuration;
	}

	private List<InviteVisitorRelContext> invitees;

	public List<InviteVisitorRelContext> getInvitees() {
		return invitees;
	}

	public void setInvitees(List<InviteVisitorRelContext> invitees) {
		this.invitees = invitees;
	}
	
	private Boolean isApprovalNeeded;

	public Boolean getIsApprovalNeeded() {
		return isApprovalNeeded;
	}

	public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
		this.isApprovalNeeded = isApprovalNeeded;
	}

	public boolean isApprovalNeeded() {
		if (isApprovalNeeded != null) {
			return isApprovalNeeded.booleanValue();
		}
		return false;
	}
	
	
	private Boolean isInviteeApprovalNeeded;

	public Boolean getIsInviteeApprovalNeeded() {
		return isInviteeApprovalNeeded;
	}

	public void setIsInviteeApprovalNeeded(Boolean isApprovalNeeded) {
		this.isInviteeApprovalNeeded = isApprovalNeeded;
	}

	public boolean isInviteeApprovalNeeded() {
		if (isInviteeApprovalNeeded != null) {
			return isInviteeApprovalNeeded.booleanValue();
		}
		return false;
	}
}
