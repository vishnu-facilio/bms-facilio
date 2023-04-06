package com.facilio.bmsconsole.context;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.facilio.db.transaction.NewTransactionService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectionContext.State;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.time.DateTimeUtil;

public enum GrantType implements GrantTypeInterface{

	AUTHORIZATION_CODE("Authorization Code","authorization_code") {

		@Override
		public void updateAuthToken(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			if(connectionContext.getStateEnum() == State.AUTHORIZED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

				if(connectionContext.getRefreshToken() != null) {
					NewTransactionService.newTransaction(()->ConnectionUtil.updateAuthTokenByRefreshToken(connectionContext));
				}
				else {
					String url = connectionContext.getAccessTokenUrl();

					Map<String,String> params = new HashMap<>();
					
					Map<String,String> headerParams = new HashMap<>();

					params.put(ConnectionUtil.CODE_STRING, connectionContext.getAuthCode());
					
					if(connectionContext.getAccessTokenSetting() > 0 && connectionContext.getAccessTokenSetting() == ConnectionContext.Access_Token_Setting.CLIENT_DETAILS_ENCODED_AS_AUTH_PARAM.getValue()) {
						
						headerParams.put(ConnectionUtil.AUTHORIZATION_STRING, "Basic " + Base64.getEncoder().encodeToString(new String(connectionContext.getClientId() + ":" + connectionContext.getClientSecretId()).getBytes()));
					}
					else {
						params.put(ConnectionUtil.CLIENT_ID_STRING, connectionContext.getClientId());
						params.put(ConnectionUtil.CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
					}
					
					params.put(ConnectionUtil.GRANT_TYPE_STRING, this.getLinkName());
					params.put(ConnectionUtil.REDIRECT_URI_STRING, connectionContext.getCallBackURL());
					params.put(ConnectionUtil.ACCESS_TYPE_STRING, ConnectionUtil.ACCESS_TYPE_OFFLINE);
					
//					if(connectionContext.getScope() != null) {
//						params.put(ConnectionUtil.SCOPE_TYPE_STRING, connectionContext.getScope());
//					}
					if(connectionContext.getResource() != null) {
						params.put(ConnectionUtil.RESOURCE_STRING, connectionContext.getResource());
					}
					if(connectionContext.getAudience() != null) {
						params.put(ConnectionUtil.AUDIENCE_STRING, connectionContext.getAudience());
					}
					if(connectionContext.isoAuthParamInHeader()) {
						headerParams.putAll(params);
						params = null;
					}

					String res = ConnectionUtil.getUrlResult(url, params, HttpMethod.POST,headerParams,null,null);

					ConnectionUtil.parseJsonAndUpdateConnection(res, connectionContext);
				}
			}
			else {
				throw new Exception("Connection Not Yet Authorized");
			}
			
		}

		@Override
		public void validateConnection(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			switch(connectionContext.getStateEnum()) {
			case CREATED:
			case CLIENT_ID_MAPPED:
					throw new Exception("Connection Not Yet Authorized");
			case AUTHORIZED:
				updateAuthToken(connectionContext);
				break;

			case AUTH_TOKEN_GENERATED:
					if(connectionContext.getRefreshTokenExpiryTime() > 0 && connectionContext.getRefreshTokenExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						ConnectionUtil.invalidateConnection(connectionContext);
						updateAuthToken(connectionContext);
					}
					else if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						updateAuthToken(connectionContext);
					}
				break;

			case DISABLED:
				break;
			}
			
		}
		
	},
	CLIENT_CREDENTIALS("Client Credentials","client_credentials") {

		@Override
		public void updateAuthToken(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			
			if(connectionContext.getStateEnum() == State.CLIENT_ID_MAPPED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

				if(connectionContext.getRefreshToken() != null) {
					ConnectionUtil.updateAuthTokenByRefreshToken(connectionContext);
				}
				else {
					String url = connectionContext.getAccessTokenUrl();

					Map<String,String> params = new HashMap<>();
					Map<String,String> headerParams = new HashMap<>();
					
					params.put(ConnectionUtil.CLIENT_ID_STRING, connectionContext.getClientId());
					params.put(ConnectionUtil.CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
					
					params.put(ConnectionUtil.GRANT_TYPE_STRING, this.getLinkName());
					params.put(ConnectionUtil.REDIRECT_URI_STRING, connectionContext.getCallBackURL());
					params.put(ConnectionUtil.ACCESS_TYPE_STRING, ConnectionUtil.ACCESS_TYPE_OFFLINE);
					
					if(connectionContext.getScope() != null) {
						params.put(ConnectionUtil.SCOPE_TYPE_STRING, connectionContext.getScope());
					}
					if(connectionContext.getResource() != null) {
						params.put(ConnectionUtil.RESOURCE_STRING, connectionContext.getResource());
					}
					if(connectionContext.getAudience() != null) {
						params.put(ConnectionUtil.AUDIENCE_STRING, connectionContext.getAudience());
					}
					
					if(connectionContext.isoAuthParamInHeader()) {
						headerParams.putAll(params);
						params = null;
					}

					String res = ConnectionUtil.getUrlResult(url, params, HttpMethod.POST,headerParams,null,null);
					
					ConnectionUtil.parseJsonAndUpdateConnection(res, connectionContext);
				}
			}
			else {
				throw new Exception("Connection Not Yet Authorized");
			}
		}

		@Override
		public void validateConnection(ConnectionContext connectionContext) throws Exception {

			switch(connectionContext.getStateEnum()) {
			case CREATED:
				throw new Exception("Client ID yet to be mapped");
			case CLIENT_ID_MAPPED:
				updateAuthToken(connectionContext);
				break;

			case AUTH_TOKEN_GENERATED:
					if(connectionContext.getRefreshTokenExpiryTime() > 0 && connectionContext.getRefreshTokenExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						ConnectionUtil.invalidateConnection(connectionContext);
						updateAuthToken(connectionContext);
					}
					else if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						updateAuthToken(connectionContext);
					}
				break;

			case DISABLED:
				break;
			}
		}
		
	},
	PASSWORD_CREDENTIALS("Password Credentials","password") {

		@Override
		public void updateAuthToken(ConnectionContext connectionContext) throws Exception {
			
			if(connectionContext.getStateEnum() == State.CREATED || connectionContext.getStateEnum() == State.CLIENT_ID_MAPPED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

				if(connectionContext.getRefreshToken() != null) {
					ConnectionUtil.updateAuthTokenByRefreshToken(connectionContext);
				}
				else {
					String url = connectionContext.getAccessTokenUrl();

					Map<String,String> params = new HashMap<>();
					Map<String,String> headerParams = new HashMap<>();
					
					params.put(ConnectionUtil.USER_NAME_STRING, connectionContext.getUserName());
					params.put(ConnectionUtil.PASSWORD_STRING, connectionContext.getPassword());
					
					if(connectionContext.getClientId() != null) {
						params.put(ConnectionUtil.CLIENT_ID_STRING, connectionContext.getClientId());
					}
					if(connectionContext.getClientSecretId() != null) {
						params.put(ConnectionUtil.CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
					}
					
					params.put(ConnectionUtil.GRANT_TYPE_STRING, this.getLinkName());
					params.put(ConnectionUtil.REDIRECT_URI_STRING, connectionContext.getCallBackURL());
					params.put(ConnectionUtil.ACCESS_TYPE_STRING, ConnectionUtil.ACCESS_TYPE_OFFLINE);
					
					if(connectionContext.getScope() != null) {
						params.put(ConnectionUtil.SCOPE_TYPE_STRING, connectionContext.getScope());
					}
					if(connectionContext.getResource() != null) {
						params.put(ConnectionUtil.RESOURCE_STRING, connectionContext.getResource());
					}
					if(connectionContext.getAudience() != null) {
						params.put(ConnectionUtil.AUDIENCE_STRING, connectionContext.getAudience());
					}
					
					if(connectionContext.isoAuthParamInHeader()) {
						headerParams.putAll(params);
						params = null;
					}

					String res = ConnectionUtil.getUrlResult(url, params, HttpMethod.POST,headerParams,null,null);
					
					ConnectionUtil.parseJsonAndUpdateConnection(res, connectionContext);
				}
			}
			else {
				throw new Exception("Connection Not Yet Authorized");
			}
		}

		@Override
		public void validateConnection(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			
			switch(connectionContext.getStateEnum()) {
			case CREATED:
			case CLIENT_ID_MAPPED:
				updateAuthToken(connectionContext);
				break;

			case AUTH_TOKEN_GENERATED:
					if(connectionContext.getRefreshTokenExpiryTime() > 0 && connectionContext.getRefreshTokenExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						ConnectionUtil.invalidateConnection(connectionContext);
						updateAuthToken(connectionContext);
					}
					else if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						updateAuthToken(connectionContext);
					}
				break;

			case DISABLED:
				break;
			}
		}
		
	},
	IMPLICIT("Implicit","") {			// mostly not using now

		@Override
		public void updateAuthToken(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			
			if(connectionContext.getStateEnum() == State.CREATED || connectionContext.getStateEnum() == State.CLIENT_ID_MAPPED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

				if(connectionContext.getRefreshToken() != null) {
					ConnectionUtil.updateAuthTokenByRefreshToken(connectionContext);
				}
				else {
					String url = connectionContext.getAccessTokenUrl();

					Map<String,String> params = new HashMap<>();
					Map<String,String> headerParams = new HashMap<>();
					
					
					params.put(ConnectionUtil.CLIENT_ID_STRING, connectionContext.getClientId());
					
					params.put(ConnectionUtil.RESPONSE_TYPE_STRING, "token");
					params.put(ConnectionUtil.REDIRECT_URI_STRING, connectionContext.getCallBackURL());
					
					if(connectionContext.getScope() != null) {
						params.put(ConnectionUtil.SCOPE_TYPE_STRING, connectionContext.getScope());
					}
					
					if(connectionContext.isoAuthParamInHeader()) {
						headerParams.putAll(params);
						params = null;
					}

					String res = ConnectionUtil.getUrlResult(url, params, HttpMethod.POST,headerParams,null,null);
					
					ConnectionUtil.parseJsonAndUpdateConnection(res, connectionContext);
				}
			}
			else {
				throw new Exception("Connection Not Yet Authorized");
			}

		}

		@Override
		public void validateConnection(ConnectionContext connectionContext) throws Exception {
			// TODO Auto-generated method stub
			switch(connectionContext.getStateEnum()) {
			case CREATED:
			case CLIENT_ID_MAPPED:
				updateAuthToken(connectionContext);
				break;

			case AUTH_TOKEN_GENERATED:
					if(connectionContext.getRefreshTokenExpiryTime() > 0 && connectionContext.getRefreshTokenExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						ConnectionUtil.invalidateConnection(connectionContext);
						updateAuthToken(connectionContext);
					}
					else if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
						updateAuthToken(connectionContext);
					}
				break;

			case DISABLED:
				break;
			}
		}
		
	},
	
	;
	
	String name;
	String linkName;
	private GrantType(String name,String linkName) {
		this.name = name;
		this.linkName = linkName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLinkName() {
		return linkName;
	}
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static GrantType valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
