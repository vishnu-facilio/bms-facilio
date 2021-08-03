package com.facilio.iam.accounts.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.iam.accounts.util.DCUtil;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;

import lombok.SneakyThrows;
import lombok.var;
import lombok.extern.log4j.Log4j;

@Log4j
public class IamClient {
    private static HttpClient httpclient = HttpClientBuilder.create().build(); // TODO Check if any specific use case for this httpclient. Else remove

    @SneakyThrows
    public static Integer lookupUserDC(String name, GroupType groupType) throws Exception {
        var url = FacilioProperties.getIAMDCLookupURL();
        Map<String, String> params = new HashMap<>();
        params.put("userName", name);
        if (groupType == null) {
        		groupType = GroupType.FACILIO;
        }
        params.put("groupType", groupType.getIndex().toString());
        
        String response = FacilioHttpUtils.doHttpGet(getUrl(url), getSecretKeyHeader(), params);
        if (StringUtils.isNotEmpty(response)) {
        		JSONObject obj = FacilioUtil.parseJson(response);
        		if (obj.containsKey("code")) {
        			int code = FacilioUtil.parseInt(obj.get("code").toString());
        			if (code == 0) {
        				JSONObject data = (JSONObject) obj.get("data");
        				return FacilioUtil.parseInt(data.get("dc").toString());
        			}
        		}
        }
        return null;
    }
    
    public static void addUserToDC(String name, GroupType groupType) throws Exception {
        var url = FacilioProperties.getIAMAddUserURL();
        JSONObject params = new JSONObject();
        params.put("userName", name);
        if (groupType == null) {
        		groupType = GroupType.FACILIO;
        }
        params.put("groupType", groupType.getIndex().toString());
        params.put("dc", DCUtil.getCurrentDC().toString());
        
        JSONObject body = new JSONObject();
        body.put("user", params);
        
        String response = FacilioHttpUtils.doHttpPost(getUrl(url), getSecretKeyHeader(), null, body);
        if (StringUtils.isNotEmpty(response)) {
        		JSONObject obj = FacilioUtil.parseJson(response);
        		if (obj.containsKey("code")) {
        			int code = FacilioUtil.parseInt(obj.get("code").toString());
        			if (code != 0) {
        				// Throw proper AccountException
        			}
        		}
        }
    }

    public static void deleteDevicePassCode(String code) throws Exception {
        var url = FacilioProperties.getIAMURL() + "/api/v3/internal/dc/deleteDevicePassCode";
        JSONObject params = new JSONObject();
        params.put("deviceCode", code);
        String response = FacilioHttpUtils.doHttpPost(getUrl(url), getSecretKeyHeader(), null, params);
        LOGGER.error("[deleteDevicePassCode] " + response);
        if (StringUtils.isNotEmpty(response)) {
            JSONObject obj = FacilioUtil.parseJson(response);
            if (obj.containsKey("code")) {
                int responseCode = FacilioUtil.parseInt(obj.get("code").toString());
                if (responseCode != 0) {
                    // Throw proper AccountException
                }
            }
        }
    }

    public static void markCodeAsConnected(String code, long connectedDeviceId, int currentDC) throws Exception {
        var url = FacilioProperties.getIAMURL() + "/api/v3/internal/dc/markCodeAsConnected";
        JSONObject params = new JSONObject();
        params.put("deviceCode", code);
        params.put("connectedDeviceId", connectedDeviceId);
        params.put("dc", currentDC);
        String response = FacilioHttpUtils.doHttpPost(getUrl(url), getSecretKeyHeader(), null, params);
        LOGGER.error("[markCodeAsConnected] " + response);
        if (StringUtils.isNotEmpty(response)) {
            JSONObject obj = FacilioUtil.parseJson(response);
            if (obj.containsKey("code")) {
                int responseCode = FacilioUtil.parseInt(obj.get("code").toString());
                if (responseCode != 0) {
                    // Throw proper AccountException
                }
            }
        }
    }

    public static Map<String, Object> getDeviceCodeInfo(String code) throws Exception {
        var url = FacilioProperties.getIAMURL() + "/api/v3/internal/dc/getDeviceCodeInfo";
        JSONObject params = new JSONObject();
        params.put("deviceCode", code);
        String response = FacilioHttpUtils.doHttpPost(getUrl(url), getSecretKeyHeader(), null, params);
        LOGGER.error("[getDeviceCodeInfo] " + response);
        if (StringUtils.isNotEmpty(response)) {
            JSONObject obj = FacilioUtil.parseJson(response);
            if (obj.containsKey("data")) {
                JSONObject data = (JSONObject) obj.get("data");
                return (Map<String, Object>) data.get("deviceCodeInfo");
            }
        }
        return null;
    }


    public static Map<String, Object> getDevicePasscode(String code) throws Exception {
        var url = FacilioProperties.getIAMURL() + "/api/v3/internal/dc/getDevicePasscode";
        JSONObject params = new JSONObject();
        params.put("deviceCode", code);
        String response = FacilioHttpUtils.doHttpPost(getUrl(url), getSecretKeyHeader(), null, params);
        LOGGER.error("[getDevicePasscode] " + response);
        if (StringUtils.isNotEmpty(response)) {
            JSONObject obj = FacilioUtil.parseJson(response);
            if (obj.containsKey("data")) {
                JSONObject data = (JSONObject) obj.get("data");
                return (Map<String, Object>) data.get("deviceCodeInfo");
            }
        }
        return null;
    }


    
    private static String getUrl(String url) {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		return RequestUtil.getProtocol(request) + "://" + url;
	}
    
    private static Map<String, String> getSecretKeyHeader() {
    		// TODO
    		return null;
    }

    private static JSONObject getJsonFromResponse(HttpResponse response) throws ParseException {
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            //handle unsuccessful response
            return null;
        }
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(getResponseString(response));
    }

    private static String getResponseString(HttpResponse response){
        HttpEntity ent = response.getEntity();
        InputStream is = null;
        try {
            is = ent.getContent();
            InputStreamReader isr = new InputStreamReader(is);
            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();
            return result;
        } catch (IOException e) {
            LOGGER.info("Exception occurred ",e);
        }
        return "_";
    }
}
