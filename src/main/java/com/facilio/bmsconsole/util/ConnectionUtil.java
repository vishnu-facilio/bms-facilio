package com.facilio.bmsconsole.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.AddConnectionCommand;
import com.facilio.bmsconsole.context.ConnectionApiContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionContext.State;
import com.facilio.bmsconsole.context.ConnectionParamContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.CryptoUtils;
import com.facilio.time.DateTimeUtil;

public class ConnectionUtil {


	public static final int CONNECTION_TIMEOUT_IN_SEC = 60000;		//60 sec

	public static final String CODE_STRING = "code";
	public static final String CLIENT_ID_STRING = "client_id";
	public static final String CLIENT_SECRET_STRING = "client_secret";
	public static final String REDIRECT_URI_STRING = "redirect_uri";
	public static final String GRANT_TYPE_STRING = "grant_type";
	public static final String ACCESS_TYPE_STRING = "access_type";
	public static final String SCOPE_TYPE_STRING = "scope";
	public static final String RESPONSE_TYPE_STRING = "response_type";
	public static final String ACCESS_TYPE_OFFLINE = "offline";
	public static final String ACCESS_TOKEN_STRING = "access_token";
	public static final String EXPIRES_IN_STRING = "expires_in";
	public static final String REFRESH_TOKEN_STRING = "refresh_token";
	public static final String GRANT_TYPE_AUTH_TOKEN = "authorization_code";
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	public static final String SECRET_STATE = "state";

	public static final String EQUALS = "=";
	public static final String QUERY_STRING_SEPERATOR = "?";
	public static final String PARAM_SEPERATOR = "&";

	public static String getUrlResult(ConnectionContext connectionContext,String urlString,Map<String,String> params,HttpMethod method,String bodyString,String bodyType) throws Exception {

		params = params == null ? new HashMap<>() : params;

		Map<String,String> headerParam = new HashMap<>();

		switch(connectionContext.getAuthTypeEnum()) {

			case OAUTH2:
				validateOauth2Connection(connectionContext);
				if(urlString.contains(QUERY_STRING_SEPERATOR)) {
					urlString = urlString + PARAM_SEPERATOR;
				}
				else {
					urlString = urlString + QUERY_STRING_SEPERATOR;
				}
				urlString = urlString + ACCESS_TOKEN_STRING + EQUALS + connectionContext.getAccessToken();
				break;
			case BASIC:
				if(connectionContext.getConnectionParams() != null) {
					for(ConnectionParamContext connectionParams : connectionContext.getConnectionParams()) {
						if (connectionParams.isProperty())
							headerParam.put(connectionParams.getKey(), connectionParams.getValue());
						else
							params.put(connectionParams.getKey(),connectionParams.getValue());
					}
				}
				break;
		}
		return getUrlResult(urlString, params, method,headerParam,bodyString,bodyType);
	}

	private static void validateOauth2Connection(ConnectionContext connectionContext) throws Exception {

		switch(connectionContext.getStateEnum()) {
		case CREATED:
		case CLIENT_ID_MAPPED:
				throw new Exception("Connection Not Yet Authorized");
		case AUTHORIZED:
				getAuthToken(connectionContext);
			break;

		case AUTH_TOKEN_GENERATED:
				if(connectionContext.getExpiryTime() <= DateTimeUtil.getCurrenTime()) {
					getAuthToken(connectionContext);
				}
			break;

		case DISABLED:
			break;
		}
	}

	private static void getAuthToken(ConnectionContext connectionContext) throws Exception {

		if(connectionContext.getStateEnum() == State.AUTHORIZED || connectionContext.getStateEnum() == State.AUTH_TOKEN_GENERATED) {

			if(connectionContext.getStateEnum() == State.AUTHORIZED) {
				String url = connectionContext.getAccessTokenUrl();

				Map<String,String> params = new HashMap<>();

				params.put(CODE_STRING, connectionContext.getAuthCode());
				params.put(CLIENT_ID_STRING, connectionContext.getClientId());
				params.put(CLIENT_SECRET_STRING, connectionContext.getClientSecretId());
				params.put(GRANT_TYPE_STRING, GRANT_TYPE_AUTH_TOKEN);
				params.put(REDIRECT_URI_STRING, connectionContext.getCallBackURL());
				params.put(ACCESS_TYPE_STRING, ACCESS_TYPE_OFFLINE);
				params.put(SECRET_STATE, connectionContext.getSecretStateKey());

				String res = getUrlResult(url, params, HttpMethod.POST,null,null,null);
				JSONParser parser = new JSONParser();
				JSONObject resultJson = (JSONObject) parser.parse(res);

				System.out.println("res ------ "+resultJson);

				if(resultJson.containsKey(ACCESS_TOKEN_STRING) && resultJson.containsKey(EXPIRES_IN_STRING) && resultJson.containsKey(REFRESH_TOKEN_STRING)) {
					connectionContext.setAccessToken((String)resultJson.get(ACCESS_TOKEN_STRING));
					connectionContext.setRefreshToken((String)resultJson.get(REFRESH_TOKEN_STRING));

					long expireTimeInSec = (long) resultJson.get(EXPIRES_IN_STRING);

					expireTimeInSec = expireTimeInSec - 60;

					connectionContext.setExpiryTime(DateTimeUtil.getCurrenTime() + (expireTimeInSec * 1000));

					updateConnectionContext(connectionContext);
				}
				else {
					//throw new Exception("Required Param is Missing in Response - "+resultJson.toJSONString());
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

				System.out.println("res ------ "+resultJson);

				if(resultJson.containsKey(ACCESS_TOKEN_STRING) && resultJson.containsKey(EXPIRES_IN_STRING)) {
					connectionContext.setAccessToken((String)resultJson.get(ACCESS_TOKEN_STRING));

					long expireTimeInSec = (long) resultJson.get(EXPIRES_IN_STRING);

					expireTimeInSec = expireTimeInSec - 60;

					connectionContext.setExpiryTime(DateTimeUtil.getCurrenTime() + (expireTimeInSec * 1000));

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

		HttpsURLConnection conn = null;
		if(method == HttpMethod.GET) {
			conn  = handleGetConnection(urlString,params);
		}
		else if(method == HttpMethod.POST) {
			conn  = handlePostConnection(urlString,params,bodyString,bodyType);
		}



		if(headerParam != null && !headerParam.isEmpty()) {
			for(String key : headerParam.keySet()) {
				conn.setRequestProperty(key, headerParam.get(key));
			}
		}
		conn.connect();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),Charset.forName(DEFAULT_CHARSET_NAME)));
		}
		catch(IOException e) {
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream(),Charset.forName(DEFAULT_CHARSET_NAME)));
		}

  	   	String input;
  	   	StringBuffer output = new StringBuffer();
  	   	while ((input = br.readLine()) != null){
  	   		output.append(input);
  	   	}
  	   	conn.disconnect();
  	   	return output.toString();
	}

	private static HttpsURLConnection handlePostConnection(String urlString, Map<String, String> params,String bodyString,String bodyType) throws Exception {

		URL url = new URL(urlString);

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(HttpMethod.POST.name());
		conn.setConnectTimeout(CONNECTION_TIMEOUT_IN_SEC);

		String actualBodyString = null;
		if(params != null && !params.isEmpty()) {

			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

			for(String key :params.keySet()) {
				paramsList.add(new BasicNameValuePair(key, params.get(key)));
			}

			actualBodyString = getQuery(paramsList);
		}
		else if(bodyString != null && bodyType != null) {
			conn.setRequestProperty("Content-Type", bodyType);
			actualBodyString = bodyString;
		}
		if(actualBodyString != null) {

			OutputStream os	= conn.getOutputStream();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, DEFAULT_CHARSET_NAME));

			writer.write(actualBodyString);
			writer.flush();
			writer.close();
			os.close();
		}
		return conn;
	}

	private static HttpsURLConnection handleGetConnection(String urlString, Map<String, String> params) throws Exception {

		String queryString = "";
		if(params != null && !params.isEmpty()) {

			StringBuilder queryStringBuilder = new StringBuilder();
			for(String key :params.keySet()) {
				queryStringBuilder.append(key);
				queryStringBuilder.append(EQUALS);
				queryStringBuilder.append(params.get(key));
				queryStringBuilder.append(PARAM_SEPERATOR);
			}
			queryString = queryStringBuilder.subSequence(0, queryStringBuilder.length()-1).toString();
		}
		if(queryString != null && !queryString.isEmpty()) {

			if(urlString.contains(QUERY_STRING_SEPERATOR)) {
				urlString = urlString + PARAM_SEPERATOR;
			}
			else {
				urlString = urlString + QUERY_STRING_SEPERATOR;
			}
			urlString = urlString+queryString;
		}

		URL url = new URL(null,urlString,new sun.net.www.protocol.https.Handler());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(HttpMethod.GET.name());
		conn.setConnectTimeout(CONNECTION_TIMEOUT_IN_SEC);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		return conn;
	}

	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        String value = pair.getValue();
	        value = value.replaceAll("<br>", "\n");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(value, "UTF-8"));
	    }

	    return result.toString();
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
		if(connectionContext.getAccessToken() != null) {
			connectionContext.setState(ConnectionContext.State.AUTH_TOKEN_GENERATED.getValue());
		}
		else if(connectionContext.getAuthCode() != null) {
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
