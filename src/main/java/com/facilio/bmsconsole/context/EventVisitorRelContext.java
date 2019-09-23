package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class EventVisitorRelContext extends ModuleBaseWithCustomFields{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private VisitorEventContext eventId ;
	private VisitorContext visitorId;
	public VisitorEventContext getEventId() {
		return eventId;
	}
	public void setEventId(VisitorEventContext eventId) {
		this.eventId = eventId;
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
