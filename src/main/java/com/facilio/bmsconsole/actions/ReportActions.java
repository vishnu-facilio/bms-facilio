package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
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
	
	//time series data..hardocded for last month..
	//need building id, 
	// need period to be set to week for By Week break down..
	// if period not set , by date 
	public String getServiceBreakDown() throws Exception
	{
		JSONObject resultJson = new JSONObject();
		HashMap<Long,String> purposeMapping= getPurposeMapping(getBuildingId(),true);
		
		Set<Long> keys=purposeMapping.keySet();
		String deviceList=StringUtils.join(keys, ",");
		String duration = getPeriod();
		long startTime=DateTimeUtil.getMonthStartTime();
	    long endTime=DateTimeUtil.getCurrenTime();
	    
	    List<FacilioField> fields = new ArrayList<FacilioField>() ;
		if(duration.equals("week"))
		{
			FacilioField dayFld = getField("DAY","TTIME_DAY",FieldType.NUMBER);
			fields.add(dayFld);
		}
		
		FacilioField meterFld = getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
		FacilioField timeFld = getField("DATE","TTIME_DATE",FieldType.NUMBER);
		fields.add(meterFld);
		fields.add(timeFld);
		
		List<Map<String, Object>> current=getGroupByData(deviceList,startTime, endTime, fields);
		fields = new ArrayList<FacilioField>() ;
		List<Map<String, Object>> total=getData(deviceList,startTime, endTime, fields,false);
		if(total!=null & !total.isEmpty()) {
			Map<String,Object> currentTotal=total.get(0);
			double currentKwh = (double)currentTotal.get("consumption");
			resultJson.put("totalkwh", currentKwh);
		}
		  
		resultJson.put("currentVal", current);
		resultJson.put("mapping", purposeMapping);
		
		setReportAllData(resultJson);
		
		return SUCCESS;
	}
	
	//no time series..
	//needs period, building id..
	public String getServiceConsumption() throws Exception
	{
		JSONObject resultJson = new JSONObject();
		
		HashMap<Long,String> purposeMapping= getPurposeMapping(getBuildingId(),true);
		Set<Long> keys=purposeMapping.keySet();
		String deviceList=StringUtils.join(keys, ",");
		String duration = getPeriod();
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		
		FacilioField selectFld = getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
				
		if(duration.equals("today"))
		{
			startTime=DateTimeUtil.getDayStartTime();
		}
		else if (duration.equals("week"))
		{
			startTime=DateTimeUtil.getWeekStartTime();	
		}
		else if (duration.equals("month"))
		{
			startTime=DateTimeUtil.getMonthStartTime();

		}
		else if (duration.equals("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		
		List<FacilioField> fields = new ArrayList<FacilioField>() ;
		fields.add(selectFld);
		
		
		List<Map<String, Object>> current=getData(deviceList,startTime, endTime, fields,false);
		resultJson.put("currentVal", current);
		resultJson.put("mapping", purposeMapping);
		setReportAllData(resultJson);
		return SUCCESS;
	}
	
	private HashMap<Long,String> getPurposeMapping(long buildingId,boolean root) throws Exception {
		
		HashMap<Long,String> deviceMapping = new LinkedHashMap<Long,String>();
		FacilioField meterFld = getField("Meter","Energy_Meter.ID",FieldType.NUMBER);
		FacilioField purposeField =getField("Name","Energy_Meter_Purpose.NAME",FieldType.STRING);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(meterFld);
		fields.add(purposeField);
		//hardocding...
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Meter")
				.innerJoin("Energy_Meter_Purpose")
				.on("Energy_Meter.PURPOSE_ID = Energy_Meter_Purpose.ID")
				.andCustomWhere("Energy_Meter.ORGID= ? AND Energy_Meter_Purpose.NAME != ? "
						+ "AND Energy_Meter.PURPOSE_SPACE_ID =? AND Energy_Meter.IS_ROOT=?", orgId,"Main",buildingId,root);
		List<Map<String, Object>> stats = builder.get();
		for(Map<String, Object> rowData: stats) {

			long meterId=(long)rowData.get("Meter");
			String purposeName=(String)rowData.get("Name");
			deviceMapping.put(meterId, purposeName);
		}
		return deviceMapping;
	}
		
		
	

	//needs building id & period like today/week/month
	// needs service id like 'Main/AHU/Chiller/Lighting/Lift
	// time series data..
	public String getEnergyConsumption() throws Exception
	{
		getRootConsumption(getPurpose());
		return SUCCESS;
	}
	
	
	//time series data..
	private void getRootConsumption(String purpose) throws Exception
	{
		JSONObject consumptionData = new JSONObject();
		long deviceId =getRootMeter(purpose);
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		long previousStartTime=-1;
		long previousEndTime=-1;
		
		List<FacilioField> fields = new ArrayList<FacilioField>() ;
		
		String duration= getPeriod();
		String displayName="HOUR";
		String colName="TTIME_HOUR";
		
		if(duration.equals("today"))
				{
				startTime=DateTimeUtil.getDayStartTime();
				previousStartTime=DateTimeUtil.getDayStartTime(-1);
				previousEndTime=startTime-1;
				}
		else if (duration.equals("week"))
		{
			displayName="DATE";
			colName="TTIME_DATE";
			startTime=DateTimeUtil.getWeekStartTime();
			previousStartTime=DateTimeUtil.getWeekStartTime(-1);
			previousEndTime=startTime-1;
			
		}
		else if (duration.equals("month"))
		{
			displayName="DATE";
			colName="TTIME_DATE";
			startTime=DateTimeUtil.getMonthStartTime();
			previousStartTime=DateTimeUtil.getMonthStartTime(-1);
			previousEndTime=startTime-1;
		}
		else if (duration.equals("year"))
		{
			displayName="YEAR";
			colName="TTIME_MONTH";
			startTime=DateTimeUtil.getYearStartTime();
			previousStartTime=DateTimeUtil.getYearStartTime(-1);
			previousEndTime=startTime-1;
		}
		FacilioField periodFld = getField(displayName,colName, FieldType.NUMBER);
		periodFld.setName(displayName);
		
		fields.add(periodFld);
		
		
		double currentKwh=-1;
		double previousKwh=-1;
		//send deviceid..
		List<Map<String, Object>> current=getData(String.valueOf(deviceId),startTime, endTime, fields,true);
		List<Map<String, Object>> previous=getData(String.valueOf(deviceId),previousStartTime, previousEndTime, fields, true);
		if(current!=null & !current.isEmpty()) {
			Map<String,Object> currentTotal=current.remove(current.size()-1);
			currentKwh = (double)currentTotal.get("consumption");
		}
		if(previous!=null & !previous.isEmpty()) {
			Map<String,Object> previousTotal=previous.remove(previous.size()-1);
			previousKwh = (double)previousTotal.get("consumption");
		}
		consumptionData.put("currentVal", current);
		consumptionData.put("previousVal", previous);
		consumptionData.put("currentTotal", currentKwh);
		consumptionData.put("previousTotal", previousKwh);
		consumptionData.put("units", "kWh");
		double variance=getVariance(currentKwh, previousKwh);
		consumptionData.put("variance", variance);
		
		setReportAllData(consumptionData);
	}

	

	private List<Map<String, Object>> getGroupByData( String deviceList, long startTime, long endTime, List<FacilioField> fields) throws Exception {
		
		
		StringBuilder groupBy= new StringBuilder();		
		for(FacilioField field: fields)
		{
			groupBy.append(field.getName());
			groupBy.append(",");
		}
		groupBy=groupBy.deleteCharAt(groupBy.lastIndexOf(","));
		fields.add(getEnergyField());
		fields.add(getField("TIME","ANY_VALUE(TTIME)",FieldType.NUMBER));
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=? AND TTIME between ? AND ?",orgId,startTime,endTime)
				.andCondition(getDeviceListCondition(deviceList))
				.groupBy(groupBy.toString());
		return builder.get();
	}
	
private List<Map<String, Object>> getData( String deviceList, long startTime, long endTime, List<FacilioField> fields, boolean rollUp ) throws Exception {
		
		
		StringBuilder groupBy=new StringBuilder();
		if(!fields.isEmpty())
		{
			groupBy=groupBy.append(fields.get(0).getName());
			fields.add(getField("TIME","ANY_VALUE(TTIME)",FieldType.NUMBER));
		}
		
		FacilioField energyFld = getEnergyField();
		fields.add(energyFld);
		
		if(rollUp)
		{
			groupBy.append(" WITH ROLLUP");
		}
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=? AND TTIME between ? AND ?",orgId,startTime,endTime)
				.andCondition(getDeviceListCondition(deviceList))
				.groupBy(groupBy.toString());
		return builder.get();
	}

private FacilioField getEnergyField() {
	FacilioField energyFld = new FacilioField();
	energyFld.setName("consumption");
	energyFld.setColumnName("ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2)");
	energyFld.setDataType(FieldType.DECIMAL);
	return energyFld;
}

private FacilioField getField(String name, String colName, FieldType type) {
	FacilioField energyFld = new FacilioField();
	energyFld.setName(name);
	energyFld.setColumnName(colName);
	energyFld.setDataType(type);
	return energyFld;
}



	private long getRootMeter(String purpose) throws Exception {
		 List<EnergyMeterPurposeContext> purposeList= DeviceAPI.getEnergyMeterPurpose(purpose);
		if(purposeList==null || purposeList.size()==0)
		{
			return -1;
		}
		
		
		EnergyMeterPurposeContext empc= purposeList.get(0);
		List<EnergyMeterContext> energyMeters = DeviceAPI.getAllEnergyMeters(getBuildingId(), empc.getId(), true);
		
		if(energyMeters==null || energyMeters.size()==0)
		{
			return -1;
		}
		EnergyMeterContext energyMeter= energyMeters.get(0);
		return energyMeter.getId();
	}
	
	
	private double getVariance(Double currentVal, Double previousVal)
	{
		double variance =(currentVal - previousVal)/currentVal;
		variance=Math.round(variance*100)/ 100;
		return variance*100;
	}
	
	// needs building id, duration,
	// no time series data...
	public String getTopNSpaces() throws Exception
	{
		JSONObject resultData = new JSONObject();
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		String duration = getPeriod();
		if(duration.equals("today"))
		{
		startTime=DateTimeUtil.getDayStartTime();
		}
		else if (duration.equals("week"))
		{
			startTime=DateTimeUtil.getWeekStartTime();	
		}
		else if (duration.equals("month"))
		{
			startTime=DateTimeUtil.getMonthStartTime();
			
		}
		else if (duration.equals("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		
		List<BaseSpaceContext> floors=SpaceAPI.getBuildingFloors(getBuildingId());
		Map<String,Double> result = new LinkedHashMap<String,Double>();
		for(BaseSpaceContext floor:floors)
		{
			String floorName=floor.getName();
			List<EnergyMeterContext> meterList= DeviceAPI.getAllEnergyMeters(floor.getId(), false);
			StringBuilder deviceList= new StringBuilder();
			for(EnergyMeterContext emc:meterList) {
				
				deviceList.append(emc.getId());
				deviceList.append(",");
			}
			if(deviceList.length()!=0)
			{
				deviceList.deleteCharAt(deviceList.lastIndexOf(","));
			}
			List<FacilioField> fields = new ArrayList<FacilioField>() ;
			List<Map<String, Object>> total=getData(deviceList.toString(),startTime, endTime, fields,false);
			if(total!=null && !total.isEmpty())
			{
				 Map<String,Object> rowData=total.get(0);
				 Double totalKwh=(Double)rowData.get("consumption");
				 result.put(floorName, totalKwh);
			}
		}
		if(result!=null && !result.isEmpty())
		{
			resultData.put("currentVal", valueSort(result,true));
		}
		setReportAllData(resultData);
		return SUCCESS;
	}
	
	
	private static <K, V extends Comparable<? super V>> Map<K, V> valueSort(Map<K, V> map, boolean descending) {
	   
		Stream<Entry<K,V>> stream= map.entrySet().stream();
		
		if(descending){
			
			stream =stream.sorted(Map.Entry.comparingByValue(Collections.reverseOrder()));
		}
		else {
			stream=stream.sorted(Map.Entry.comparingByValue());	
		}
		
		return stream.collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new
              ));
	}
	
	
	public String getBuildingDetails() throws Exception
	{
		JSONObject buildingData = new JSONObject();
		
		BuildingContext building =SpaceAPI.getBuildingSpace(getBuildingId());
		int floors=building.getNoOfFloors();
		buildingData.put("floors", floors);
		long photoId=building.getPhotoId();
		buildingData.put("photoid", photoId);
		
		String avatarUrl=building.getAvatarUrl();
		String buildingName=building.getName();
		String displayName=building.getDisplayName();
		LocationContext location= building.getLocation();
		if(location!=null){
			String cityName=location.getCity();
			String streetName=location.getStreet();
			buildingData.put("city", cityName);
			buildingData.put("street", streetName);
		}
		
		double buildingArea=building.getGrossFloorArea();
		buildingData.put("avatar",avatarUrl);
		buildingData.put("name", buildingName);
		buildingData.put("displayName", displayName);
		buildingData.put("area", buildingArea);
		//hardcoding..
		long deviceId=getRootMeter("Main");
		
		FacilioField monthFld = new FacilioField();
		monthFld.setName("MONTH");
		monthFld.setColumnName("TTIME_MONTH");
		monthFld.setDataType(FieldType.NUMBER);
		
		FacilioField energyFld = getEnergyField();
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
				.andCondition(getDeviceListCondition(""+deviceId))
				.groupBy("TTIME_MONTH");
		List<Map<String, Object>> result = builder.get();
		
		int lastMonth=-1;
		int thisMonth=-1;
		double lastMonthKwh=-1;
		double thisMonthKwh=-1;
		
		if(result!=null && !result.isEmpty())
		{
			if(result.size()==2){
				Map<String,Object> map = result.get(0);
				lastMonth =(int)map.get("MONTH");
				lastMonthKwh = (double)map.get("consumption");
				map = result.get(1);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("consumption");		
			}
			else{
				
				Map<String,Object> map = result.get(0);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("consumption");
			}
			
		}
		
		double thisMonthCost=thisMonthKwh*unitCost;
		double lastMonthCost=lastMonthKwh*unitCost;
		
		long endTimestamp=DateTimeUtil.getMonthStartTime();
		int lastMonthDays= DateTimeUtil.getDaysBetween(startTime, endTimestamp-1);
		int thisMonthDays=DateTimeUtil.getDaysBetween(endTimestamp,endTime);
		
		
		//double lastMonthAvgEUI= lastMonthKwh/buildingArea/lastMonthDays;
		//double thisMonthAvgEUI=thisMonthKwh/buildingArea/thisMonthDays;
		
		double variance= getVariance(thisMonthKwh, lastMonthKwh);
		
		JSONObject lastMonthData = new JSONObject();
		lastMonthData.put("kwh",lastMonthKwh);
		lastMonthData.put("days", lastMonthDays);
		lastMonthData.put("units","kWh");
		lastMonthData.put("currency","$");
		//lastMonthData.put("eui", lastMonthAvgEUI);
		lastMonthData.put("cost", lastMonthCost);
		lastMonthData.put("monthVal", lastMonth);
		
		JSONObject thisMonthData = new JSONObject();
		thisMonthData.put("kwh",thisMonthKwh);
		thisMonthData.put("days", thisMonthDays);
		thisMonthData.put("units","kWh");
		thisMonthData.put("currency","$");
		//thisMonthData.put("eui", thisMonthAvgEUI);
		thisMonthData.put("cost", thisMonthCost);
		thisMonthData.put("monthVal", thisMonth);
		
		buildingData.put("previousVal", lastMonthData);
		buildingData.put("currentVal", thisMonthData);
		buildingData.put("variance", variance);
		buildingData.put("purpose", DeviceAPI.getAllEnergyMeterPurpose());
		
		// need to send cost..as well..
		//need to send temperature & carbon emmission [
	
		setReportAllData(buildingData);
		
		//get the data for This month & Previous month by group by Month in one query by giving proper ttime between..
		//calculate EUI -- KWH/SqFt/No.of days for this month & last month..
		
		return SUCCESS;
		
	}
	
	
	private Condition getDeviceListCondition (String deviceList)
	{
		return DeviceAPI.getCondition("PARENT_METER_ID", deviceList, NumberOperators.EQUALS);
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
	
	//hardcoding..
	private String purpose="Main";
	public String getPurpose() 
	{
		return purpose;
	}
	
	public void setPurpose(String purpose) 
	{
		this.purpose = purpose;
	}
	
	private String period="today";
	
	public void setPeriod(String period)
	{
		this.period=period;
	}
	
	public String getPeriod()
	{
		return this.period;
	}
	private JSONObject reportAllData = null;
	
	public JSONObject getReportAllData() {
		return reportAllData;
	}
	public void setReportAllData(JSONObject reportAllData) {
		this.reportAllData = reportAllData;		
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