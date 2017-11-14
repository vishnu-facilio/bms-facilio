package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.AlarmContext.AlarmStatus;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class ReportActions extends ActionSupport {
	
	double unitCost=2;
	
	public String reportdata() throws Exception 
	{
		String strDurat = String.valueOf(report.get("duration"));
		int duration = Integer.valueOf(strDurat);
		String strEngery = String.valueOf(report.get("energydata"));
		int energyData =Integer.valueOf(strEngery);
		List<Long> deviceList = new ArrayList<Long>();
		Long[] deviceLong = null;
		deviceList = (ArrayList) report.get("device");
		if (deviceList!= null)
		{
		deviceLong = deviceList.toArray(new Long[0]);
		System.out.println(deviceLong);
		}
		JSONObject reportDatas=ReportsUtil.getEnergyData(energyData,duration,deviceLong);
		setReportData(reportDatas);
		return SUCCESS;
	}
	 
	public String reportDashboard() throws Exception 
	{
		Long deviceLong = (long) 4;
		JSONObject energyConsumptionMonth = ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_SUM,FacilioConstants.Reports.THIS_YEAR,deviceLong);
		JSONObject energyConsumptionThisWeek = ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_SUM,FacilioConstants.Reports.THIS_WEEK);
		JSONObject energyConsumptionLastWeek = ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_SUM,FacilioConstants.Reports.LAST_WEEK);
		JSONObject phaseEnergyConsumptionB =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA_SUM,FacilioConstants.Reports.THIS_WEEK);
		JSONObject phaseEnergyConsumptionR =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA_SUM,FacilioConstants.Reports.THIS_WEEK);
		JSONObject phaseEnergyConsumptionY =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA_SUM,FacilioConstants.Reports.THIS_WEEK);
		JSONObject powerFactorB =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.POWER_FACTOR_Y_AVERAGE,FacilioConstants.Reports.THIS_WEEK);
		JSONObject powerFactorR =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.POWER_FACTOR_Y_AVERAGE,FacilioConstants.Reports.THIS_WEEK);
		JSONObject powerFactorY =  ReportsUtil.getEnergyData(FacilioConstants.Reports.Energy.POWER_FACTOR_Y_AVERAGE,FacilioConstants.Reports.LAST_30_DAYS);
		JSONObject wholeData = new JSONObject();
		wholeData.put("energyConsumptionMonth",energyConsumptionMonth);
		wholeData.put("energyConsumptionThisWeek",energyConsumptionThisWeek);
		wholeData.put("energyConsumptionLastWeek", energyConsumptionLastWeek);
		wholeData.put("phaseEnergyConsumptionB", phaseEnergyConsumptionB);
		wholeData.put("phaseEnergyConsumptionR", phaseEnergyConsumptionR);
		wholeData.put("phaseEnergyConsumptionY", phaseEnergyConsumptionY);
		wholeData.put("powerFactorB", powerFactorB);
		wholeData.put("powerFactorR", powerFactorR);
		wholeData.put("powerFactorY", powerFactorY);
		setReportAllData(wholeData);
 		return SUCCESS;
		
	}
	
	
	public String getBuildingDetails() throws Exception
	{
		JSONObject buildingData = new JSONObject();
		
		BuildingContext building =getBuilding();
		long photoId=building.getPhotoId();
		buildingData.put("photoid", photoId);
		
		String avatarUrl=building.getAvatarUrl();
		String buildingName=building.getDisplayName();
		LocationContext location= building.getLocation();
		String cityName=location.getCity();
		String streetName=location.getStreet();
		double buildingArea=building.getGrossFloorArea();
		
		buildingData.put("avatar",avatarUrl);
		buildingData.put("name", buildingName);
		buildingData.put("city", cityName);
		buildingData.put("street", streetName);
		buildingData.put("area", buildingArea);
		
		
		//Energy Meter purpose id: Main, AHU, Lighting, Chiller, Lift, UPS..
		EnergyMeterPurposeContext empc= DeviceAPI.getEnergyMeterPurpose("Main").get(0);
		List<EnergyMeterContext> energyMeters = DeviceAPI.getAllEnergyMeters(getBuildingId(), empc.getId(), true);
		
		EnergyMeterContext energyMeter= energyMeters.get(0);
		long deviceId=energyMeter.getId();
		FacilioField monthFld = new FacilioField();
		monthFld.setName("MONTH");
		monthFld.setColumnName("TTIME_MONTH");
		monthFld.setDataType(FieldType.NUMBER);
		
		FacilioField energyFld = new FacilioField();
		energyFld.setName("KWH");
		energyFld.setColumnName("SUM(TOTAL_ENERGY_CONSUMPTION_DELTA)");
		energyFld.setDataType(FieldType.DECIMAL);
		long startTime=DateTimeUtil.getMonthStartTime(-1);
		long endTime=DateTimeUtil.getCurrenTime();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(energyFld);
		fields.add(monthFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=? AND TTIME between ? AND ?",orgId,startTime,endTime)
				.groupBy("TTIME_MONTH")
				.orderBy("TTIME_MONTH");
		List<Map<String, Object>> result = builder.get();
		
		int lastMonth=-1;
		double lastMonthKwh=-1;
		int thisMonth=-1;
		double thisMonthKwh=-1;
		
		if(result!=null && !result.isEmpty())
		{
			if(result.size()==2){
				Map<String,Object> map = result.get(0);
				lastMonth =(int)map.get("MONTH");
				lastMonthKwh = (double)map.get("KWH");
				map = result.get(1);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("KWH");		
			}
			else{
				
				Map<String,Object> map = result.get(0);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("KWH");
			}
			
		}
		
		double thisMonthCost=thisMonthKwh*unitCost;
		double lastMonthCost=lastMonthKwh*unitCost;
		
		long endTimestamp=DateTimeUtil.getMonthStartTime();
		int lastMonthDays= DateTimeUtil.getDaysBetween(startTime, endTimestamp-1);
		int thisMonthDays=DateTimeUtil.getDaysBetween(endTimestamp,endTime);
		
		
		double lastMonthAvgEUI= lastMonthKwh/buildingArea/lastMonthDays;
		double thisMonthAvgEUI=thisMonthKwh/buildingArea/thisMonthDays;
		double variance= ((thisMonthAvgEUI - lastMonthAvgEUI)/thisMonthAvgEUI)*100;
		
		JSONObject lastMonthData = new JSONObject();
		lastMonthData.put("kwh",lastMonthKwh);
		lastMonthData.put("days", lastMonthDays);
		lastMonthData.put("eui", lastMonthAvgEUI);
		lastMonthData.put("cost", lastMonthCost);
		lastMonthData.put("monthVal", lastMonth);
		
		JSONObject thisMonthData = new JSONObject();
		thisMonthData.put("kwh",thisMonthKwh);
		thisMonthData.put("days", thisMonthDays);
		thisMonthData.put("eui", thisMonthAvgEUI);
		lastMonthData.put("cost", thisMonthCost);
		thisMonthData.put("monthVal", thisMonth);
		
		buildingData.put("lastMonth", lastMonthData);
		buildingData.put("thisMonth", thisMonthData);
		buildingData.put("variance", variance);
		
		// need to send cost..as well..
		//need to send temperature & carbon emmission [
	
		setBuildingEnergyDetails(buildingData);
		
		//get the data for This month & Previous month by group by Month in one query by giving proper ttime between..
		//calculate EUI -- KWH/SqFt/No.of days for this month & last month..
		
		return SUCCESS;
		
	}
	
	
	
	private BuildingContext getBuilding() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());		
		Chain getBuildingChain = FacilioChainFactory.getBuildingDetailsChain();
		getBuildingChain.execute(context);
		return (BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING);
	}
	
	private long buildingId;
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}
	
	private JSONObject reportAllData = null;
	
	public JSONObject getReportAllData() {
		return reportAllData;
	}
	public void setReportAllData(JSONObject reportAllData) {
		this.reportAllData = reportAllData;		
	}
	
	private JSONObject buildingEnergyDetails = null;
	public void setBuildingEnergyDetails(JSONObject buildingEnergyDetails) {
		this.buildingEnergyDetails = buildingEnergyDetails;		
	}
	public JSONObject getBuildingEnergyDetails() {
		return buildingEnergyDetails;		
	}
	
	private JSONObject reportData = null;
	
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	public void setConstantEnergy() {
		
	}
	private JSONObject report;
	public  JSONObject getParamsdata() {
		return report;
	}
	public void setParamsdata(JSONObject report) {
		System.out.println("setParamsdata"+report);
		this.report = report;
	}
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	private int queryReportfilter;
	public int getQueryReportfilter() {
		return queryReportfilter;
	}
	public void setQueryReportfilter(int filterId) {
		this.queryReportfilter = filterId;
	}
	private int queryDatafilter;
	public int getQueryDatafilter() {
		return queryDatafilter;
	}
	public void setQueryDatafilter(int dataId) {
		this.queryDatafilter = dataId;
	}
}