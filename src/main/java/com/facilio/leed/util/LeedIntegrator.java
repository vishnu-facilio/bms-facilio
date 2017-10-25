package com.facilio.leed.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LeedIntegrator {
	
	public static String hostname = "api.usgbc.org";
	public static int port = 443;
	public static String username = "yoge@facilio.com";
	public static String password = "Chennai1#";
	public static String subscriptionKey = "ffa4212a87b748bb8b3623f3d97ae285"; // This key is available developer profile page @ https://developer.usgbc.org/developer
	public static String authheadervalue= Base64.getEncoder().encodeToString((username+":"+password).getBytes());
	public static String authKey = "Bearer Qq2NJpDM7IieThJiU3iFIoqT19YJGW"; // This is generated with doAuth() method in this same class. 
	public static String serverURL = "https://api.usgbc.org:443";
	
	public static void main(String[] args) throws ClientProtocolException, IOException, ParseException {
		//doAuth();
		LeedIntegrator api = new LeedIntegrator();
		JSONObject response = api.getAssetList();
		System.out.println(response);
	}
	
	public static JSONObject getAssetDetail(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getAssetList() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getMeters(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getAssetMeterDetail(String leedId, String meterId) throws ClientProtocolException,IOException,ParseException
	{
		String urlString = serverURL+"arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getPerformanceScores(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/scores/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject searchAsset(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString;
		if(leedId != null)
		{
			urlString = serverURL+"/arc/data/dev/assets/search/?q="+1000064028;
		}
		else
		{
			urlString = serverURL+"/arc/data/dev/assets/search/";
		}
		JSONObject response = getURLResponse(urlString);
		return response;
		
	}
	
	public static JSONObject searchAsset() throws ClientProtocolException, IOException, ParseException
	{
		return searchAsset(null);
	}
	
	public static JSONObject getConsumptionList(String leedId, String meterId) throws ClientProtocolException, IOException, ParseException 
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getConsumptionDetail(String leedId, String meterId, String consumptionId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/ID:"+consumptionId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getEnvironmentSurvey(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/environment/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getEnvironmentSurveySummarize(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/environment/summarize/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getFuelCategory() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/fuel/category/";
		JSONObject response =  getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getTransitSurvey(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/transit/";
		JSONObject response = getURLResponse(urlString);
		return response;
		
	}
	
	public static JSONObject getTransitSurveySummarize(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/transit/summarize/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getWaste(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/waste/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject getWasteDetails(String leedId, String wasteId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/waste/ID:"+wasteId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public static JSONObject createMeter(long leedId, JSONObject meterInfo) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/";
		/*
		 * Sample meterInfo can be as follow :
		 * { "name": "DG_Mains", "included": "true", "native_unit": "kWh", "type": "13"}
		 * Here the type 13 is got from fuelCategory query, where for Id:13, the fuel type is Diesel.
		 * This RestAPI Call to create meter will return a JSON with meter ID as one of the parameter and response code as 201
		 */
		JSONObject response = getPostResponse(urlString,meterInfo);
		return response;
	}
	
	public static JSONObject updateMeter(String leedId,String meterId,JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
	/*
	 * Note that in updateInfo json, name and type attributes are required attributes
	*/
		String urlString = serverURL+"arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/";
		JSONObject response = getUpdateResponse(urlString,updateInfo);
		return response; 
	}
	
	public static JSONObject createConsumption(long leedId,long meterId,JSONObject consumptionData) throws ClientProtocolException, IOException, ParseException
	{
//		Sample consumptionData json 
//		{
//			"start_date": "2017-01-29T10:00:10.894752+05",
//			"end_date": "2017-01-30T10:01:10.894752+05",
//			"reading": "1200"
//		}
		
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/";
		JSONObject response = getPostResponse(urlString,consumptionData);
		return response;
	}
	
	public static JSONObject updateConsumption(String leedId, String meterId, String consumptionId, JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
//		Sample updateInfo. Note that same interval cannot be given for update. 
//		{
//			"start_date": "2017-04-25T10:00:10.894752+05",
//			"end_date": "2017-04-26T10:01:10.894752+05",
//			"reading": "1150"
//		}	
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/ID:"+consumptionId;
		JSONObject response =  getUpdateResponse(urlString,updateInfo);
		return response;
	}
	
	public static JSONObject updateAsset(String leedId, JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
//		Sample updateInfo JSON 
//		{
//			  "gross_area": 15000
//		}
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/";
		JSONObject response =  getUpdateResponse(urlString,updateInfo);
		return response;
	}
/*	
	
	public JSONObject createWasteGenerated(String leedId, JSONObject wasteGenData) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/waste/generated/";
		
	}
	
	public JSONObject createWasteDiverted() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"";
	}
	
	public JSONObject createEnvironmentSurvey() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"";
	}
	
	public JSONObject createTransitSurvey() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"";
	}
	
	public JSONObject createWaste() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"";
	}
	
	public JSONObject updateWaste() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"";
	}
	*/
	private static JSONObject getUpdateResponse(String urlString,JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(urlString);
		httpPut.setHeader("Authorization",authKey);
		httpPut.setHeader("Content-Type","application/json");
		httpPut.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
		HttpEntity entity = new ByteArrayEntity(updateInfo.toString().getBytes("UTF-8"));
        httpPut.setEntity(entity);
        HttpResponse response = client.execute(httpPut);
        StringBuffer result = new StringBuffer();
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("response : "+responseCode);
        
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
    	String output;
    	while ((output = br.readLine()) != null) 
    	{
              System.out.println(output);
              result.append(output);
    	}
    	
    	int resCode = response.getStatusLine().getStatusCode();
        JSONParser parser = new JSONParser(); 
        JSONObject obj = (JSONObject)parser.parse(result.toString());
        JSONObject resp = new JSONObject();
        resp.put("status", resCode);
		resp.put("message", obj);
		
        return resp;
	}
	
	private static JSONObject getURLResponse(String urlString) throws ClientProtocolException, IOException, ParseException 
    {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(urlString);
		httpGet.setHeader("Authorization", authKey);
		httpGet.setHeader("Content-Type","application/json");
		httpGet.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
   
        HttpResponse response = client.execute(httpGet);
        StringBuffer result = new StringBuffer();
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("response : "+responseCode);
        
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
    	String output;
    	while ((output = br.readLine()) != null) 
    	{
              //System.out.println(output);
              result.append(output);
    	}
    	
    	int resCode = response.getStatusLine().getStatusCode();
        JSONParser parser = new JSONParser(); 
        JSONObject obj = (JSONObject)parser.parse(result.toString());
        JSONObject resp = new JSONObject();
        resp.put("status", resCode);
		resp.put("message", obj);
		
        return resp;
    }
	
	
	public static JSONObject getPostResponse(String URL, JSONObject data) throws ClientProtocolException, IOException, ParseException
	{
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URL);
		httpPost.setHeader("Authorization", authKey);
        httpPost.setHeader("Content-Type","application/json");
        httpPost.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
        HttpEntity entity = new ByteArrayEntity(data.toString().getBytes("UTF-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        StringBuffer result = new StringBuffer();
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("response : "+responseCode);
        
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
    	String output;
    	while ((output = br.readLine()) != null) 
    	{
              System.out.println(output);
              result.append(output);
    	}
    	
    	int resCode = response.getStatusLine().getStatusCode();
        JSONParser parser = new JSONParser(); 
        JSONObject obj = (JSONObject)parser.parse(result.toString());
        JSONObject resp = new JSONObject();
        resp.put("status", resCode);
		resp.put("message", obj);
		
        return resp;
	}
	
		public static void doAuth() throws ClientProtocolException, IOException
	    {
			HttpHost target = new HttpHost(hostname, port, "https");
	        CloseableHttpClient httpclient =org.apache.http.impl.client.HttpClients.createDefault();
	        try {
	        		HttpGet httpget = new HttpGet(serverURL);
	        		String loginURL = serverURL+"/arc/data/dev/auth/login/";
	        		HttpPost httppost = new HttpPost(loginURL);
	        		httppost.addHeader("Ocp-Apim-Subscription-Key",subscriptionKey);
	        		httppost.addHeader("Content-Type","application/json");
	        		System.out.println("Executing request \n" + httppost.getRequestLine() + " to target \n" + target );
	        		Header [] headerNames = httppost.getAllHeaders();	            
	        		for(int i=0;i<headerNames.length;i++) 
	        		{
	        			String headerName = (String)headerNames[i].getName();
	        			System.out.println(headerName + " = " + (String)headerNames[i].getValue());
	        		}
	        		StringEntity entity = new StringEntity("{\"username\":\"yoge@facilio.com\",\"password\":\"Chennai1#\"}");
	            
	        		httppost.setEntity(entity);
	            
	        		for (int i = 0; i < 3; i++) {
	        			CloseableHttpResponse response = httpclient.execute(target, httppost);
	        			try {
	        				System.out.println("----------------------------------------");
	        				System.out.println(response.getStatusLine());
	        				System.out.println(EntityUtils.toString(response.getEntity()));
	        			} finally {
	                    response.close();
	        			}
	        		}
	        	} finally {
	            httpclient.close();
	        }
	    }
}
