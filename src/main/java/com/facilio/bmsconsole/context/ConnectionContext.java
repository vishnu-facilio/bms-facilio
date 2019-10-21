package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ConnectionUtil;

public class ConnectionContext {
	long id = -1l;
	long orgId = -1l;
	AuthType authType;
	ParamType paramType;
	State state;
	String name;
	String serviceName;
	String clientId;
	String clientSecretId;
	String scope;
	String authorizeUrl;
	String accessTokenUrl;
	String refreshTokenUrl;
	String revokeTokenUrl;
	String accessToken;
	String authCode;
	String refreshToken;
	String callBackURL;
	List<ConnectionParamContext> connectionParams;
	long expiryTime = -1l;
	
	public List<ConnectionParamContext> getConnectionParams() {
		return connectionParams;
	}
	public void setConnectionParams(List<ConnectionParamContext> connectionParams) {
		this.connectionParams = connectionParams;
	}
	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
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
	
	public int getParamType() {
		if(paramType != null) {
			return paramType.getValue();
		}
		return -1;
	}
	public void setParamType(int paramType) {
		if(paramType > 0) {
			this.paramType = ParamType.valueOf(paramType);
		}
	}

	public ParamType getParamTypeEnum() {
		return paramType;
	}
	
	public int getState() {
		if(state != null) {
			return state.getValue();
		}
		return -1;
	}

	public void setState(int state) {
		if(state > 0) {
			this.state = State.valueOf(state);
		}
	}
	
	public State getStateEnum() {
		return state;
	}
	
	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
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


	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
		BASIC
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
	
	public enum ParamType {
		QUERY_STRING,
		FORM_DATA,
		HEADER
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ParamType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public enum State {
		CREATED,
		CLIENT_ID_MAPPED,
		AUTHORIZED,
		AUTH_TOKEN_GENERATED,
		DISABLED,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static State valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	private long sysCreatedTime = -1;
	public long getSysCreatedTime() {
		return sysCreatedTime;
	}
	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	private long sysModifiedTime = -1;
	public long getSysModifiedTime() {
		return sysModifiedTime;
	}
	public void setSysModifiedTime(long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}

//	private User sysCreatedBy;
//	public User getSysCreatedBy() {
//		return sysCreatedBy;
//	}
//	public void setSysCreatedBy(User sysCreatedBy) {
//		this.sysCreatedBy = sysCreatedBy;
//	}

	private User sysModifiedBy;
	public User getSysModifiedBy() {
		return sysModifiedBy;
	}
	public void setSysModifiedBy(User sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getCallBackURL() {
		if(id > 0) {
			String protocol = "";
			if(FacilioProperties.isDevelopment()) {
				protocol = "http://";
			}
			else {
				protocol = "https://";
			}
			String domain = FacilioProperties.getAppDomain();
			callBackURL = protocol+ domain +"/api/v2/connection/"+id;
			return callBackURL;
		}
		return null;
	}
	
	public String getAuthenticationURL() {
		StringBuilder builder = new StringBuilder();
		if(authorizeUrl != null && clientId != null && getCallBackURL() != null && scope != null) {
			builder.append(authorizeUrl);
			builder.append(ConnectionUtil.QUERY_STRING_SEPERATOR);
			builder.append(ConnectionUtil.CLIENT_ID_STRING+ConnectionUtil.EQUALS+clientId);
			builder.append(ConnectionUtil.PARAM_SEPERATOR);
			
			builder.append(ConnectionUtil.REDIRECT_URI_STRING+ConnectionUtil.EQUALS+getCallBackURL());
			builder.append(ConnectionUtil.PARAM_SEPERATOR);
			
			builder.append(ConnectionUtil.SCOPE_TYPE_STRING+ConnectionUtil.EQUALS+scope);
			builder.append(ConnectionUtil.PARAM_SEPERATOR);
			
			builder.append(ConnectionUtil.ACCESS_TYPE_STRING+ConnectionUtil.EQUALS+ConnectionUtil.ACCESS_TYPE_OFFLINE);
			builder.append(ConnectionUtil.PARAM_SEPERATOR);
			
			builder.append(ConnectionUtil.RESPONSE_TYPE_STRING+ConnectionUtil.EQUALS+ConnectionUtil.CODE_STRING);
			
		}
		
		return builder.toString();
	}
	
	
	@Override
	public String toString() {
		return "ConnectionContext [id=" + id + ", orgId=" + orgId + ", authType=" + authType + ", state=" + state
				+ ", name=" + name + ", serviceName=" + serviceName + ", clientId=" + clientId + ", clientSecretId="
				+ clientSecretId + ", scope=" + scope + ", authorizeUrl=" + authorizeUrl + ", accessTokenUrl="
				+ accessTokenUrl + ", refreshTokenUrl=" + refreshTokenUrl + ", revokeTokenUrl=" + revokeTokenUrl
				+ ", accessToken=" + accessToken + ", authCode=" + authCode + ", refreshToken=" + refreshToken
				+ ", callBackURL=" + getCallBackURL() + ", expiryTime=" + expiryTime + ", sysCreatedTime=" + sysCreatedTime
				+ ", sysModifiedTime=" + sysModifiedTime + ", sysCreatedBy="  + ", sysModifiedBy="
				+ sysModifiedBy + "]";
	}
}
