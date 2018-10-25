package com.facilio.urjanet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.urjanet.entity.Account;
import com.facilio.urjanet.entity.Attribute;
import com.facilio.urjanet.entity.ChangeLog;
import com.facilio.urjanet.entity.Credential;
import com.facilio.urjanet.entity.Meter;
import com.facilio.urjanet.entity.Provider;
import com.facilio.urjanet.entity.Template;

public class ExampleCall {
	
	public static void main(String args[])
	{
		UrjanetConnection api = new UrjanetConnection("raj-facilio","Facilio2017#");
		Account accEntity =  new Account();
		new Credential();
		new Meter();
		new Attribute();
		new ChangeLog();
		new Template();
		Provider providerEntity = new Provider();
		
		JSONObject result = null;
		try{
			accEntity.setCreateJSON(getCreateAccountJSON());
/*			result = api.create(accEntity);
			System.out.println(">>>>>>> create account  : "+result);
			accEntity.setUpdateJSON(getUpdateAccountJSON());
			result = api.update(accEntity);
			System.out.println(">>>>>>> update account : "+result);
			accEntity.setAccountURL("2464914147");
			result = api.getStatus(accEntity);
			System.out.println(">>>>>>> account status : "+result);
			accEntity.setSearchAccountJSON(getSearchAccountJSON());
			result = api.search(accEntity);
			System.out.println(">>>>>>> search account status : "+result);
			accEntity.setSearchCandidateJSON(getSearchAccountJSON());
			result = api.searchCandidate(accEntity);
			System.out.println(">>>>>>> search candidate status : "+result);
			
			credEntity.setSearchJSON(getSearchCredentialJSON());
			result = api.search(credEntity);
			System.out.println(">>>>>>> search credentials status : "+result);
			
			meterEntity.setSearchJSON(getSearchMeterJSON());
			result = api.search(meterEntity);
			System.out.println(">>>>>>> search Meter status : "+result);
			
			attrEntity.setSearchJSON(getSearchAttributeJSON());
			result =  api.search(attrEntity);
			System.out.println(">>>>>>> search Attribute status : "+result);
		
			clogEntity.setSearchJSON(getSearchChangeLogJSON());
			result = api.search(clogEntity);
			System.out.println(">>>>>>> search ChangeLog status : "+result);
				
			templateEntity.setSearchJSON(getSearchTemplateJSON());
			result = api.search(templateEntity);
			System.out.println(">>>>>>> search Template status : "+result);
*/			
			providerEntity.setSearchJSON(getSearchProviderJSON());
			result = api.search(providerEntity);
			System.out.println(">>>>>>> search Provider status : "+result);
			
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}
		
		
		
	}
	
	
	public static JSONObject getCreateAccountJSON()
	{
		JSONObject data = new JSONObject();
		data.put("accountNumber", "01460-97154");
		data.put("customerId", "Facilio");
		data.put("utilityProviderId", "GeorgePower");
		data.put("subscribed", "true");
		data.put("pullHistory", "false");
		data.put("toBeDelivered", "true");
		data.put("ebillRequirement", "TURN_ON_EBILL");
		data.put("extractionChannelId", "Facilio_10001-GeorgePower_1");

		return data;
	}
	
	public static JSONObject getUpdateAccountJSON()
	{
		JSONObject data = new JSONObject();
		data.put("id", "24649-14147");
		data.put("pullHistory", "true");

		return data;
	}

	public static JSONObject getSearchAccountJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "10");
		JSONArray sorts = new JSONArray();
		JSONObject sorts1 = new JSONObject();
		sorts1.put("field", "accountNumber");
		sorts1.put("direction", "asc");
		sorts.add(sorts1);
		queryCriteria.put("sorts", sorts);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "extractionStatus");
		conditions1.put("operator", "eq");
		conditions1.put("value", "SUCCESS");
		conditions.add(conditions1);
		filter.put("conditions", conditions);
		queryCriteria.put("filter", filter);
		data.put("queryCriteria", queryCriteria);		
		//System.out.println(">>>> data : "+data.toString());
		return data;
	}
	
	public static JSONObject getSearchCredentialJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		queryCriteria.put("sorts", null);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "utilityProviderId");
		conditions1.put("operator", "includes");
		conditions1.put("value", "Georgia");
		conditions.add(conditions1);
		filter.put("conditions", conditions);
		queryCriteria.put("filter", filter);
		data.put("queryCriteria", queryCriteria);
		System.out.println(">>>> search criteria json : "+data.toString());
		return data;
	}
	
	public static JSONObject getSearchMeterJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		JSONArray sorts = new JSONArray();
		JSONObject sorts1 = new JSONObject();
		sorts1.put("field", "meterNumber");
		sorts1.put("direction", "asc");
		sorts.add(sorts1);
		queryCriteria.put("sorts", sorts);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "serviceType");
		conditions1.put("operator", "eq");
		conditions1.put("value", "GAS");
		conditions.add(conditions1);
		filter.put("conditions",conditions);
		data.put("filter", filter);
		
		return data;
	}
	
	public static JSONObject getSearchAttributeJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject 	queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		queryCriteria.put("sorts", null);
		queryCriteria.put("filter", null);
		data.put("queryCriteria", queryCriteria);
		
		return data;
	}
	
	public static JSONObject getSearchChangeLogJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		queryCriteria.put("sorts", null);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "requestedByEmail");
		conditions1.put("operator", "eq");
		conditions1.put("value", "raj@facilio.com");
		conditions.add(conditions1);
		filter.put("conditions", conditions);
		queryCriteria.put("filter", filter);
		data.put("queryCriteria", queryCriteria);
		
		return data;
	}
	
	public static JSONObject getSearchTemplateJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		queryCriteria.put("sorts", null);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "templateName");
		conditions1.put("operator", "includes");
		conditions1.put("value", "Georgia");
		conditions.add(conditions1);
		filter.put("conditions", conditions);
		queryCriteria.put("filter", filter);
		data.put("queryCriteria", queryCriteria);
		
		return data;
	}
	
	public static JSONObject getSearchProviderJSON()
	{
		JSONObject data = new JSONObject();
		data.put("customerId", "Facilio");
		JSONObject queryCriteria = new JSONObject();
		queryCriteria.put("start", "0");
		queryCriteria.put("length", "100");
		queryCriteria.put("sorts", null);
		JSONObject filter = new JSONObject();
		JSONArray conditions = new JSONArray();
		JSONObject conditions1 = new JSONObject();
		conditions1.put("field", "providerAlias");
		conditions1.put("operator", "startsWith");
		conditions1.put("value", "Georgia");
		conditions.add(conditions1);
		filter.put("conditions", conditions);
		queryCriteria.put("filter", filter);
		data.put("queryCriteria", queryCriteria);
		
		return data;
	}
}
