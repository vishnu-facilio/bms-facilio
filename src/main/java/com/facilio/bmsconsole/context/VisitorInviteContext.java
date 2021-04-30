package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioIntEnum;
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
	
	private InviteSource inviteSource;
	public int getInviteSource() {
		if (inviteSource != null) {
			return inviteSource.getIndex();
		}
		return -1;
	}
	public void setInviteSource(int inviteSource) {
		this.inviteSource = InviteSource.valueOf(inviteSource);
	}
	public InviteSource getInviteSourceEnum() {
		return inviteSource;
	}
	public void setInviteSource(InviteSource inviteSource) {
		this.inviteSource = inviteSource;
	}

	public static enum InviteSource implements FacilioIntEnum {
		WORKORDER, PURCHASE_ORDER, TENANT, MANUAL;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static InviteSource valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long sourceId;


	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	
	private VisitorTypeContext visitorType;

	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	private BusinessHoursContext recurringVisitTime;
	
	public BusinessHoursContext getRecurringVisitTime() {
		return recurringVisitTime;
	}

	public void setRecurringVisitTime(BusinessHoursContext recurringVisitTime) {
		this.recurringVisitTime = recurringVisitTime;
	}

	private Boolean isRecurring;

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public boolean isRecurring() {
		if (isRecurring != null) {
			return isRecurring.booleanValue();
		}
		return false;
	}
	
	private long visitingHoursId;

	public long getVisitingHoursId() {
		return visitingHoursId;
	}

	public void setVisitingHoursId(long visitingHoursId) {
		this.visitingHoursId = visitingHoursId;
	}
	
	private VendorContext vendor;


	public VendorContext getVendor() {
		return vendor;
	}

	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	private User requestedBy;


	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	
}
