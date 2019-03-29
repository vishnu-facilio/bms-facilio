package com.facilio.urjanet.entity;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;
import org.json.simple.JSONObject;

public class Template implements Entity {

	public static String moduleString = "/template";
	public static String searchURL =moduleString + "/search";
	public static JSONObject searchJSON = null;
	@Override
	public EntityType getEntityType() {
		// TODO Auto-generated method stub
		return EntityType.Templates;
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
