package com.facilio.leed.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;

public class FetchArcAssetsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ArcContext arccontext = LeedAPI.getArcContext();
		LeedIntegrator integ = new LeedIntegrator(arccontext);
		JSONObject responseJSON = integ.getAssetList();
		JSONObject response = (JSONObject)responseJSON.get("message");
		System.out.println("FetchArcAssetsCommand.getAssetList().response :"+response);
		
		response.get("count");
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
				//lat = (double)asset.get("geoLat");
				lat = Double.parseDouble((String)asset.get("geoLat"));
			}
			double lng =0.0;
			if(asset.get("geoLang") != null) {
				//lng = (double)asset.get("geoLang");
				lng = Double.parseDouble((String)asset.get("geoLang"));
			}
			
			asset.get("scores");
			
			
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
			leedcontext.setSpaceType(SpaceType.BUILDING);
			leedcontext.setLeedId(leedId);
			leedcontext.setBuildingStatus(buildingStatus);
			leedcontext.setGrossFloorArea(Double.parseDouble(area.toString()));
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
		long energyScore = 0;
		long waterScore = 0;
		long wasteScore = 0;
		long humanExperienceScore = 0;
		long transportScore = 0;
		long baseScore = 0;
		long totalScore = 0;
		
		HashMap scoreMap = new HashMap();
		ArcContext arccontext = LeedAPI.getArcContext();
		LeedIntegrator integ = new LeedIntegrator(arccontext);
		JSONObject response = integ.getPerformanceScores(leedId+"");
		JSONObject scoMsg = (JSONObject)response.get("message");
		String errResult = (String)scoMsg.get("result");
		if(errResult != null && errResult.equalsIgnoreCase("No result found."))
		{
		}
		else
		{
		JSONObject scores = (JSONObject)scoMsg.get("scores");
		System.out.println("FetchArcAssetsCommand.getLeedScore().scores :"+scores);
		
		if(scores.get("energy") != null)
		{
			energyScore = (long)scores.get("energy");
		}
		
		if(scores.get("water") != null)
		{
			waterScore = (long)scores.get("water");
		}
		
		if(scores.get("waste") != null)
		{
			wasteScore = (long)scores.get("waste");
		}
		
		if(scores.get("human_experience") != null)
		{
			humanExperienceScore = (long)scores.get("human_experience");
		}
		
		if(scores.get("transport") != null)
		{
			transportScore = (long)scores.get("transport");
		}
		
		if(scores.get("base") != null)
		{
			baseScore = (long)scores.get("base");
		}
		totalScore = energyScore+waterScore+wasteScore+humanExperienceScore+transportScore+baseScore;
		}
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
