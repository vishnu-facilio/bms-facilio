package com.facilio.leed.commands;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.ConsumptionInfoContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class FetchArcAssetsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject responseJSON = LeedIntegrator.getAssetList();
		JSONObject response = (JSONObject)responseJSON.get("message");
		System.out.println("FetchArcAssetsCommand.getAssetList().response :"+response);
		
		long assetCount = (long)response.get("count");
		JSONArray assetList = (JSONArray)response.get("results");
		List<LeedConfigurationContext> leedList = new ArrayList();
		for(int i=0; i< assetList.size();i++)
		{
			JSONObject asset = (JSONObject)assetList.get(i);
			String name =  (String)asset.get("name");
			long leedId = (long)asset.get("leed_id");
			String buildingStatus = (String)asset.get("building_status");
			Double area =0.0;
			if(asset.get("gross_area") != null) {
				area = (double)asset.get("gross_area");
			}
			
			long occupancy =0;
			if(asset.get("occupancy") != null) {
				occupancy = (long)asset.get("occupancy");
			}
			String country = (String)asset.get("country");
			String zip = (String)asset.get("zip_code");
			String state = (String)asset.get("state");
			String city = (String)asset.get("city");
			String street = (String)asset.get("street");
			double lat =0.0;
			if(asset.get("geoLat") != null) {
				lat = (double)asset.get("geoLat");
			}
			double lng =0.0;
			if(asset.get("geoLang") != null) {
				lng = (double)asset.get("geoLang");
			}
			
			JSONObject scores = (JSONObject)asset.get("scores");
			
			
			HashMap scoresMap = getLeedScore(leedId);
			long leedScore = 0;
			
			LocationContext location = new LocationContext();
			location.setCity(city);
			location.setCountry(country);
			location.setLat(lat);
			location.setLng(lng);
			location.setState(state);
			location.setZip(zip);
			location.setStreet(street);
			
			LeedConfigurationContext leedcontext = new LeedConfigurationContext();
			leedcontext.setName(name);
			leedcontext.setLeedId(leedId);
			leedcontext.setBuildingStatus(buildingStatus);
			leedcontext.setGrossFloorArea(Integer.parseInt(area.toString()));
			leedcontext.setLastCurrentOccupancy(((Long)occupancy).intValue());
			leedcontext.setLocation(location);
			leedcontext.setLeedScore(leedScore);
			leedcontext.setEnergyScore((long)scoresMap.get("energyScore"));
			leedcontext.setWaterScore((long)scoresMap.get("waterScore"));
			leedcontext.setWasteScore((long)scoresMap.get("wasteScore"));
			leedcontext.setHumanExperienceScore((long)scoresMap.get("humanExperienceScore"));
			leedcontext.setTransportScore((long)scoresMap.get("transportScore"));
			leedcontext.setLeedScore((long)scoresMap.get("totalScore"));
			leedList.add(leedcontext);
		}
		
		LeedAPI.addLeedConfigurations(leedList);
		
		return false;
	}
	
	public HashMap getLeedScore(long leedId) throws Exception
	{
		HashMap scoreMap = new HashMap();
		JSONObject response = (JSONObject)LeedIntegrator.getPerformanceScores(leedId+"");
		JSONObject scores = (JSONObject)response.get("scores");
		System.out.println("FetchArcAssetsCommand.getLeedScore().scores :"+scores);
		long energyScore = 0;
		if(scores.get("energy") != null)
		{
			energyScore = (long)scores.get("energy");
		}
		long waterScore = 0;
		if(scores.get("water") != null)
		{
			waterScore = (long)scores.get("water");
		}
		long wasteScore = 0;
		if(scores.get("waste") != null)
		{
			wasteScore = (long)scores.get("waste");
		}
		long humanExperienceScore = 0;
		if(scores.get("human_experience") != null)
		{
			humanExperienceScore = (long)scores.get("human_experience");
		}
		long transportScore = 0;
		if(scores.get("transport") != null)
		{
			transportScore = (long)scores.get("transport");
		}
		long baseScore = 0;
		if(scores.get("base") != null)
		{
			baseScore = (long)scores.get("base");
		}
		long totalScore = energyScore+waterScore+wasteScore+humanExperienceScore+transportScore+baseScore;
		scoreMap.put("totalScore", totalScore);
		scoreMap.put("energyScore", energyScore);
		scoreMap.put("waterScore", waterScore);
		scoreMap.put("wasteScore", wasteScore);
		scoreMap.put("humanExperienceScore", humanExperienceScore);
		scoreMap.put("transportScore", transportScore);
		scoreMap.put("baseScore", baseScore);
		
		return scoreMap;
	}
	
}
