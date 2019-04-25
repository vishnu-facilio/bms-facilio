package com.facilio.bmsconsole.context;

public class ConnectionContext {
	long id = -1l;
	long orgId = -1l;
	AuthType authType;
	
	String clientId;
	String clientSecretId;
	String authorizeUrl;
	String accessTokenUrl;
	String refreshTokenUrl;
	String revokeTokenUrl;
	String authToken;
	String refreshToken;
	
	long expiryTime = -1l;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public int getAuthType() {
		if(authType != null) {
			return authType.getValue();
		}
		return -1;
	}

	public void setAuthType(int authType) {
		if(authType > 0) {
			this.authType = AuthType.valueOf(authType);
		}
	}
	
	public AuthType getAuthTypeEnum() {
		return authType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecretId() {
		return clientSecretId;
	}

	public void setClientSecretId(String clientSecretId) {
		this.clientSecretId = clientSecretId;
	}

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getRefreshTokenUrl() {
		return refreshTokenUrl;
	}

	public void setRefreshTokenUrl(String refreshTokenUrl) {
		this.refreshTokenUrl = refreshTokenUrl;
	}

	public String getRevokeTokenUrl() {
		return revokeTokenUrl;
	}

	public void setRevokeTokenUrl(String revokeTokenUrl) {
		this.revokeTokenUrl = revokeTokenUrl;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public enum AuthType {
		OAUTH2,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static AuthType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
