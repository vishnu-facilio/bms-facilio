package com.facilio.urjanet.entity;

import org.json.simple.JSONObject;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;

public class Provider implements Entity {

	public static String moduleString = "/provider";
	public static String searchURL =moduleString + "/search";
	public static JSONObject searchJSON = null;
	
	@Override
	public EntityType getEntityType() {
		// TODO Auto-generated method stub
		return EntityType.Providers;
	}

	public String getSearchURL()
	{
		return this.searchURL;
	}
	
	public void setSearchJSON(JSONObject obj)
	{
		this.searchJSON = obj;
	}
	
	public JSONObject getSearchJSON()
	{
		return this.searchJSON;
	}
}
