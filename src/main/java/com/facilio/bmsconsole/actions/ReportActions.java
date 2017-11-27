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

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
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
	
	//time series data....
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
			double currentMWh = (double)currentTotal.get("CONSUMPTION")/1000;
			resultJson.put("totalMWh", currentMWh);
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
				
		if(duration.equals("day"))
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
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Meter")
				.innerJoin("Energy_Meter_Purpose")
				.on("Energy_Meter.PURPOSE_ID = Energy_Meter_Purpose.ID")
				.innerJoin("Assets")
				.on("Energy_Meter.ID = Assets.ID")
				.andCustomWhere("Energy_Meter.ORGID= ?",orgId) 
				.andCustomWhere("Energy_Meter.PURPOSE_SPACE_ID =?",buildingId)
				.andCustomWhere("Energy_Meter.IS_ROOT=?",root)
				.andCustomWhere("Assets.PARENT_ASSET_ID IS NOT NULL");
		List<Map<String, Object>> stats = builder.get();
		for(Map<String, Object> rowData: stats) {

			long meterId=(long)rowData.get("Meter");
			String purposeName=(String)rowData.get("Name");
			deviceMapping.put(meterId, purposeName);
		}
		return deviceMapping;
	}
		
		
	

	//needs building id & period like day/week/month
	// needs purpose id  for 'AHU/Chiller/Lighting/Lift..
	// for Main or root meters no need to set the purpose id..
	// time series data..
	public String getEnergyConsumption() throws Exception
	{
		
		long purposeId=getPurpose();
		long buildingId=getBuildingId();
		String deviceId=null;
		if(purposeId==-1)
		{//this means root meter..
			List<EnergyMeterContext> rootMeterList =	DeviceAPI.getRootEnergyMeter(""+buildingId);
			deviceId=getDeviceList(rootMeterList);
		}
		else
		{
			deviceId =getRootMeterList(""+purposeId,""+buildingId);
		}
		getRootConsumption(deviceId);
		return SUCCESS;
	}
	
	
	//time series data..
	private void getRootConsumption(String deviceId) throws Exception
	{
		JSONObject consumptionData = new JSONObject();
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		long previousStartTime=-1;
		long previousEndTime=-1;
		
		List<FacilioField> fields = new ArrayList<FacilioField>() ;
		
		String duration= getPeriod();
		String displayName="HOUR";
		String colName="TTIME_HOUR";
		
		if(duration.equals("day"))
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
		
		
		double currentMWh=-1;
		double previousMWh=-1;
		//send deviceid..
		List<Map<String, Object>> current=getData(deviceId,startTime, endTime, fields,true);
		List<Map<String, Object>> previous=getData(deviceId,previousStartTime, previousEndTime, fields, true);
		if(current!=null & !current.isEmpty()) {
			Map<String,Object> currentTotal=current.remove(current.size()-1);
			currentMWh = (double)currentTotal.get("CONSUMPTION")/1000;
		}
		if(previous!=null & !previous.isEmpty()) {
			Map<String,Object> previousTotal=previous.remove(previous.size()-1);
			previousMWh = (double)previousTotal.get("CONSUMPTION")/1000;
		}
		consumptionData.put("currentVal", current);
		consumptionData.put("previousVal", previous);
		consumptionData.put("currentTotal", currentMWh);
		consumptionData.put("previousTotal", previousMWh);
		consumptionData.put("units", "MWh");
		double variance=getVariance(currentMWh, previousMWh);
		consumptionData.put("variance", variance);
		
		setReportAllData(consumptionData);
	}

	

	private List<Map<String, Object>> getGroupByData( String deviceList, long startTime, long endTime, List<FacilioField> fields) throws Exception {
		
		
		StringBuilder groupBuilder= new StringBuilder();		
		for(FacilioField field: fields)
		{
			groupBuilder.append(field.getName());
			groupBuilder.append(",");
		}
		String groupBy= removeLastChar(groupBuilder,",");
		
		fields.add(getEnergyField());
		fields.add(getField("TIME","MAX(TTIME)",FieldType.NUMBER));
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=? AND TTIME between ? AND ?",orgId,startTime,endTime)
				.andCondition(getDeviceListCondition(deviceList))
				.groupBy(groupBy);
		return builder.get();
	}
	
	
	private String removeLastChar(StringBuilder builder, String deleteChar)
	{
		int index=builder.lastIndexOf(deleteChar);
		if(index==-1)
		{
			return builder.toString();
		}
		return builder.deleteCharAt(index).toString();
	}
	
	private List<Map<String, Object>> getData( String deviceList, long startTime, long endTime, List<FacilioField> fields, boolean rollUp ) throws Exception {


		StringBuilder groupBy=new StringBuilder();
		if(!fields.isEmpty())
		{
			groupBy=groupBy.append(fields.get(0).getName());
			fields.add(getField("TIME","MAX(TTIME)",FieldType.NUMBER));
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
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(getDeviceListCondition(deviceList))
				.groupBy(groupBy.toString());
		return builder.get();
	}

	private FacilioField getEnergyField() {
		FacilioField energyFld = new FacilioField();
		energyFld.setName("CONSUMPTION");
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



	private String getRootMeterList(String purposeList, String buildingId ) throws Exception {
		
		List<EnergyMeterContext> energyMeters = DeviceAPI.getAllEnergyMeters(buildingId, purposeList, true);
		return getDeviceList(energyMeters);
	}
	
	
	private String getDeviceList(List<EnergyMeterContext> energyMeters)
	{
		StringBuilder rootList= new StringBuilder();
		for(EnergyMeterContext emc:energyMeters)
		{
			rootList.append(emc.getId());
			rootList.append(",");
		}
		return removeLastChar(rootList, ",");
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
		if(duration.equals("day"))
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
			List<EnergyMeterContext> meterList= DeviceAPI.getAllEnergyMeters(""+floor.getId(), false);
			String deviceList=getDeviceList(meterList);
			List<FacilioField> fields = new ArrayList<FacilioField>() ;
			List<Map<String, Object>> total=getData(deviceList,startTime, endTime, fields,false);
			if(total!=null && !total.isEmpty())
			{
				 Map<String,Object> rowData=total.get(0);
				 Double totalMWh=(Double)rowData.get("CONSUMPTION")/1000;
				 result.put(floorName, totalMWh);
			}
		}
		if(result!=null && !result.isEmpty())
		{
			resultData.put("currentVal", valueSort(result,true));
			resultData.put("units", "MWh");
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
		setReportAllData(getBuildingDetails(getBuildingId()));
		return SUCCESS;
	}
	
	
	private JSONObject getBuildingData(JSONObject buildingData, BuildingContext building) throws Exception
	{
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
		return buildingData;
	}
	
	
	public JSONObject getBuildingDetails(long buildingId) throws Exception
	{
		JSONObject buildingData = new JSONObject();
		
		BuildingContext building =SpaceAPI.getBuildingSpace(getBuildingId());
		
		buildingData=getBuildingData(buildingData,building);
		 
		List<EnergyMeterContext> rootMeterList= DeviceAPI.getRootEnergyMeter(""+buildingId);
		
		StringBuilder rootBuilder= new StringBuilder();
		StringBuilder purposeBuilder= new StringBuilder();
	
		for(EnergyMeterContext emc : rootMeterList)
		{
			rootBuilder.append(emc.getId());
			rootBuilder.append(",");
			purposeBuilder.append(emc.getPurpose().getId());
			purposeBuilder.append(",");
		}
		String rootList=removeLastChar(rootBuilder, ",");
		String rootPurposeList=removeLastChar(purposeBuilder,",");

		long startTime=DateTimeUtil.getMonthStartTime(-1);
		long endTime=DateTimeUtil.getCurrenTime();
		
		FacilioField monthFld = getField("MONTH", "TTIME_MONTH", FieldType.NUMBER);
		FacilioField energyFld = getEnergyField();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(energyFld);
		fields.add(monthFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(getDeviceListCondition(rootList))
				.groupBy("TTIME_MONTH");
		List<Map<String, Object>> result = builder.get();
		
		int lastMonth=-1;int thisMonth=-1;
		double lastMonthMWh=-1;double thisMonthMWh=-1;
		
		if(result!=null && !result.isEmpty())
		{
			if(result.size()==2){
				Map<String,Object> map = result.get(0);
				lastMonth =(int)map.get("MONTH");
				lastMonthMWh = (double)map.get("CONSUMPTION");
				map = result.get(1);
				thisMonth =(int)map.get("MONTH");
				thisMonthMWh = (double)map.get("CONSUMPTION");		
			}
			else{
				Map<String,Object> map = result.get(0);
				thisMonth =(int)map.get("MONTH");
				thisMonthMWh = (double)map.get("CONSUMPTION");
			}
			
		}
		
		lastMonthMWh=lastMonthMWh/1000;
		thisMonthMWh=thisMonthMWh/1000;
		
		double thisMonthCost=thisMonthMWh*unitCost;
		double lastMonthCost=lastMonthMWh*unitCost;
		
		long endTimestamp=DateTimeUtil.getMonthStartTime();
		int lastMonthDays= DateTimeUtil.getDaysBetween(startTime, endTimestamp-1);
		int thisMonthDays=DateTimeUtil.getDaysBetween(endTimestamp,endTime);
		
		//double lastMonthAvgEUI= lastMonthKwh/buildingArea/lastMonthDays;
		//double thisMonthAvgEUI=thisMonthKwh/buildingArea/thisMonthDays;
		double variance= getVariance(thisMonthMWh, lastMonthMWh);
		
		JSONObject lastMonthData = new JSONObject();
		lastMonthData.put("kwh",lastMonthMWh);
		lastMonthData.put("days", lastMonthDays);
		lastMonthData.put("units","MWh");
		lastMonthData.put("currency","$");
		//lastMonthData.put("eui", lastMonthAvgEUI);
		lastMonthData.put("cost", lastMonthCost);
		lastMonthData.put("monthVal", lastMonth);
		
		JSONObject thisMonthData = new JSONObject();
		thisMonthData.put("kwh",thisMonthMWh);
		thisMonthData.put("days", thisMonthDays);
		thisMonthData.put("units","kWh");
		thisMonthData.put("currency","$");
		//thisMonthData.put("eui", thisMonthAvgEUI);
		thisMonthData.put("cost", thisMonthCost);
		thisMonthData.put("monthVal", thisMonth);
		
		buildingData.put("previousVal", lastMonthData);
		buildingData.put("currentVal", thisMonthData);
		buildingData.put("variance", variance);
		buildingData.put("purpose", DeviceAPI.getFilteredPurposes(rootPurposeList,NumberOperators.NOT_EQUALS));
		
		// need to send cost..as well..
		//need to send temperature & carbon emmission [
		return buildingData;
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