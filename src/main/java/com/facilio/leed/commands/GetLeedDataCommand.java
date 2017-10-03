package com.facilio.leed.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.constants.FacilioConstants;
import com.facilio.leed.util.LeedIntegrator;

public class GetLeedDataCommand {
	
	public static List<Map<String, String>> energy_meters = new ArrayList<>();
	
	public static void main(String args[])
	{
		GetLeedDataCommand obj = new GetLeedDataCommand();
		//obj.getMetersForAsset("1000064200");
		obj.getMeterData("1000064200","29587");
	}

	public void getMetersForAsset(String leedId)
	{
		List<Map<String, String>> meters = new ArrayList<>();
		
		try {
		
			JSONObject jsonObj = LeedIntegrator.getMeters(leedId);
			//System.out.println(">>>>>>>>> "+jsonObj);
			JSONArray meterarray = (JSONArray)((JSONObject)jsonObj.get("message")).get("results");

			for(int i=0 ; i< meterarray.size(); i++)
			{
				JSONObject meter = (JSONObject)meterarray.get(i);
				String id = ((Long)meter.get("id")).toString();
				String name = (String)meter.get("name");
				String unit = (String)meter.get("native_unit");
				String fueltype = (String)((JSONObject)meter.get("fuel_type")).get("type");
				String fuelId = ((Long)((JSONObject)meter.get("fuel_type")).get("id")).toString();
				Map<String, String> meterMap = new HashMap<>();
				meterMap.put("id", id);
				meterMap.put("name", name);
				meterMap.put("unit", unit);
				meterMap.put("fueltype", fueltype);
				meterMap.put("fuelId", fuelId);
				meters.add(meterMap);
				if(unit.equalsIgnoreCase("kWh"))
				{
					energy_meters.add(meterMap);
				}
			}
			System.out.println(energy_meters.toString());
		
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public void getMeterData(String leedId,String meterId)
	{
		Map<String, Double> meterMap = new HashMap<>();
		try {
			JSONObject jsonObj = LeedIntegrator.getConsumptionList(leedId, meterId);
			System.out.println(">>>> jsonObj : "+jsonObj);
			String nextURL = (String)((JSONObject)jsonObj.get("message")).get("next");
			String pageNo;
			if(nextURL != null)
			{
				pageNo = nextURL.substring(nextURL.lastIndexOf("="));
			}
			
			JSONArray consumptionarray = (JSONArray)((JSONObject)jsonObj.get("message")).get("results");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			for(int i=0 ; i< consumptionarray.size(); i++)
			{
				JSONObject consumption = (JSONObject)consumptionarray.get(i);
				String consumptiondate = (String)consumption.get("end_date");
				Double consumptionvalue = (Double)consumption.get("reading");
				
				meterMap.put(consumptiondate, consumptionvalue);
			}
			System.out.println("@@@@@@@@@@@ :"+meterMap.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
