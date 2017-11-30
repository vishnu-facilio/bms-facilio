package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.opensymphony.xwork2.ActionSupport;

public class PortfolioAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	
	
	@SuppressWarnings("unchecked")
	public String getAllBuildings() throws Exception
	{
		JSONObject result = new JSONObject();
		long sitesCount = SpaceAPI.getSitesCount();
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		result.put("sitesCount", sitesCount);
		result.put("buildingCount", buildings.size());
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, Long> meterVsBuilding= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			meterVsBuilding.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		long prevStartTime=DateTimeUtil.getMonthStartTime(-1);
		long currentStartTime=DateTimeUtil.getMonthStartTime();
		long endTime=DateTimeUtil.getCurrenTime();

		List<Map<String, Object>> prevResult= ReportsUtil.fetchMeterData(deviceList,prevStartTime,currentStartTime-1,false);
		Map<Long,Double> prevMeterVsConsumption=ReportsUtil.getMeterVsConsumption(prevResult);
		//going for two queries.. so that it will be easy while going for separate queries in case of caching url..
		List<Map<String, Object>> currentResult= ReportsUtil.fetchMeterData(deviceList,currentStartTime,endTime,false);
		Map<Long,Double> currentMeterVsConsumption=ReportsUtil.getMeterVsConsumption(currentResult);
		
		int lastMonthDays= DateTimeUtil.getDaysBetween(prevStartTime, currentStartTime);//sending this month startTime as to time is excluded
		int thisMonthDays=DateTimeUtil.getDaysBetween(currentStartTime,endTime)+1; //adding 1 as it to time is excluded..
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long buildingId=building.getId();
			long meterId=meterVsBuilding.get(buildingId);
			Double lastMonthKwh=prevMeterVsConsumption.get(meterId);
			Double thisMonthKwh=currentMeterVsConsumption.get(meterId);
			JSONObject lastMonthData = ReportsUtil.getMonthData(lastMonthKwh,lastMonthDays);
			buildingData.put("previousVal", lastMonthData);
			JSONObject thisMonthData = ReportsUtil.getMonthData(thisMonthKwh,thisMonthDays);
			buildingData.put("currentVal", thisMonthData);
			
			if(lastMonthKwh!=null || thisMonthKwh!=null)
			{
				double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
				buildingData.put("variance", variance);
			}
			buildingArray.add(buildingData);
		}
		result.put("buildingDetails", buildingArray);
		setReportData(result);
		return SUCCESS;
	}
	
	//period -year & lastYear -EUI ranking
	@SuppressWarnings("unchecked")
	public String getBuildingRankings() throws Exception
	{
		JSONObject result = new JSONObject();
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, Long> meterVsBuilding= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			meterVsBuilding.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		
		String period=getPeriod();
		
		if(period.equals("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		else if(period.equals("lastYear"))
		{
			startTime=DateTimeUtil.getYearStartTime(-1);
			endTime=DateTimeUtil.getYearStartTime()-1;
		}
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,false);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		
		Map<String,Double> euiMap = new LinkedHashMap<String,Double>();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			long meterId=meterVsBuilding.get(buildingId);
			Double currentKwh= meterVsConsumption.get(meterId);
			String displayName=building.getDisplayName();
			double buildingArea=building.getGrossFloorArea();
			if(!(currentKwh==null || currentKwh==0))
			{
				euiMap.put(displayName, ReportsUtil.getEUI(currentKwh,buildingArea));
			}
		}
		if(!euiMap.isEmpty())
		{
			result.put("buildingDetails",ReportsUtil.valueSort(euiMap,false));
			result.put("units", "kBTU/sq.ft");
		}
		setReportData(result);
		return SUCCESS;
	}
	
	
	//period - year/day/week/month
	// use this for Building Consumption Stats & Consumption vs Buildings
	@SuppressWarnings("unchecked")
	public String getConsumptionDetails() throws Exception
	{
		JSONObject result = new JSONObject();
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, Long> meterVsBuilding= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			meterVsBuilding.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		
		String period=getPeriod();
		
		if(period.equals("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		else if(period.equals("day"))
		{
			startTime=DateTimeUtil.getDayStartTime();
		}
		else if(period.equals("week"))
		{
			startTime=DateTimeUtil.getWeekStartTime();
		}
		else if(period.equals("month"))
		{
			startTime=DateTimeUtil.getMonthStartTime();
		}
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,false);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long meterId=meterVsBuilding.get(buildingId);
			Double currentKwh=meterVsConsumption.get(meterId);
			JSONObject currentData = ReportsUtil.getMonthData(currentKwh,-1);
			buildingData.put("currentVal", currentData);
			buildingArray.add(buildingData);
		}
		result.put("buildingDetails", buildingArray);
		setReportData(result);
		return SUCCESS;
	}
	



	private long purpose=-1;
	public long getPurpose() 
	{
		return purpose;
	}
	
	public void setPurpose(long purpose) 
	{
		this.purpose = purpose;
	}
	
	private String period="day";
	
	public void setPeriod(String period)
	{
		this.period=period;
	}
	
	public String getPeriod()
	{
		return this.period;
	}
	
	private String type="";
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type=type;
	}
	
	private JSONObject reportData = null;
	
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	
}