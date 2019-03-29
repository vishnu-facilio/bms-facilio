package com.facilio.leed.commands;

import com.facilio.leed.context.ArcContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLeedDataCommand {
	
	public static List<Map<String, String>> energy_meters = new ArrayList<>();
	private static Logger log = LogManager.getLogger(GetLeedDataCommand.class.getName());

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
			ArcContext arccontext = LeedAPI.getArcContext();
			LeedIntegrator integ = new LeedIntegrator(arccontext);
			JSONObject jsonObj = integ.getMeters(leedId);
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
		
			log.info("Exception occurred ", e);
		}
	}
	
	public void getMeterData(String leedId,String meterId)
	{
		Map<String, Double> meterMap = new HashMap<>();
		try {
			ArcContext arccontext = LeedAPI.getArcContext();
			LeedIntegrator integ = new LeedIntegrator(arccontext);
			JSONObject jsonObj = integ.getConsumptionList(leedId, meterId);
			System.out.println(">>>> jsonObj : "+jsonObj);
			String nextURL = (String)((JSONObject)jsonObj.get("message")).get("next");
			if(nextURL != null)
			{
				nextURL.substring(nextURL.lastIndexOf("="));
			}
			
			JSONArray consumptionarray = (JSONArray)((JSONObject)jsonObj.get("message")).get("results");
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			for(int i=0 ; i< consumptionarray.size(); i++)
			{
				JSONObject consumption = (JSONObject)consumptionarray.get(i);
				String consumptiondate = (String)consumption.get("end_date");
				Double consumptionvalue = (Double)consumption.get("reading");
				
				meterMap.put(consumptiondate, consumptionvalue);
			}
			System.out.println("@@@@@@@@@@@ :"+meterMap.toString());
		
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
	}
	
}
