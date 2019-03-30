package com.facilio.urjanet;

import com.facilio.urjanet.entity.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

public class UrjanetConnection {
	
	public String urjanetUrl = null;
	public String username = "raj-facilio";
	public String password = "Facilio2017#";
	public String encodingString = "";
	public String customerId = "facilio";

	public JSONObject create(Entity record) throws ClientProtocolException, IOException, ParseException
	{
		JSONObject response = null;
		if(record.getEntityType() == EntityType.Account)
		{
			Account obj = (Account)record;
			String url = urjanetUrl+obj.getCreateURL();
			JSONObject createAccountJSON = obj.getCreateJSON();
			response = getResponseFromPost(url,createAccountJSON);
		}
		else if(record.getEntityType() == EntityType.Credential)
		{
			Credential obj = (Credential)record;
			obj.getCreateURL();
			obj.getCreateJSON();
		}
		return response;
	}

public JSONObject update(Entity record) throws ClientProtocolException, IOException, ParseException
{
	JSONObject response = null;
	if(record.getEntityType() == EntityType.Account)
	{
		Account obj = (Account)record;
		String url = urjanetUrl+obj.getUpdateURL();
		JSONObject updateAccountJSON = obj.getUpdateJSON();
		response = getResponseFromPost(url,updateAccountJSON);	
	}
	return response;
}

public JSONObject getStatus(Entity record) throws ClientProtocolException, IOException, ParseException
{
	JSONObject response = null;
	if(record.getEntityType() == EntityType.Account)
	{
		Account obj = (Account)record;
		String url = urjanetUrl+obj.getAccountURL();
		response = getResponseFromURL(url);	
	}
	return response;
}

public JSONObject search(Entity record) throws ClientProtocolException, IOException, ParseException
{
	JSONObject response = null;
	if(record.getEntityType() == EntityType.Account)
	{
		Account obj = (Account)record;
		String url = urjanetUrl+obj.getSearchAccountURL();
		JSONObject searchAccountJSON = obj.getSearchAccountJSON();
		response =  getResponseFromPost(url,searchAccountJSON);
	}
	else if(record.getEntityType() == EntityType.Credential)
	{
		Credential obj = (Credential)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchCredentialJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchCredentialJSON);
	}
	else if(record.getEntityType() == EntityType.Meters)
	{
		Meter obj = (Meter)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchMeterJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchMeterJSON);
	}
	else if(record.getEntityType() == EntityType.Attributes)
	{
		Attribute obj = (Attribute)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchAttributeJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchAttributeJSON);
	}
	else if(record.getEntityType() == EntityType.ChangeLog)
	{
		ChangeLog obj = (ChangeLog)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchChangeLogJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchChangeLogJSON);
	}
	else if(record.getEntityType() == EntityType.Templates)
	{
		Template obj = (Template)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchTemplateJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchTemplateJSON);
	}
	else if(record.getEntityType() == EntityType.Providers)
	{
		Provider obj = (Provider)record;
		String url = urjanetUrl+obj.getSearchURL();
		JSONObject searchProviderJSON = obj.getSearchJSON();
		response = getResponseFromPost(url,searchProviderJSON);
	}
	
	return response;
}

public JSONObject searchCandidate(Entity record) throws ClientProtocolException, IOException, ParseException
{
	JSONObject response = null;
	if(record.getEntityType() == EntityType.Account)
	{
		Account obj = (Account)record;
		String url = urjanetUrl+obj.getSearchCandidateURL();
		JSONObject searchCandidateJSON = obj.getSearchCandidateJSON();
		response =  getResponseFromPost(url,searchCandidateJSON);
	}
	return response;
}

public UrjanetConnection()
{
	urjanetUrl = "https://api.urjanet.net/api/v1";
	//urjanetUrl = "https://portalqa.urjanet.net/api/v1";
	encodingString =  Base64.getEncoder().encodeToString((username + ":" + password).getBytes()); 
} 

public UrjanetConnection(String username, String password)
{
	this.username = username;
	this.password = password;
	urjanetUrl = "https://api.urjanet.net/api/v1";
	//urjanetUrl = "https://portalqa.urjanet.net/api/v1";
	encodingString =  Base64.getEncoder().encodeToString((username + ":" + password).getBytes()); 
}

public enum EntityType {
	Account,
	Credential,
	Meters,
	Providers,
	ServiceType,
	Templates,
	ChangeLog,
	Attributes
}

public JSONObject getResponseFromURL(String URL) throws ClientProtocolException, IOException, ParseException
{
	CloseableHttpClient client = HttpClients.createDefault();
	System.out.println("URL : "+URL);
	HttpGet httpGet = new HttpGet(URL);
	httpGet.setHeader("Authorization", "Basic " + encodingString);
    httpGet.setHeader("Content-Type","application/json");
   
    HttpResponse response = client.execute(httpGet);
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


public JSONObject getResponseFromPost(String URL, JSONObject data) throws ClientProtocolException, IOException, ParseException
{
	CloseableHttpClient client = HttpClients.createDefault();
	System.out.println("URL : "+URL);
	HttpPost httpPost = new HttpPost(URL);
	httpPost.setHeader("Authorization", "Basic " + encodingString);
    httpPost.setHeader("Content-Type","application/json");
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


public static void main(String args) throws ClientProtocolException, IOException, ParseException
{
	UrjanetConnection uc = new UrjanetConnection();
	uc.login("raj-facilio", "Facilio2017#");
	
	
	//uc.search(EntityType.Account, new JSONObject());
	uc.create(new Account());
}

public void login(String username, String password)
{
	
	Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	//myHttpPost.setHeader("Authorization", "Basic " + encoding);
	return;
}



}
