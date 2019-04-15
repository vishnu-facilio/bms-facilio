package com.facilio.urjanet.entity;

import com.facilio.urjanet.Entity;
import com.facilio.urjanet.UrjanetConnection.EntityType;
import org.json.simple.JSONObject;

public class Account implements Entity {
	
	public static String moduleString = "/account";
	public String createURL = moduleString+"/create";
	public String enrollCandidateURL = moduleString+"/enrollCandidate";
	public String updateURL = moduleString+"/update";
	public String getAccountURL = moduleString+"/status";
	public String searchAccountURL = moduleString+"/search";
	public String searchCandidatesURL = moduleString+"/candidates";
	public JSONObject createJSON = null;
	public JSONObject enrollCandidateJSON = null;
	public JSONObject updateJSON = null;
	public JSONObject searchAccountJSON = null;
	public JSONObject searchCandidateJSON = null;
	
	public EntityType getEntityType()
	{
		return EntityType.Account;
	}
	
	public String getCreateURL()
	{
		return createURL;
	}
	
	public String getEnrollCandidateURL()
	{
		return enrollCandidateURL;
	}
	
	public String getUpdateURL()
	{
		return updateURL;
	}
	public void setAccountURL(String accountID)
	{
		getAccountURL = getAccountURL+"/"+accountID;
	}
	public String getAccountURL()
	{
		return getAccountURL;
	}
	
	public String getSearchAccountURL()
	{
		return searchAccountURL;
	}
	
	public String getSearchCandidateURL()
	{
		return searchCandidatesURL;
	}
	public void setCreateJSON( JSONObject obj)
	{
		this.createJSON = obj;
	}
	public JSONObject getCreateJSON()
	{
		return this.createJSON;
	}
	
	public void setEnrollCandidateJSON(JSONObject obj)
	{
		this.enrollCandidateJSON = obj;
	}
	
	public JSONObject getEnrollCandidateJSON()
	{
		return this.enrollCandidateJSON;
	}
	
	public void setUpdateJSON(JSONObject obj)
	{
		this.updateJSON = obj;
	}
	
	public JSONObject getUpdateJSON()
	{
		return this.updateJSON;
	}
		
	public void setSearchAccountJSON(JSONObject obj)
	{
		this.searchAccountJSON = obj;
	}
	
	public JSONObject getSearchAccountJSON()
	{
		return this.searchAccountJSON;
	}
	
	public void setSearchCandidateJSON(JSONObject obj)
	{
		this.searchCandidateJSON = obj;
	}
	
	public JSONObject getSearchCandidateJSON()
	{
		return this.searchCandidateJSON;
	}
}
