package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorLoggingContext extends ModuleBaseWithCustomFields{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private VisitorInviteContext invite;
	private User host;
	private long checkInTime = -1;
	private long checkOutTime = -1;
	private VisitorContext visitor;
	private ResourceContext visitedSpace;
	
	

	public ResourceContext getVisitedSpace() {
		return visitedSpace;
	}

	public void setVisitedSpace(ResourceContext visitedSpace) {
		this.visitedSpace = visitedSpace;
	}

	public VisitorInviteContext getInvite() {
		return invite;
	}

	public void setInvite(VisitorInviteContext invite) {
		this.invite = invite;
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
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
	
	private String purposeOfVisit;

	public String getPurposeOfVisit() {
		return purposeOfVisit;
	}

	public void setPurposeOfVisit(String purposeOfVisit) {
		this.purposeOfVisit = purposeOfVisit;
	}
	
}
