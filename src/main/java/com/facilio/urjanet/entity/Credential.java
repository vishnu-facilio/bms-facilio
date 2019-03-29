package com.facilio.urjanet.entity;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;
import org.json.simple.JSONObject;

public class Credential implements Entity {
	
	public static String moduleString = "/credential";
	public static String createURL = moduleString+"create";
	public static String searchURL = moduleString+"/search" ;
	public static JSONObject createJSON = null;
	public static JSONObject getCreateJSON() {
		return createJSON;
	}

	public static void setCreateJSON(JSONObject createJSON) {
		Credential.createJSON = createJSON;
	}

	public static String getCreateURL() {
		return createURL;
	}

	public static void setCreateURL(String createURL) {
		Credential.createURL = createURL;
	}

	public static JSONObject searchJSON = null;
	
	public EntityType getEntityType()
	{
		return EntityType.Credential;
	}
	
	public String getSearchURL()
	{
		return this.searchURL;
	}
	
	public void setSearchJSON(JSONObject jsonObject)
	{
		this.searchJSON = jsonObject;
	}
	
	public JSONObject getSearchJSON()
	{
		return this.searchJSON;
	}

}
