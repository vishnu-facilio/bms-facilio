package com.facilio.bmsconsole.util;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.HttpMethod;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectionApiContext;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionContext.State;
import com.facilio.bmsconsole.context.ConnectionParamContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.CryptoUtils;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.time.DateTimeUtil;

public class ConnectionUtil {


	public static final int CONNECTION_TIMEOUT_IN_SEC = 60000;		//60 sec

	public static final String CODE_STRING = "code";
	public static final String CLIENT_ID_STRING = "client_id";
	public static final String AUTHORIZATION_STRING = "Authorization";
	public static final String CLIENT_SECRET_STRING = "client_secret";
	public static final String REDIRECT_URI_STRING = "redirect_uri";
	public static final String GRANT_TYPE_STRING = "grant_type";
	public static final String ACCESS_TYPE_STRING = "access_type";
	public static final String SCOPE_TYPE_STRING = "scope";
	public static final String RESPONSE_TYPE_STRING = "response_type";
	public static final String ACCESS_TYPE_OFFLINE = "offline";
	public static final String ACCESS_TOKEN_STRING = "access_token";
	public static final String EXPIRES_IN_STRING = "expires_in";
	public static final String REFRESH_TOKEN_EXPIRES_IN_STRING = "x_refresh_token_expires_in";
	public static final String REFRESH_TOKEN_STRING = "refresh_token";
	public static final String GRANT_TYPE_AUTH_TOKEN = "authorization_code";
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	public static final String SECRET_STATE = "state";
	
	public static final long MAX_TIME = 4102425000000l;

	public static final String EQUALS = "=";
	public static final String QUERY_STRING_SEPERATOR = "?";
	public static final String PARAM_SEPERATOR = "&";
	
	public static String getUrlResult(ConnectionContext connectionContext,String urlString,Map<String,String> params,HttpMethod method,String bodyString,String bodyType,Map<String,String> headerParam) throws Exception {

		params = params == null ? new HashMap<>() : params;

		headerParam = headerParam == null ? new HashMap<>() : headerParam;

		switch(connectionContext.getAuthTypeEnum()) {

			case OAUTH2:
				validateOauth2Connection(connectionContext);
				headerParam.put("Authorization", "Bearer "+connectionContext.getAccessToken());
				
				break;
			case BASIC:
				if(connectionContext.getConnectionParams() != null) {
					for(ConnectionParamContext connectionParams : connectionContext.getConnectionParams()) {
						if (connectionParams.isProperty()) {
							headerParam.put(connectionParams.getKey(), connectionParams.getValue());
						}
						else {
							params.put(connectionParams.getKey(),connectionParams.getValue());
						}
					}
				}
				break;
		}
		return getUrlResult(urlString, params, method,headerParam,bodyString,bodyType);
	}

	public static void validateOauth2Connection(ConnectionContext connectionContext) throws Exception {

		switch(connectionContext.getStateEnum()) {
		case CREATED:
		case CLIENT_ID_MAPPED:
				throw new Exception("Connection Not Yet Authorized");
		case AUTHORIZED:
				getAuthToken(connectionContext);
			break;

		case AUTH_TOKEN_GENERATED:
				if(connectionContext.getRefreshTokenExpiryTime() > 0 && connectionContext.getRefreshTokenExpiryTime() <= DateTimeUtil.getCurrenTime()) {
					invalidateConnection(connectionContext);
					validateOauth2Connection(connectionContext);
				}
				else if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
					getAuthToken(connectionContext);
				}
			break;

		case DISABLED:
			break;
		}
	}
	
	private static void invalidateConnection(ConnectionContext connectionContext) throws Exception {
		
		connectionContext.setState(ConnectionContext.State.CLIENT_ID_MAPPED.getValue());
		connectionContext.setAuthCode("");
		connectionContext.setAccessToken("");
		connectionContext.setRefreshToken("");
		connectionContext.setExpiryTime(-99);
		
		NewTransactionService.newTransaction(() ->  ConnectionUtil.updateConnectionContext(connectionContext));
	}

	private static void getAuthToken(ConnectionContext connectionContext) throws Exception {

		if(connectionContext.getStateEnum() == State.AUTHORIZED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

			if(connectionContext.getStateEnum() == State.AUTHORIZED) {
				String url = connectionContext.getAccessTokenUrl();

				Map<String,String> params = new HashMap<>();
				
				Map<String,String> headerParams = new HashMap<>();

				params.put(CODE_STRING, connectionContext.getAuthCode());
				
				if(connectionContext.getAccessTokenSetting() > 0 && connectionContext.getAccessTokenSetting() == ConnectionContext.Access_Token_Setting.CLIENT_DETAILS_ENCODED_AS_AUTH_PARAM.getValue()) {
					
					headerParams.put(AUTHORIZATION_STRING, "Basic " + Base64.getEncoder().encodeToString(new String(connectionContext.getClientId() + ":" + connectionContext.getClientSecretId()).getBytes()));
				}
				else {
					params.put(CLIENT_ID_STRING, connectionContext.getClientId());
					params.put(CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
				}
				
				params.put(GRANT_TYPE_STRING, GRANT_TYPE_AUTH_TOKEN);
				params.put(REDIRECT_URI_STRING, connectionContext.getCallBackURL());
				params.put(ACCESS_TYPE_STRING, ACCESS_TYPE_OFFLINE);

				String res = getUrlResult(url, params, HttpMethod.POST,headerParams,null,null);
				JSONParser parser = new JSONParser();
				JSONObject resultJson = (JSONObject) parser.parse(res);

				if(resultJson.containsKey(ACCESS_TOKEN_STRING) && resultJson.containsKey(EXPIRES_IN_STRING) && resultJson.containsKey(REFRESH_TOKEN_STRING)) {
					connectionContext.setAccessToken((String)resultJson.get(ACCESS_TOKEN_STRING));
					connectionContext.setRefreshToken((String)resultJson.get(REFRESH_TOKEN_STRING));

					long expireTimeInSec = (long) resultJson.get(EXPIRES_IN_STRING);

					expireTimeInSec = expireTimeInSec - 60;

					connectionContext.setExpiryTime(DateTimeUtil.getCurrenTime() + (expireTimeInSec * 1000));
					
					if(resultJson.containsKey(REFRESH_TOKEN_EXPIRES_IN_STRING)) {
						
						long refreshTokenExpireTimeInSec = (long) resultJson.get(REFRESH_TOKEN_EXPIRES_IN_STRING);
						
						refreshTokenExpireTimeInSec = refreshTokenExpireTimeInSec - 6000;
						
						connectionContext.setRefreshTokenExpiryTime(DateTimeUtil.getCurrenTime() + (refreshTokenExpireTimeInSec * 1000));
					}

					updateConnectionContext(connectionContext);
				}
				else {
					throw new Exception("Required Param is Missing in Response - "+resultJson.toJSONString());
				}
			}

			else if (connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {
				String url = connectionContext.getRefreshTokenUrl();

				Map<String,String> params = new HashMap<>();

				params.put(REFRESH_TOKEN_STRING, connectionContext.getRefreshToken());
				params.put(CLIENT_ID_STRING, connectionContext.getClientId());
				params.put(CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
				params.put(GRANT_TYPE_STRING, REFRESH_TOKEN_STRING);
				params.put(SECRET_STATE, connectionContext.getSecretStateKey());

				String res = getUrlResult(url, params, HttpMethod.POST,null,null,null);
				JSONParser parser = new JSONParser();
				JSONObject resultJson = (JSONObject) parser.parse(res);

				if(resultJson.containsKey(ACCESS_TOKEN_STRING)) {
					connectionContext.setAccessToken((String)resultJson.get(ACCESS_TOKEN_STRING));
					
					if(resultJson.containsKey(EXPIRES_IN_STRING)) {
						long expireTimeInSec = (long) resultJson.get(EXPIRES_IN_STRING);

						expireTimeInSec = expireTimeInSec - 60;

						connectionContext.setExpiryTime(DateTimeUtil.getCurrenTime() + (expireTimeInSec * 1000));

					}
					else {
						connectionContext.setExpiryTime(MAX_TIME);
					}
					
					updateConnectionContext(connectionContext);
				}
				else {
					throw new Exception("Required Param is Missing in Response - "+resultJson.toJSONString());
				}
			}
		}
		else {
			throw new Exception("Connection Not Yet Authorized");
		}
	}

	private static String getUrlResult(String urlString,Map<String,String> params,HttpMethod method,Map<String,String> headerParam,String bodyString,String bodyType) throws Exception {

		
		if(method == HttpMethod.GET) {
			return FacilioHttpUtils.doHttpGet(urlString, headerParam, params);
		}
		else if(method == HttpMethod.POST) {
			if(bodyString != null && bodyType != null) {
				headerParam = headerParam == null ? new HashMap<>() : headerParam; 
				headerParam.put("Content-Type", bodyType);
			}
			else if (params != null && !params.isEmpty()) {
				headerParam = headerParam == null ? new HashMap<>() : headerParam; 
				headerParam.put("Content-Type", "application/x-www-form-urlencoded");
			}
			return FacilioHttpUtils.doHttpPost(urlString, headerParam, params,bodyString);
		}
		return null;
	}

	public static ConnectionContext getConnection(long connectionId) throws Exception {

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getIdCondition(connectionId, ModuleFactory.getConnectionModule()));

		List<Map<String, Object>> props = select.get();

		if(props != null && !props.isEmpty()) {
			ConnectionContext connectionContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
			connectionContext.setConnectionParams(getConnectionParams(connectionContext.getId()));
			return connectionContext;
		}
		return null;
	}
	
	public static ConnectionContext getConnectionFromSecretStateString(String secretStateKey) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getConnectionFields());
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("secretStateKey"), secretStateKey, StringOperators.IS));

		List<Map<String, Object>> props = select.get();

		if(props != null && !props.isEmpty()) {
			ConnectionContext connectionContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
			connectionContext.setConnectionParams(getConnectionParams(connectionContext.getId()));
			return connectionContext;
		}
		return null;
	}

	public static List<ConnectionParamContext> getConnectionParams(long connectionId) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getConnectionParamFields());
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionParamsModule().getTableName())
				.select(FieldFactory.getConnectionParamFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("connectionId"), connectionId+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = select.get();
		List<ConnectionParamContext> list = new ArrayList<>();
		for (Map<String, Object> prop:props){
			ConnectionParamContext connectionParamContext = new ConnectionParamContext();
			connectionParamContext.setConnectionId((Long) prop.get("connectionId"));
			connectionParamContext.setId((Long) prop.get("id"));
			connectionParamContext.setOrgId((Long)prop.get("orgId"));
			connectionParamContext.setKey((String)prop.get("key"));
			connectionParamContext.setValue((String)prop.get("value"));
			connectionParamContext.setProperty((Boolean.parseBoolean(prop.get("isProperty").toString())));
			list.add(connectionParamContext);
		}
		return list;
	}

	public static ConnectionContext getConnection(String connectionName) throws Exception {


		List<FacilioField> fields = FieldFactory.getConnectionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), connectionName, StringOperators.IS));

		List<Map<String, Object>> props = select.get();

		if(props != null && !props.isEmpty()) {
			ConnectionContext connectionContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
			connectionContext.setConnectionParams(getConnectionParams(connectionContext.getId()));
			return connectionContext;
		}
		return null;
	}

	public static List<ConnectionContext> getAllConnections() throws Exception {

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields());

		List<Map<String, Object>> props = select.get();

		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, ConnectionContext.class);
		}
		return null;
	}

	public static  void updateConnectionContext(ConnectionContext connectionContext) throws Exception {
		fillState(connectionContext);
		fillDefaultfields(connectionContext);

		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getIdCondition(connectionContext.getId(), ModuleFactory.getConnectionModule()));

		updateRecordBuilder.update(FieldUtil.getAsProperties(connectionContext));

	}

	private static void fillState(ConnectionContext connectionContext) {
		if(connectionContext.getAccessToken() != null && !connectionContext.getAccessToken().isEmpty()) {
			connectionContext.setState(ConnectionContext.State.AUTH_TOKEN_GENERATED.getValue());
		}
		else if(connectionContext.getAuthCode() != null && !connectionContext.getAuthCode().isEmpty()) {
			connectionContext.setState(ConnectionContext.State.AUTHORIZED.getValue());
		}
		else if(connectionContext.getClientId() != null && connectionContext.getClientSecretId() != null) {
			connectionContext.setState(ConnectionContext.State.CLIENT_ID_MAPPED.getValue());
		}
	}
	
	public static void addConnection(ConnectionContext connectionContext) throws Exception {
		
		fillDefaultfields(connectionContext);
		
		Map<String, Object> prop = FieldUtil.getAsProperties(connectionContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.addRecord(prop);
		
		insert.save();
		
		connectionContext.setId((Long) prop.get("id"));
	}
	
	public static void addConnectionApi(ConnectionApiContext connectionApiContext) throws Exception {
		
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionApiModule().getTableName())
				.fields(FieldFactory.getConnectionApiFields());
		
		connectionApiContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(connectionApiContext);
		
		insert.addRecord(prop);
		
		insert.save();
		
		connectionApiContext.setId((Long) prop.get("id"));
	}
	
	public static void addConnectionParams(ConnectionParamContext connectionParamContext) throws Exception {
		
		GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionParamsModule().getTableName())
				.fields(FieldFactory.getConnectionParamFields());
		
		connectionParamContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(connectionParamContext);
		insert1.addRecord(prop);
		
		insert1.save();
		
		connectionParamContext.setId((Long) prop.get("id"));
	}

	private static void fillDefaultfields(ConnectionContext connectionContext) throws NoSuchAlgorithmException {

//		connectionContext.setSysModifiedBy(AccountUtil.getCurrentUser());
		connectionContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());
		if(connectionContext.getId() < 0) {
			connectionContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());
		}
		
		String key = CryptoUtils.hash256(""+DateTimeUtil.getCurrenTime()+Math.random());
		
		connectionContext.setSecretStateKey(key);
		
		connectionContext.setOrgId(AccountUtil.getCurrentOrg().getId());
	}

	public static ConnectionApiContext getConnectionApi(String name) throws Exception {

		List<FacilioField> fields = FieldFactory.getConnectionApiFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionApiModule().getTableName())
				.andCondition(CriteriaAPI.getNameCondition(name,ModuleFactory.getConnectionApiModule()))
				.select(FieldFactory.getConnectionApiFields());
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			ConnectionApiContext connectionApiContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectionApiContext.class);
			connectionApiContext.setConnectionContext(getConnection(connectionApiContext.getConnectionId()));
			return connectionApiContext;
		}
		return null;
	}
}
