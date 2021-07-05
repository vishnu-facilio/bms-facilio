package com.facilio.iam.accounts.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collections;
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

import com.facilio.aws.util.FacilioProperties;
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
    public static Integer lookupUserDC(String name) throws Exception {
        var url = FacilioProperties.getIAMUserLookupURL();
        Map<String, String> params = Collections.singletonMap("username", name);
        
        String response = FacilioHttpUtils.doHttpGet(getProtocol() + "://" + url, null, params);
        if (StringUtils.isNotEmpty(response)) {
        		JSONObject obj = FacilioUtil.parseJson(response);
        		JSONObject responseObj = (JSONObject) obj.get("jsonresponse");
        		if (responseObj.containsKey("code")) {
        			int code = FacilioUtil.parseInt(responseObj.get("code").toString());
        			if (code == 1) {
        				return FacilioUtil.parseInt(responseObj.get("dc").toString());
        			}
        		}
        }
        return null;
    }
    
    private static String getProtocol() {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		return RequestUtil.getProtocol(request);
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
