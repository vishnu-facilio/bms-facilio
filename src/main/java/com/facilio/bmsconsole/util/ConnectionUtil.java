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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionContext.State;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ConnectionUtil {
	
	
	public static final int CONNECTION_TIMEOUT_IN_SEC = 60000;		//60 sec
	
	public static final String CODE_STRING = "code";
	public static final String CLIENT_ID_STRING = "client_id";
	public static final String CLIENT_SECRET_STRING = "client_secret";
	public static final String REDIRECT_URI_STRING = "redirect_uri";	
	public static final String GRANT_TYPE_STRING = "grant_type";
	public static final String ACCESS_TYPE_STRING = "access_type";
	
	public static final String ACCESS_TYPE_OFFLINE = "offline";
		
	
	public static final String ACCESS_TOKEN_STRING = "access_token";
	public static final String EXPIRES_IN_STRING = "expires_in";
	public static final String REFRESH_TOKEN_STRING = "refresh_token";
	
	public static final String GRANT_TYPE_AUTH_TOKEN = "authorization_code";
	
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	
	
	public static final String EQUALS = "=";
	public static final String QUERY_STRING_SEPERATOR = "?";
	public static final String PARAM_SEPERATOR = "&";
	
	public static String getUrlResult(ConnectionContext connectionContext,String urlString,Map<String,String> params,HttpMethod method) throws Exception {
		
		validateConnection(connectionContext);
		
		params = params == null ? new HashMap<>() : params;
		
		params.put(ACCESS_TOKEN_STRING, connectionContext.getAccessToken());
		
		return getUrlResult(urlString, params, method);
	}
	
	private static void validateConnection(ConnectionContext connectionContext) throws Exception {
		
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
				
				String res = getUrlResult(url, params, HttpMethod.POST);
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
				
				String res = getUrlResult(url, params, HttpMethod.POST);
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

	private static String getUrlResult(String urlString,Map<String,String> params,HttpMethod method) throws Exception {
		
		HttpsURLConnection conn = null;
		if(method == HttpMethod.GET) {
			conn  = handleGetConnection(urlString,params);
		}
		else if(method == HttpMethod.POST) {
			conn  = handlePostConnection(urlString,params);
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
	
	private static HttpsURLConnection handlePostConnection(String urlString, Map<String, String> params) throws Exception {
		
		URL url = new URL(urlString);
		
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(HttpMethod.POST.name());
		conn.setConnectTimeout(CONNECTION_TIMEOUT_IN_SEC);
		
		if(params != null && !params.isEmpty()) {
			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			
			for(String key :params.keySet()) {
				paramsList.add(new BasicNameValuePair(key, params.get(key)));
			}

			OutputStream os	= conn.getOutputStream();
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, DEFAULT_CHARSET_NAME));
			
			writer.write(getQuery(paramsList));
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
		
		URL url = new URL(urlString+QUERY_STRING_SEPERATOR+queryString);
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

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
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
			return FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
		}
		return null;
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
			return FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
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

	private static void fillDefaultfields(ConnectionContext connectionContext) {
		
//		connectionContext.setSysModifiedBy(AccountUtil.getCurrentUser());
		connectionContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());
	}
}
