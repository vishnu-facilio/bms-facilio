package com.facilio.bmsconsole.context;

public class SupportEmailContext {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private String replyName;
	public String getReplyName() {
		return replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	
	private String actualEmail;
	public String getActualEmail() {
		return actualEmail;
	}
	public void setActualEmail(String actualEmail) {
		this.actualEmail = actualEmail;
	}
	
	private String fwdEmail;
	public String getFwdEmail() {
		return fwdEmail;
	}
	public void setFwdEmail(String fwdEmail) {
		this.fwdEmail = fwdEmail;
	}
	
	private long autoAssignGroupId = -1;
	public long getAutoAssignGroupId() {
		return autoAssignGroupId;
	}
	public void setAutoAssignGroupId(long autoAssignGroupId) {
		this.autoAssignGroupId = autoAssignGroupId;
	}
	
	@Override
	public String toString() {
		return "SupportEmailContext [id=" + id + ", orgId=" + orgId + ", replyName=" + replyName + ", actualEmail="
				+ actualEmail + ", fwdEmail=" + fwdEmail + ", autoAssignGroupId=" + autoAssignGroupId
				+ ", autoAssignGroupId =" + autoAssignGroupId + ", verified=" + verified + ", primarySupportMail="
				+ primarySupportMail + "]";
	}

	private boolean verified;
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	private boolean primarySupportMail;
	public boolean getPrimarySupportMail() {
		return primarySupportMail;
	}
	public void setPrimarySupportMail(boolean primarySupportMail) {
		this.primarySupportMail = primarySupportMail;
	}
}
