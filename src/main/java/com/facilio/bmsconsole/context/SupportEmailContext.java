package com.facilio.bmsconsole.context;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;


public class SupportEmailContext implements  Serializable{
	private static final long serialVersionUID = 1L;

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
				+ primarySupportMail + ", isCustomMail=" + isCustomMail + ", workflowRuleId=" + supportRuleId + "]";
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

	public Long getSupportRuleId() {
		return supportRuleId;
	}

	public void setSupportRuleId(Long supportRuleId) {
		this.supportRuleId = supportRuleId;
	}

	private Long supportRuleId;


	public Boolean getIsCustomMail() {
		return isCustomMail;
	}

	public void setIsCustomMail(Boolean customMail) {
		isCustomMail = customMail;
	}

	private Boolean isCustomMail;


	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	private String mailServer;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private int port;


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String userName;


	private String password;

	@JSON(serialize=false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private AuthenticationType authentication;
	public int getAuthentication() {
		if (authentication == null) {
			return -1;
		}
		return authentication.getIndex();
	}
	public AuthenticationType getAuthenticationEnum() {
		return authentication;
	}
	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authentication = authenticationType;
	}
	public void setAuthenticationType(int type) {
		this.authentication = AuthenticationType.valueOf(type);
	}

	public static enum AuthenticationType implements FacilioIntEnum {
		PLAIN
		;
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static AuthenticationType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	public long getLatestMessageUID() {
		return latestMessageUID;
	}

	public void setLatestMessageUID(long latestMessageUID) {
		this.latestMessageUID = latestMessageUID;
	}

	private long latestMessageUID ;

	public long getUidValidaity() {
		return uidValidaity;
	}

	public void setUidValidaity(long uidValidaity) {
		this.uidValidaity = uidValidaity;
	}

	private long uidValidaity;
	@Getter @Setter
	IMAPServiceProviderType imapServiceProviderType;

	public enum IMAPServiceProviderType {
		DEFAULT,
		GMAIL,
		MICROSOFT_OFFICE_365,
		OTHERS,
		;
	}

}
