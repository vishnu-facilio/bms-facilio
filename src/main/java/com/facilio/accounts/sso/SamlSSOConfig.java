package com.facilio.accounts.sso;

public class SamlSSOConfig extends SSOConfig {
	
	private String entityId;
	private String loginUrl;
	private String logoutUrl;
	private String passwordUrl;
	private String certificate;
	
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getLoginUrl() {
		return loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	public String getPasswordUrl() {
		return passwordUrl;
	}
	public void setPasswordUrl(String passwordUrl) {
		this.passwordUrl = passwordUrl;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
}
