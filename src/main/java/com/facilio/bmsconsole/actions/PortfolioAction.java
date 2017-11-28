package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.opensymphony.xwork2.ActionSupport;

public class PortfolioAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	
	
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

		List<Map<String, Object>> prevResult= ReportsUtil.fetchMothlyData(deviceList,prevStartTime,currentStartTime-1,false);
		Map<Long,Double> prevMeterVsConsumption=ReportsUtil.getMeterVsConsumption(prevResult);
		
		List<Map<String, Object>> currentResult= ReportsUtil.fetchMothlyData(deviceList,currentStartTime,endTime,false);
		Map<Long,Double> currentMeterVsConsumption=ReportsUtil.getMeterVsConsumption(currentResult);
		int lastMonthDays= DateTimeUtil.getDaysBetween(prevStartTime, currentStartTime-1);
		int thisMonthDays=DateTimeUtil.getDaysBetween(currentStartTime,endTime);
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long buildingId=building.getBuildingId();
			long meterId=meterVsBuilding.get(buildingId);
			double lastMonthKwh= prevMeterVsConsumption.get(meterId);
			double thisMonthKwh= currentMeterVsConsumption.get(meterId);
			double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
			JSONObject lastMonthData = ReportsUtil.getMonthData(lastMonthKwh,lastMonthDays);
			JSONObject thisMonthData = ReportsUtil.getMonthData(thisMonthKwh,thisMonthDays);
			buildingData.put("previousVal", lastMonthData);
			buildingData.put("currentVal", thisMonthData);
			buildingData.put("variance", variance);
			buildingArray.add(buildingData);
		}
		result.put("buildingDetails", buildingArray);
		setReportData(result);
		return SUCCESS;
	}
	
	
	
	
	private Condition getDeviceListCondition (String deviceList)
	{
		return DeviceAPI.getCondition("PARENT_METER_ID", deviceList, NumberOperators.EQUALS);
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