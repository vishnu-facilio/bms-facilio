package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
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
			//under the assumption only one main meter for a building.. 
			//when there are more than 1 main meter, there should be one virtual meter which will be the main meter..
			meterVsBuilding.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		long prevStartTime=DateTimeUtil.getMonthStartTime(-1);
		long currentStartTime=DateTimeUtil.getMonthStartTime();
		long endTime=DateTimeUtil.getCurrenTime();

		List<Map<String, Object>> prevResult= ReportsUtil.fetchMeterData(deviceList,prevStartTime,currentStartTime-1,true);
		Map<Long,Double> prevMeterVsConsumption=ReportsUtil.getMeterVsConsumption(prevResult);
		//going for two queries.. so that it will be easy while going for separate queries in case of caching url..
		List<Map<String, Object>> currentResult= ReportsUtil.fetchMeterData(deviceList,currentStartTime,endTime,true);
		Map<Long,Double> currentMeterVsConsumption=ReportsUtil.getMeterVsConsumption(currentResult);
		
		int lastMonthDays= DateTimeUtil.getDaysBetween(prevStartTime,currentStartTime);//sending this month startTime as to time is excluded
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
		HashMap <Long, Long> buildingVsMeter= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			buildingVsMeter.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		
		Map<String,Double> euiMap = new LinkedHashMap<String,Double>();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			long meterId=buildingVsMeter.get(buildingId);
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
		HashMap <Long, Long> buildingVsMeter= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			buildingVsMeter.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long meterId=buildingVsMeter.get(buildingId);
			Double currentKwh=meterVsConsumption.get(meterId);
			JSONObject currentData = ReportsUtil.getMonthData(currentKwh,-1);
			buildingData.put("currentVal", currentData);
			buildingArray.add(buildingData);
		}
		result.put("buildingDetails", buildingArray);
		setReportData(result);
		return SUCCESS;
	}
	
	// period -day/week/month/year
	@SuppressWarnings("unchecked")
	public String getAllServiceConsumption() throws Exception
	{
		JSONObject result = new JSONObject();
		
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllServiceMeters();
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, ArrayList<Long>> purposeVsMeter= new HashMap<Long,ArrayList<Long>>();
		
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long purposeId =emc.getPurpose().getId();
			ArrayList<Long> meterList=purposeVsMeter.get(purposeId);
			if(meterList==null)
			{
				meterList=new ArrayList<Long>();
				purposeVsMeter.put(purposeId, meterList);
			}
			meterList.add(meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];

		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		
		for (Map.Entry<Long,ArrayList<Long>> entry:purposeVsMeter.entrySet())
		{
			Long purposeId=entry.getKey();
			ArrayList<Long> meterList=entry.getValue();
			Double totalKwh=new Double(0);
			for(Long meter:meterList)
			{
				Double value=meterVsConsumption.get(meter);
				if(value!=null)
				{
					totalKwh+=value;
				}
			}
			result.put(purposeId, ReportsUtil.roundOff(totalKwh,2));
		}
		result.put("units", "kWh");
		setReportData(result);
		return SUCCESS;
	}

	// period -day/week/month/year
	// purpose - id for purposes like LIFT, AHU, Lighting etc..
	@SuppressWarnings("unchecked")
	public String getServiceConsumption() throws Exception
	{
		JSONObject result = new JSONObject();
		List<EnergyMeterContext> energyMeters =DeviceAPI.getEnergyMetersOfPurpose(""+getPurpose(),true);
		
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, Long> buildingVsMeter= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			buildingVsMeter.put(buildingId,meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true,true);
		double totalKwh=0;
		if(resultData!=null & !resultData.isEmpty()) {
			Map<String,Object> currentTotal=resultData.remove(resultData.size()-1);
			totalKwh = (double)currentTotal.get("CONSUMPTION");
		}
		
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumptionPercentage(resultData,totalKwh);
		for(Map.Entry<Long, Long> entry:buildingVsMeter.entrySet()) {
			
			long meterId=entry.getValue();
			long buildingId=entry.getKey();
			Double percentage=meterVsConsumption.get(meterId);
			if(percentage!=null)
			{
				result.put(buildingId,ReportsUtil.roundOff(percentage, 2));
			}
		}
		result.put("units", "%");
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
	
	private JSONObject reportData = null;
	
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	
}