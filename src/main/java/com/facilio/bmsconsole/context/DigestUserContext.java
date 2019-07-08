package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;

public class DigestUserContext {

	private DigestConfigContext digestConfig;
	private User user;
	public DigestConfigContext getDigestConfig() {
		return digestConfig;
	}
	public void setDigestConfig(DigestConfigContext digestConfig) {
		this.digestConfig = digestConfig;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
