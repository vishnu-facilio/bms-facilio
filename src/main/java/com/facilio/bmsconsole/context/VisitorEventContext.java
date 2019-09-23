package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorEventContext extends ModuleBaseWithCustomFields{
	
	private static final long serialVersionUID = 1L;

	private String eventName;
	private long eventStartTime = -1;
	private long eventEndTime = -1;
	private long eventDuration = -1;
	
	private User eventHost;

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public long getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(long eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public long getEventEndTime() {
		return eventEndTime;
	}

	public void setEventEndTime(long eventEndTime) {
		this.eventEndTime = eventEndTime;
	}

	public long getEventDuration() {
		return eventDuration;
	}

	public void setEventDuration(long eventDuration) {
		this.eventDuration = eventDuration;
	}

	public User getEventHost() {
		return eventHost;
	}

	public void setEventHost(User eventHost) {
		this.eventHost = eventHost;
	}
	
	private List<EventVisitorRelContext> invitees;

	public List<EventVisitorRelContext> getInvitees() {
		return invitees;
	}

	public void setInvitees(List<EventVisitorRelContext> invitees) {
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
