package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class InviteVisitorRelContext extends ModuleBaseWithCustomFields{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private VisitorInviteContext inviteId ;
	private VisitorContext visitorId;
	
	private String qrUrl;
	private File qr;
	private String qrFileName;
	private  String qrContentType;
	private long qrId;
	
	
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

	public long getQrId() {
		return qrId;
	}

	public void setQrId(long qrId) {
		this.qrId = qrId;
	}

	public String getQrUrl() {
		return qrUrl;
	}

	public void setQrUrl(String qrUrl) {
		this.qrUrl = qrUrl;
	}

	public File getQr() {
		return qr;
	}

	public void setQr(File qr) {
		this.qr = qr;
	}

	public String getQrFileName() {
		return qrFileName;
	}

	public void setQrFileName(String qrFileName) {
		this.qrFileName = qrFileName;
	}

	public String getqrContentType() {
		return qrContentType;
	}

	public void setQrContentType(String qrContentType) {
		this.qrContentType = qrContentType;
	}

}
