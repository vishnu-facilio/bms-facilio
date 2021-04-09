package com.facilio.bmsconsoleV3.context;

import java.io.File;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.v3.context.V3Context;

public class VisitorLogContextV3 extends BaseVisitContextV3 {

	private static final long serialVersionUID = 1L;

	private Long checkInTime;
	private Long checkOutTime;
    private Long actualVisitDuration;
    private Boolean isApprovalNeeded;
    private Boolean isOverStay;
    public Boolean isNdaSigned;
	
    private Long ndaId;
    private String ndaUrl;
    private File nda;
    private String ndaFileName;
    private String ndaContentType;

    private Long signatureId;
    private String signatureUrl;
    private File signature;
    private String signatureFileName;
    private String signatureContentType;
    
    private InviteVisitorContextV3 invite;

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

	public Long getActualVisitDuration() {
		return actualVisitDuration;
	}

	public void setActualVisitDuration(Long actualVisitDuration) {
		this.actualVisitDuration = actualVisitDuration;
	}

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

	public Long getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(Long signatureId) {
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

	public void setNdaId(Long ndaId) {
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

	public Boolean getIsNdaSigned() {
		return isNdaSigned;
	}

	public void setIsNdaSigned(Boolean isNdaSigned) {
		this.isNdaSigned = isNdaSigned;
	}

	public Boolean isNdaSigned() {
		if (isNdaSigned != null) {
			return isNdaSigned.booleanValue();
		}
		return false;
	}

	public InviteVisitorContextV3 getInvite() {
		return invite;
	}

	public void setInvite(InviteVisitorContextV3 invite) {
		this.invite = invite;
	}
	
	@Override
	public String toString() {
		return "VisitorLogContextV3 [checkInTime=" + checkInTime + ", isApprovalNeeded=" + isApprovalNeeded
				+ ", invite=" + invite + ", getVisitorType()=" + getVisitorType() + ", getHost()=" + getHost()
				+ ", getVisitor()=" + getVisitor() + ", getVisitedSpace()=" + getVisitedSpace() + ", getPassCode()="
				+ getPassCode() + ", getVisitorName()=" + getVisitorName() + ", getVisitorEmail()=" + getVisitorEmail()
				+ ", getVisitorPhone()=" + getVisitorPhone() + ", getChildVisitTypeEnum()=" + getChildVisitTypeEnum()
				+ ", getFormId()=" + getFormId() + ", getModuleState()=" + getModuleState() + ", getStateFlowId()="
				+ getStateFlowId() + "]";
	}
}
