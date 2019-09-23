package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorLoggingContext extends ModuleBaseWithCustomFields{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private VisitorEventContext event;
	private User eventHost;
	private long checkInTime = -1;
	private long checkOutTime = -1;
	private VisitorContext visitor;
	
	public VisitorEventContext getEvent() {
		return event;
	}

	public void setEvent(VisitorEventContext event) {
		this.event = event;
	}

	public User getEventHost() {
		return eventHost;
	}

	public void setEventHost(User eventHost) {
		this.eventHost = eventHost;
	}

	public long getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(long checkInTime) {
		this.checkInTime = checkInTime;
	}

	public long getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(long checkOutTime) {
		this.checkOutTime = checkOutTime;
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

	public VisitorContext getVisitor() {
		return visitor;
	}

	public void setVisitor(VisitorContext visitor) {
		this.visitor = visitor;
	}
	
	

}
