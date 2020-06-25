package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

import java.io.File;

public class V3VisitorLoggingContext extends V3Context {

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
    private Long checkInTime;
    private Long checkOutTime;
    private V3VisitorContext visitor;
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

    public Long getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Long getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    private Boolean isApprovalNeeded;

    public Boolean getIsApprovalNeeded() {
        return isApprovalNeeded;
    }

    public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
        this.isApprovalNeeded = isApprovalNeeded;
    }

    public Boolean isApprovalNeeded() {
        if (isApprovalNeeded != null) {
            return isApprovalNeeded.booleanValue();
        }
        return false;
    }

    public V3VisitorContext getVisitor() {
        return visitor;
    }

    public void setVisitor(V3VisitorContext visitor) {
        this.visitor = visitor;
    }

    private Integer purposeOfVisit;
    public Integer getPurposeOfVisit() {
        return purposeOfVisit;
    }

    public void setPurposeOfVisit(Integer purposeOfVisit) {
        this.purposeOfVisit = purposeOfVisit;
    }

    private String purposeOfVisitEnum;

    public String getPurposeOfVisitEnum() {
        return purposeOfVisitEnum;
    }

    public void setPurposeOfVisitEnum(String purposeOfVisitEnum) {
        this.purposeOfVisitEnum = purposeOfVisitEnum;
    }

    private Long ndaId;
    private String ndaUrl;
    private File nda;
    private String ndaFileName;
    private  String ndaContentType;

    private Long signatureId;
    private String signatureUrl;
    private File signature;
    private String signatureFileName;
    private  String signatureContentType;

    public Long getSignatureId() {
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

    public Long getNdaId() {
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

    private Long avatarId;
    public Long getAvatarId() {
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

    private Long expectedVisitDuration;
    private Long expectedCheckOutTime;



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

    public Boolean isBlocked;

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Boolean isBlocked() {
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

    public Boolean isVip() {
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

    private Long parentLogId;

    public Long getParentLogId() {
        return parentLogId;
    }

    public void setParentLogId(Long parentLogId) {
        this.parentLogId = parentLogId;
    }

    private String groupName;
    private Long expectedCheckInTime;
    private Long sourceId;

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

    public Boolean isPreregistered() {
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

    public Boolean isInviteApprovalNeeded() {
        if (isInviteApprovalNeeded != null) {
            return isInviteApprovalNeeded.booleanValue();
        }
        return false;
    }

    public static enum Source implements FacilioEnum {
        WORKORDER, PURCHASE_ORDER, TENANT, MANUAL;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static V3VisitorLoggingContext.Source valueOf(int value) {
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

    public Long getExpectedCheckInTime() {
        return expectedCheckInTime;
    }

    public void setExpectedCheckInTime(Long expectedCheckInTime) {
        this.expectedCheckInTime = expectedCheckInTime;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    private V3VisitorLoggingContext.Source source;
    public Integer getSource() {
        if (source != null) {
            return source.getIndex();
        }
        return null;
    }
    public void setSource(Integer source) {
        if(source != null) {
            this.source = V3VisitorLoggingContext.Source.valueOf(source);
        }
    }
    public V3VisitorLoggingContext.Source getSourceEnum() {
        return source;
    }

    private PMTriggerContext trigger;

    public PMTriggerContext getTrigger() {
        return trigger;
    }

    public void setTrigger(PMTriggerContext trigger) {
        this.trigger = trigger;
    }

    public V3VisitorLoggingContext getChildLog(long expectedCheckInTime) throws Exception{
        V3VisitorLoggingContext childLog = FieldUtil.cloneBean(this, V3VisitorLoggingContext.class);
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

    private Long logGeneratedUpto;
    public Long getLogGeneratedUpto() {
        return logGeneratedUpto;
    }

    public void setLogGeneratedUpto(Long logGeneratedUpto) {
        this.logGeneratedUpto = logGeneratedUpto;
    }

    private Boolean isInvitationSent;

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

    private Boolean isOverStay;

    public Boolean getIsOverStay() {
        return isOverStay;
    }

    public void setIsOverStay(Boolean isOverStay) {
        this.isOverStay = isOverStay;
    }

    public Boolean isOverStay() {
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

    private Long actualVisitDuration;

    public Long getActualVisitDuration() {
        return actualVisitDuration;
    }

    public void setActualVisitDuration(Long actualVisitDuration) {
        this.actualVisitDuration = actualVisitDuration;
    }

    private Boolean isReturningVisitor;

    public Boolean getIsReturningVisitor() {
        return isReturningVisitor;
    }

    public void setIsReturningVisitor(Boolean isReturning) {
        this.isReturningVisitor = isReturning;
    }

    public Boolean isReturningVisitor() {
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

    private V3TenantContext tenant;
    public V3TenantContext getTenant() {
        return tenant;
    }
    public void setTenant(V3TenantContext tenant) {
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
