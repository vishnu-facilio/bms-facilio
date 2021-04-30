package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorLoggingContext extends ModuleBaseWithCustomFields{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private VisitorTypeContext visitorType;
	
	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}

	private VisitorInviteContext invite;
	private ContactsContext host;
	private long checkInTime = -1;
	private long checkOutTime = -1;
	private VisitorContext visitor;
	private BaseSpaceContext visitedSpace;
	
	

	public BaseSpaceContext getVisitedSpace() {
		return visitedSpace;
	}

	public void setVisitedSpace(BaseSpaceContext visitedSpace) {
		this.visitedSpace = visitedSpace;
	}

	public VisitorInviteContext getInvite() {
		return invite;
	}

	public void setInvite(VisitorInviteContext invite) {
		this.invite = invite;
	}

	public ContactsContext getHost() {
		return host;
	}

	public void setHost(ContactsContext host) {
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
	
	private int purposeOfVisit;
	public int getPurposeOfVisit() {
		return purposeOfVisit;
	}

	public void setPurposeOfVisit(int purposeOfVisit) {
		this.purposeOfVisit = purposeOfVisit;
	}
	
	private String purposeOfVisitEnum;
	
	public String getPurposeOfVisitEnum() {
		return purposeOfVisitEnum;
	}

	public void setPurposeOfVisitEnum(String purposeOfVisitEnum) {
		this.purposeOfVisitEnum = purposeOfVisitEnum;
	}

	private long ndaId;
	private String ndaUrl;
	private File nda;
	private String ndaFileName;
	private  String ndaContentType;
	
	private long signatureId;
	private String signatureUrl;
	private File signature;
	private String signatureFileName;
	private  String signatureContentType;
	
	public long getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(long signatureId) {
		this.signatureId = signatureId;
	}

	public String getSignatureUrl() {
		return signatureUrl;
	}

	public void setSignatureUrl(String signatureUrl) {
		this.signatureUrl = signatureUrl;
	}

	public File getSignature() {
		return signature;
	}

	public void setSignature(File signature) {
		this.signature = signature;
	}

	public String getSignatureFileName() {
		return signatureFileName;
	}

	public void setSignatureFileName(String signatureFileName) {
		this.signatureFileName = signatureFileName;
	}

	public String getSignatureContentType() {
		return signatureContentType;
	}

	public void setSignatureContentType(String signatureContentType) {
		this.signatureContentType = signatureContentType;
	}

	public long getNdaId() {
		return ndaId;
	}

	public void setNdaId(long ndaId) {
		this.ndaId = ndaId;
	}

	public String getNdaUrl() {
		return ndaUrl;
	}

	public void setNdaUrl(String ndaUrl) {
		this.ndaUrl = ndaUrl;
	}

	public File getNda() {
		return nda;
	}

	public void setNda(File nda) {
		this.nda = nda;
	}

	public String getNdaFileName() {
		return ndaFileName;
	}

	public void setNdaFileName(String ndaFileName) {
		this.ndaFileName = ndaFileName;
	}

	public String getNdaContentType() {
		return ndaContentType;
	}

	public void setNdaContentType(String ndaContentType) {
		this.ndaContentType = ndaContentType;
	}

	private long avatarId;
	public long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(long avatarId) {
		this.avatarId = avatarId;
	}

	private String avatarUrl;
	
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private File avatar;
	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	private String avatarFileName;
	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	private  String avatarContentType;

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private long expectedVisitDuration;
	private long expectedCheckOutTime;



	public long getExpectedVisitDuration() {
		return expectedVisitDuration;
	}

	public void setExpectedVisitDuration(long expectedVisitDuration) {
		this.expectedVisitDuration = expectedVisitDuration;
	}

	public long getExpectedCheckOutTime() {
		return expectedCheckOutTime;
	}

	public void setExpectedCheckOutTime(long expectedCheckOutTime) {
		this.expectedCheckOutTime = expectedCheckOutTime;
	}
	
	public Boolean isBlocked;

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean isBlocked() {
		if (isBlocked != null) {
			return isBlocked.booleanValue();
		}
		return false;
	}
	public Boolean isVip;

	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public boolean isVip() {
		if (isVip != null) {
			return isVip.booleanValue();
		}
		return false;
	}

	private VendorContext vendor;


	public VendorContext getVendor() {
		return vendor;
	}

	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	public Boolean isNdaSigned;

	public Boolean getIsNdaSigned() {
		return isNdaSigned;
	}

	public void setIsNdaSigned(Boolean isNdaSigned) {
		this.isNdaSigned = isNdaSigned;
	}

	public boolean isNdaSigned() {
		if (isNdaSigned != null) {
			return isNdaSigned.booleanValue();
		}
		return false;
	}
	
	public Boolean isInvited;

	public Boolean getIsInvited() {
		return isInvited;
	}

	public void setIsInvited(Boolean isInvited) {
		this.isInvited = isInvited;
	}

	public boolean isInvited() {
		if (isInvited != null) {
			return isInvited.booleanValue();
		}
		return false;
	}
	
	private Boolean photoStatus;
	public Boolean getPhotoStatus() {
		if (photoStatus == null) {
			return false;
		}
		return photoStatus;
	}
	public void setPhotoStatus(Boolean photoStatus) {
		this.photoStatus = photoStatus;
	}
	
	private Boolean hostStatus;
	public Boolean getHostStatus() {
		if (hostStatus == null) {
			return false;
		}
		return hostStatus;
	}
	public void setHostStatus(Boolean hostStatus) {
		this.hostStatus = hostStatus;
	}
	
	private Boolean idProofScanned;
	public Boolean getIdProofScanned() {
		if (idProofScanned == null) {
			return false;
		}
		return idProofScanned;
	}
	public void setIdProofScanned(Boolean idProofScanned) {
		this.idProofScanned = idProofScanned;
	}
	
	private long parentLogId;

	public long getParentLogId() {
		return parentLogId;
	}

	public void setParentLogId(long parentLogId) {
		this.parentLogId = parentLogId;
	}
	
	private String groupName;
	private long expectedCheckInTime;
	private long sourceId;
	
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
	
	private Boolean isPreregistered;

	public Boolean getIsPreregistered() {
		return isPreregistered;
	}

	public void setIsPreregistered(Boolean isPreregistered) {
		this.isPreregistered = isPreregistered;
	}

	public boolean isPreregistered() {
		if (isPreregistered != null) {
			return isPreregistered.booleanValue();
		}
		return false;
	}
	
	
	private Boolean isInviteApprovalNeeded;

	public Boolean getIsInviteApprovalNeeded() {
		return isInviteApprovalNeeded;
	}

	public void setIsInviteApprovalNeeded(Boolean isInviteApprovalNeeded) {
		this.isInviteApprovalNeeded = isInviteApprovalNeeded;
	}

	public boolean isInviteApprovalNeeded() {
		if (isInviteApprovalNeeded != null) {
			return isInviteApprovalNeeded.booleanValue();
		}
		return false;
	}
	
	public static enum Source implements FacilioIntEnum {
		WORKORDER, PURCHASE_ORDER, TENANT, MANUAL;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static Source valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getExpectedCheckInTime() {
		return expectedCheckInTime;
	}

	public void setExpectedCheckInTime(long expectedCheckInTime) {
		this.expectedCheckInTime = expectedCheckInTime;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	
	private Source source;
	public int getSource() {
		if (source != null) {
			return source.getIndex();
		}
		return -1;
	}
	public void setSource(int source) {
		this.source = Source.valueOf(source);
	}
	public Source getSourceEnum() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	
	private PMTriggerContext trigger;

	public PMTriggerContext getTrigger() {
		return trigger;
	}

	public void setTrigger(PMTriggerContext trigger) {
		this.trigger = trigger;
	}
	
	public VisitorLoggingContext getChildLog(long expectedCheckInTime) throws Exception{
		VisitorLoggingContext childLog = FieldUtil.cloneBean(this, VisitorLoggingContext.class);
		childLog.setExpectedCheckInTime(expectedCheckInTime * 1000);
		childLog.setExpectedCheckOutTime((expectedCheckInTime * 1000) + this.getExpectedVisitDuration());
		childLog.setIsRecurring(false);
		childLog.setParentLogId(this.getId());
		childLog.setIsInviteApprovalNeeded(false);
		if(this.getVisitor() != null) {
			childLog.setVisitorName(this.getVisitorName());
			childLog.setVisitorEmail(this.getVisitorEmail());
			childLog.setVisitorPhone(this.getVisitorPhone());
		}
		FacilioStatus status = VisitorManagementAPI.getLogStatus("Upcoming");
		if(status != null) {
			childLog.setModuleState(status);
		}
		childLog.setIsInvitationSent(true);
		
		return childLog;
	}
	
	private long logGeneratedUpto;
	public long getLogGeneratedUpto() {
		return logGeneratedUpto;
	}

	public void setLogGeneratedUpto(long logGeneratedUpto) {
		this.logGeneratedUpto = logGeneratedUpto;
	}
	
	private Boolean isInvitationSent;

	public Boolean getIsInvitationSent() {
		return isInvitationSent;
	}

	public void setIsInvitationSent(Boolean isInvitationSent) {
		this.isInvitationSent = isInvitationSent;
	}

	public boolean isInvitationSent() {
		if (isInvitationSent != null) {
			return isInvitationSent.booleanValue();
		}
		return false;
	}
	
	private Boolean isOverStay;

	public Boolean getIsOverStay() {
		return isOverStay;
	}

	public void setIsOverStay(Boolean isOverStay) {
		this.isOverStay = isOverStay;
	}

	public boolean isOverStay() {
		if (isOverStay != null) {
			return isOverStay.booleanValue();
		}
		return false;
	}
	
	private String qrUrl;

	public String getQrUrl() {
		return qrUrl;
	}

	public void setQrUrl(String qrUrl) {
		this.qrUrl = qrUrl;
	}
	
	private User requestedBy;

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	
	private long actualVisitDuration;

	public long getActualVisitDuration() {
		return actualVisitDuration;
	}

	public void setActualVisitDuration(long actualVisitDuration) {
		this.actualVisitDuration = actualVisitDuration;
	}
	
	private Boolean isReturningVisitor;

	public Boolean getIsReturningVisitor() {
		return isReturningVisitor;
	}

	public void setIsReturningVisitor(Boolean isReturning) {
		this.isReturningVisitor = isReturning;
	}

	public boolean isReturningVisitor() {
		if (isReturningVisitor != null) {
			return isReturningVisitor.booleanValue();
		}
		return false;
	}
	
	private String passCode;

	public String getPassCode() {
		return passCode;
	}

	public void setPassCode(String passCode) {
		this.passCode = passCode;
	}

	private TenantContext tenant;
	public TenantContext getTenant() {
		return tenant;
	}
	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}

	private String visitorName;
	private String visitorEmail;
	private String visitorPhone;

	public String getVisitorName() {
		return visitorName;
	}

	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}

	public String getVisitorEmail() {
		return visitorEmail;
	}

	public void setVisitorEmail(String visitorEmail) {
		this.visitorEmail = visitorEmail;
	}

	public String getVisitorPhone() {
		return visitorPhone;
	}

	public void setVisitorPhone(String visitorPhone) {
		this.visitorPhone = visitorPhone;
	}
	
}
