package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class InviteVisitorRelContext extends ModuleBaseWithCustomFields{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private VisitorInviteContext inviteId ;
	private VisitorContext visitorId;
	
	public VisitorInviteContext getInviteId() {
		return inviteId;
	}
	public void setInviteId(VisitorInviteContext inviteId) {
		this.inviteId = inviteId;
	}
	public VisitorContext getVisitorId() {
		return visitorId;
	}
	public void setVisitorId(VisitorContext visitorId) {
		this.visitorId = visitorId;
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
	
}
