package com.facilio.urjanet.entity;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;
import org.json.simple.JSONObject;

public class Meter implements Entity{

	public static String moduleString = "/meter";
	public static String searchURL = moduleString+"/search";
	public static JSONObject searchJSON = null;
	
	@Override
	public EntityType getEntityType() {

		return EntityType.Meters;
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
