package com.facilio.urjanet.entity;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;
import org.json.simple.JSONObject;

public class Attribute implements Entity {

	public static String moduleString = "/attribute";
	public static String searchURL = moduleString+"/search";
	public static JSONObject searchJSON = null;
	
	@Override
	public EntityType getEntityType() {
		// TODO Auto-generated method stub
		return EntityType.Attributes;
	}
	
	public String getSearchURL()
	{
		return this.searchURL;
	}
	
	public void setSearchJSON(JSONObject object)
	{
		this.searchJSON = object;
	}
	
	public JSONObject getSearchJSON()
	{
		return this.searchJSON;
	}
	
	
}
