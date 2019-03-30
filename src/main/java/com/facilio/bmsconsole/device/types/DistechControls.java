package com.facilio.bmsconsole.device.types;

import com.facilio.aws.util.AwsUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistechControls 
{
	private static Logger logger = Logger.getLogger(DistechControls.class.getName());
	
	String userName;
	String password;
	String ipAddress;
	public DistechControls(String userName, String password, String ipAddress)
	{
		this.userName = userName;
    	this.password = password;
		this.ipAddress = ipAddress;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllDeviceNames() throws IOException, ParseException
	{
		List<String> deviceNames = new ArrayList<>();
		String objectType = "analogvalue";
		String propertyName = "description";
		String instance = "-1";
        String result =  getData(objectType, propertyName, instance);
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(result);
        Iterator<JSONObject> iterator = array.iterator();
		while(iterator.hasNext())
		{
			JSONObject json = iterator.next();
			String deviceName = (String)json.get("value");
			if(deviceNames.contains(deviceName))
			{
				continue;
			}
			deviceNames.add(deviceName);
		}	
		return deviceNames;
	}
	
	public String getData(String objectType, String propertyName, String instance) throws IOException
	{
		String sessionCookie = getSessionCookie(userName, password, ipAddress);
		String url = "http://" + ipAddress + "/api/rest/v1/protocols/bacnet/local/objects/read-property-multiple";
		Map<String, String> headers = new HashMap<>();
		headers.put("cookie", sessionCookie);
		headers.put("content-type", "application/json");
		String bodyContent = "{\"encode\": \"text\",\"propertyReferences\": [{\"type\":\"" + objectType + "\",\"instance\": " + instance + ",\"property\": \"" + propertyName + "\"}]}";
		
        return AwsUtil.doHttpPost(url, headers, null, bodyContent);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getAllDataPoints() throws IOException, ParseException
	{
		String objectType = "analogvalue";
		String propertyName = "objectName";
		String instance = "-1";
        String result =  getData(objectType, propertyName, instance);
        
        JSONObject dataPointMap = new JSONObject();
        JSONParser parser = new JSONParser();
		JSONArray array = (JSONArray) parser.parse(result);
		Iterator<JSONObject> iterator = array.iterator();
		while(iterator.hasNext())
		{
			JSONObject analogDataPoints = new JSONObject();
			JSONObject json = iterator.next();
			String inst = ((Long)json.get("instance")).toString();
			String name = (String)json.get("value");
			analogDataPoints.put("name", name);
			dataPointMap.put(inst, analogDataPoints);
		}
		
		propertyName = "description";
		result = getData(objectType, propertyName, instance);
		
		parser = new JSONParser();
		array = (JSONArray) parser.parse(result);
		iterator = array.iterator();
		while(iterator.hasNext())
		{
			JSONObject json = iterator.next();
			String inst = ((Long)json.get("instance")).toString();
			JSONObject analogDataPoints = (JSONObject) dataPointMap.get(inst);
			
			String name = (String)json.get("value");
			analogDataPoints.put("description", name);
			dataPointMap.put(inst, analogDataPoints);
		}	
		return dataPointMap;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getPresentValues() throws IOException, ParseException
	{
		String sessionCookie = getSessionCookie(userName, password, ipAddress);
		
		JSONObject valuesMap = new JSONObject();
		
		String url = "http://" + ipAddress + "/api/rest/v1/protocols/bacnet/local/objects/read-property-multiple";
		Map<String, String> headers = new HashMap<>();
		headers.put("cookie", sessionCookie);
		headers.put("content-type", "application/json");
		
		String objectType = "analogvalue";
		String propertyName = "presentValue";
		String instance = "-1";
		
		String bodyContent = "{\"encode\": \"text\",\"propertyReferences\": [{\"type\":\"" + objectType + "\",\"instance\": " + instance + ",\"property\": \"" + propertyName + "\"}]}";
        String result = AwsUtil.doHttpPost(url, headers, null, bodyContent);
		
		JSONParser parser = new JSONParser();
		JSONArray array = (JSONArray) parser.parse(result);
		Iterator<JSONObject> iterator = array.iterator();
		while(iterator.hasNext())
		{
			JSONObject analogDataPoints = new JSONObject();
			JSONObject json = iterator.next();
			String inst = ((Long)json.get("instance")).toString();
			String value = (String)json.get("value");
			analogDataPoints.put("value", value);
			valuesMap.put(inst, analogDataPoints);		
		}
		System.out.println(valuesMap);
		return valuesMap;
	}
	
	public String getSessionCookie(String userName, String password, String ipAddress) throws IOException
	{
		String authorization = "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes()));
    	String url = "http://" + ipAddress + "/api/rest/v1/";
    	
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        HttpGet method = new HttpGet(url);
        method.addHeader("Authorization", authorization);
        String cookieName = null;
        String cookieValue = null;
        try 
        {
        	httpclient.execute(method, context);
		    CookieStore cookieStore = context.getCookieStore();
		    List<Cookie> cookies = cookieStore.getCookies();
            cookieName = cookies.get(0).getName();
            cookieValue = cookies.get(0).getValue();
            System.out.println(cookieName);
            System.out.println(cookieValue);
        }
        catch (IOException e) 
        {
        	logger.log(Level.SEVERE, "Executing getSessionCookie ::::" + e.getMessage(), e);
        	throw e;
        } 
        finally 
        {
        	httpclient.close();
        }
        return cookieName + "=" + cookieValue;
	}
}
