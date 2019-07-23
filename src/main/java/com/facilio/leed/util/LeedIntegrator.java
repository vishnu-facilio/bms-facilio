package com.facilio.leed.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.leed.context.ArcContext;

public class LeedIntegrator {
	
	public static String hostname = "api.usgbc.org";
	public static int port = 443;
	public static String username = "yoge@facilio.com";
	public static String password = "Chennai1#";
	public static String subscriptionKey = "ffa4212a87b748bb8b3623f3d97ae285"; // This key is available developer profile page @ https://developer.usgbc.org/developer
	public static String authheadervalue= Base64.getEncoder().encodeToString((username+":"+password).getBytes());
	public static String authKey = "Bearer Qq2NJpDM7IieThJiU3iFIoqT19YJGW"; // This is generated with doAuth() method in this same class. 
	//public static String serverURL = "https://api.usgbc.org:443";
	private static Logger log = LogManager.getLogger(LeedIntegrator.class.getName());

	public LeedIntegrator()
	{
		
	}
	
	public ArcContext context;
	public String serverURL;
	
	public LeedIntegrator(ArcContext context) throws ClientProtocolException, IOException, SQLException, RuntimeException
	{
		this.context = context; 
		String protocol = context.getArcProtocol();
		String host = context.getArcHost();
		String port = context.getArcPort();
		this.serverURL = protocol+"://"+host+":"+port;
		
		long curretTime = System.currentTimeMillis();
		long authTime = context.getAuthUpdateTime();
		if(authTime != -1 && (curretTime - authTime) > 3600000)
		{
			this.context.setAuthKey("Bearer "+getAuthKey( context));
			LeedAPI.UpdateArcCredential(this.context);
		}
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException, ParseException {
		//doAuth();
//		LeedIntegrator api = new LeedIntegrator();
//		JSONObject response = api.getAssetList();
//		System.out.println(response);
		LeedIntegrator api = new LeedIntegrator();
		ArcContext credentials = new ArcContext();
		credentials.setUserName("yoge@facilio.com");
		credentials.setPassword("Chennai1#");
		credentials.setArcProtocol("https");
		credentials.setArcHost("api.usgbc.org");
		credentials.setArcPort("443");
		credentials.setSubscriptionKey("ffa4212a87b748bb8b3623f3d97ae285");
		api.getAuthKey(credentials);
	}
	
	public ArcContext LoginArcServer(ArcContext context) throws SQLException, RuntimeException, ClientProtocolException, IOException
	{
		String authkey = getAuthKey(context);
		context.setAuthKey("Bearer "+authkey);
		return context;
	}
	
	public String getAuthKey(ArcContext context) throws ClientProtocolException, IOException
    {
		HttpHost target = new HttpHost(context.getArcHost(), Integer.parseInt(context.getArcPort()), context.getArcProtocol());
        CloseableHttpClient httpclient =org.apache.http.impl.client.HttpClients.createDefault();
        String authToken = null;
        String serverURL1 = target.toURI();
        try {
        		new HttpGet(serverURL1);
        		String loginURL = serverURL1+"/arc/data/dev/auth/login/";
        		HttpPost httppost = new HttpPost(loginURL);
        		httppost.addHeader("Ocp-Apim-Subscription-Key",context.getSubscriptionKey());
        		httppost.addHeader("Content-Type","application/json");
        		System.out.println("Executing request \n" + httppost.getRequestLine() + " to target \n" + target );
        		Header [] headerNames = httppost.getAllHeaders();	            
        		for(int i=0;i<headerNames.length;i++) 
        		{
        			String headerName = headerNames[i].getName();
        			System.out.println(headerName + " = " + headerNames[i].getValue());
        		}
        		String userName = context.getUserName();
        		String password = context.getPassword();
        		JSONObject cred = new JSONObject();
        		cred.put("username", userName);
        		cred.put("password", password);
        		//StringEntity entity = new StringEntity("{\"username\":\"yoge@facilio.com\",\"password\":\"Chennai1#\"}");
        		StringEntity entity = new StringEntity(cred.toString());
        		httppost.setEntity(entity);
        		CloseableHttpResponse response = httpclient.execute(target, httppost);
        		String respStr = EntityUtils.toString(response.getEntity());
        		System.out.println("@@@ response : "+respStr);
        		JSONParser parser = new JSONParser(); 
        		JSONObject json =  (JSONObject) parser.parse(respStr);
        		authToken = (String)json.get("authorization_token");
        		System.out.println("#### authToken : "+authToken);
        	}catch(Exception e)
        	{
        		log.info("Exception occurred ", e);
        	}
        	finally {
            httpclient.close();
        }
        return authToken;
    }
	
	public JSONObject getAssetDetail(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getAssetList() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getMeters(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getAssetMeterDetail(String leedId, String meterId) throws ClientProtocolException,IOException,ParseException
	{
		String urlString = serverURL+"arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getPerformanceScores(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/scores/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject searchAsset(String leedId) throws ClientProtocolException, IOException, ParseException
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
	
	public  JSONObject searchAsset() throws ClientProtocolException, IOException, ParseException
	{
		return searchAsset(null);
	}
	
	public  JSONObject getConsumptionList(String leedId, String meterId) throws ClientProtocolException, IOException, ParseException 
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public JSONArray getConsumptionListAsArray(String leedId, String meterId) throws ClientProtocolException, IOException, ParseException
	{
		JSONArray consumptionArray = new JSONArray();
		JSONObject message = getConsumptionList(leedId,meterId);
		JSONObject json = (JSONObject) message.get("message");
		String nextStr = (String)json.get("next");
		consumptionArray.add(message);
		while(nextStr != null)
		{
			System.out.println(">>>> nextStr : "+nextStr);
			JSONObject response = getURLResponse(nextStr);
			consumptionArray.add(response);
		}
		
		return consumptionArray;
	}
	
	public  JSONObject getConsumptionDetail(String leedId, String meterId, String consumptionId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/consumption/ID:"+consumptionId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getEnvironmentSurvey(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/environment/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public JSONObject getEnvironmentSurveySummarize(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/environment/summarize/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getFuelCategory() throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/fuel/category/";
		JSONObject response =  getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getTransitSurvey(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/transit/";
		JSONObject response = getURLResponse(urlString);
		return response;
		
	}
	
	public  JSONObject getTransitSurveySummarize(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/survey/transit/summarize/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getWaste(String leedId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/waste/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject getWasteDetails(String leedId, String wasteId) throws ClientProtocolException, IOException, ParseException
	{
		String urlString = serverURL+"/arc/data/dev/assets/LEED:"+leedId+"/waste/ID:"+wasteId+"/";
		JSONObject response = getURLResponse(urlString);
		return response;
	}
	
	public  JSONObject createMeter(long leedId, JSONObject meterInfo) throws ClientProtocolException, IOException, ParseException
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
	
	public  JSONObject updateMeter(String leedId,String meterId,JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
	/*
	 * Note that in updateInfo json, name and type attributes are required attributes
	*/
		String urlString = serverURL+"arc/data/dev/assets/LEED:"+leedId+"/meters/ID:"+meterId+"/";
		JSONObject response = getUpdateResponse(urlString,updateInfo);
		return response; 
	}
	
	public  JSONObject createConsumption(long leedId,long meterId,JSONObject consumptionData) throws ClientProtocolException, IOException, ParseException
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
	
	public  JSONObject updateConsumption(String leedId, String meterId, String consumptionId, JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
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
	
	public JSONObject updateAsset(String leedId, JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
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
	private  JSONObject getUpdateResponse(String urlString,JSONObject updateInfo) throws ClientProtocolException, IOException, ParseException
	{
		String authKey = context.getAuthKey();
		String subscriptionKey = context.getSubscriptionKey();
		
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
	
	private JSONObject getURLResponse(String urlString) throws ClientProtocolException, IOException, ParseException 
    {
		String authKey = context.getAuthKey();
		String subscriptionKey = context.getSubscriptionKey();
		
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
	
	
	public JSONObject getPostResponse(String URL, JSONObject data) throws ClientProtocolException, IOException, ParseException
	{
		String authKey = context.getAuthKey();
		String subscriptionKey = context.getSubscriptionKey();
		
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
	
/*		public static void doAuth() throws ClientProtocolException, IOException
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
	    }*/
}
