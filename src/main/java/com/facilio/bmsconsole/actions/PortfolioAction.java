package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SiteContext;
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
		List<SiteContext> sites = SpaceAPI.getAllSites();
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		result.put("sitesCount", sites.size());
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
		
		List<Map<String, Object>> currentResult= ReportsUtil.fetchMeterData(deviceList,currentStartTime,endTime,false);
		Map<Long,Double> currentMeterVsConsumption=ReportsUtil.getMeterVsConsumption(currentResult);
		int lastMonthDays= DateTimeUtil.getDaysBetween(prevStartTime, currentStartTime);
		int thisMonthDays=DateTimeUtil.getDaysBetween(currentStartTime,endTime)+1;
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long buildingId=building.getId();
			long meterId=meterVsBuilding.get(buildingId);
			double lastMonthKwh=0;
			double thisMonthKwh=0;
			if(prevMeterVsConsumption.containsKey(meterId)){
				lastMonthKwh= prevMeterVsConsumption.get(meterId);
				JSONObject lastMonthData = ReportsUtil.getMonthData(lastMonthKwh,lastMonthDays);
				buildingData.put("previousVal", lastMonthData);
			}
			if(currentMeterVsConsumption.containsKey(meterId)){
				thisMonthKwh= currentMeterVsConsumption.get(meterId);
				JSONObject thisMonthData = ReportsUtil.getMonthData(thisMonthKwh,thisMonthDays);
				buildingData.put("currentVal", thisMonthData);
			}
			if(lastMonthKwh!=0 || thisMonthKwh!=0)
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
	//type - ranking
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
		else if(period.equals("lastYear"))
		{
			startTime=DateTimeUtil.getYearStartTime(-1);
			endTime=DateTimeUtil.getYearStartTime()-1;
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
		
		String type=getType();
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,false);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		
		JSONArray buildingArray= new JSONArray ();
		Map<String,Double> euiMap = new LinkedHashMap<String,Double>();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			long meterId=meterVsBuilding.get(buildingId);
			double currentKwh=0;
			if(meterVsConsumption.containsKey(meterId)){
				currentKwh= meterVsConsumption.get(meterId);
			}
			
			if(type.equals("ranking"))
			{
				String displayName=building.getDisplayName();
				double buildingArea=building.getGrossFloorArea();
				if(currentKwh!=0)
				{
					euiMap.put(displayName, ReportsUtil.getEUI(currentKwh,buildingArea));
				}
			}
			else
			{
				JSONObject buildingData=ReportsUtil.getBuildingData(building);
				if(currentKwh!=0)
				{
					JSONObject currentData = ReportsUtil.getMonthData(currentKwh,-1);
					buildingData.put("currentVal", currentData);
				}
				buildingArray.add(buildingData);
			}
		}
		if(type.equals("ranking"))
		{
			if(!euiMap.isEmpty())
			{
				result.put("buildingDetails",ReportsUtil.valueSort(euiMap,false));
				result.put("units", "kBTU/sq.ft");
			}
		}
		else
		{
			result.put("buildingDetails", buildingArray);
		}
		setReportData(result);
		return SUCCESS;
	}
	
	
	//period - year/day/week/month
	@SuppressWarnings("unchecked")
	public String getAllConsumption() throws Exception
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
		
		JSONObject buildingList= new JSONObject ();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			long meterId=meterVsBuilding.get(buildingId);
			double currentKwh=0;
			if(meterVsConsumption.containsKey(meterId)){
				currentKwh= meterVsConsumption.get(meterId);
				buildingList.put(building.getDisplayName(), currentKwh);
			}
		}
		
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