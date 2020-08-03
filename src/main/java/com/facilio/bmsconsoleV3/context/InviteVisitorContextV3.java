package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;

public class InviteVisitorContextV3 extends BaseVisitContextV3 {

	private static final long serialVersionUID = 1L;

	private Long expectedVisitDuration;
	private Long expectedCheckOutTime;
	private Long expectedCheckInTime;
	private Boolean isInviteApprovalNeeded;
	private Boolean isRecurring;
	private Long logGeneratedUpto;
	private Boolean isInvitationSent;
	private Long groupId;
	private Long parentInviteId;
	private Boolean isInvited;
	//private Long parentLogId;

	public Long getExpectedVisitDuration() {
		return expectedVisitDuration;
	}

	public void setExpectedVisitDuration(Long expectedVisitDuration) {
		this.expectedVisitDuration = expectedVisitDuration;
	}

	public Long getExpectedCheckOutTime() {
		return expectedCheckOutTime;
	}

	public void setExpectedCheckOutTime(Long expectedCheckOutTime) {
		this.expectedCheckOutTime = expectedCheckOutTime;
	}

	public Long getExpectedCheckInTime() {
		return expectedCheckInTime;
	}

	public void setExpectedCheckInTime(Long expectedCheckInTime) {
		this.expectedCheckInTime = expectedCheckInTime;
	}

	public Boolean getIsInviteApprovalNeeded() {
		return isInviteApprovalNeeded;
	}

	public void setIsInviteApprovalNeeded(Boolean isInviteApprovalNeeded) {
		this.isInviteApprovalNeeded = isInviteApprovalNeeded;
	}

	public Boolean isInviteApprovalNeeded() {
		if (isInviteApprovalNeeded != null) {
			return isInviteApprovalNeeded.booleanValue();
		}
		return false;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public Boolean isRecurring() {
		if (isRecurring != null) {
			return isRecurring.booleanValue();
		}
		return false;
	}

	public Long getLogGeneratedUpto() {
		return logGeneratedUpto;
	}

	public void setLogGeneratedUpto(Long logGeneratedUpto) {
		this.logGeneratedUpto = logGeneratedUpto;
	}

	public Boolean getIsInvitationSent() {
		return isInvitationSent;
	}

	public void setIsInvitationSent(Boolean isInvitationSent) {
		this.isInvitationSent = isInvitationSent;
	}

	public Boolean isInvitationSent() {
		if (isInvitationSent != null) {
			return isInvitationSent.booleanValue();
		}
		return false;
	}
	
	public InviteVisitorContextV3 getChildLog(long expectedCheckInTime) throws Exception {
		InviteVisitorContextV3 childLog = FieldUtil.cloneBean(this, InviteVisitorContextV3.class);
		childLog.setExpectedCheckInTime(expectedCheckInTime * 1000);
		childLog.setExpectedCheckOutTime((expectedCheckInTime * 1000)
				+ (this.getExpectedVisitDuration() != null ? this.getExpectedVisitDuration() : 0));
		childLog.setIsRecurring(false);
		//childLog.setParentLogId(this.getId());
		childLog.setIsInviteApprovalNeeded(false);
		if (this.getVisitor() != null) {
			childLog.setVisitorName(this.getVisitorName());
			childLog.setVisitorEmail(this.getVisitorEmail());
			childLog.setVisitorPhone(this.getVisitorPhone());
		}
		FacilioStatus status = VisitorManagementAPI.getLogStatus("Upcoming");
		if (status != null) {
			childLog.setModuleState(status);
		}
		childLog.setIsInvitationSent(true);
		return childLog;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getParentInviteId() {
		return parentInviteId;
	}

	public void setParentInviteId(Long parentInviteId) {
		this.parentInviteId = parentInviteId;
	}

	public Boolean getIsInvited() {
		return isInvited;
	}

	public void setIsInvited(Boolean isInvited) {
		this.isInvited = isInvited;
	}
	
	public Boolean isInvited() {
		if (isInvited != null) {
			return isInvited.booleanValue();
		}
		return false;
	}
}
