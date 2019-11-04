package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.accounts.dto.User;
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
	
	
	
}
