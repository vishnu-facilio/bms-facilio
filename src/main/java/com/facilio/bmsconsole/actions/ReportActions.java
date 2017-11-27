package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class ReportActions extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	double unitCost=0.65;
	
	public String reportdata() throws Exception 
	{
		return SUCCESS;
	}
	 
	public String reportDashboard() throws Exception 
	{
 		return SUCCESS;
	}
	
	//time series data....
	//need building id, 
	// need period to be set to week for By Week break down..
	// if period not set , by date 
	@SuppressWarnings("unchecked")
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
			FacilioField dayFld = ReportsUtil.getField("DAY","TTIME_DAY",FieldType.NUMBER);
			fields.add(dayFld);
		}
		
		FacilioField meterFld = ReportsUtil.getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
		FacilioField timeFld = ReportsUtil.getField("DATE","TTIME_DATE",FieldType.NUMBER);
		fields.add(meterFld);
		fields.add(timeFld);
		
		List<Map<String, Object>> current=getGroupByData(deviceList,startTime, endTime, fields);
		fields = new ArrayList<FacilioField>() ;
		List<Map<String, Object>> total=getData(deviceList,startTime, endTime, fields,false);
		if(total!=null & !total.isEmpty()) {
			Map<String,Object> currentTotal=total.get(0);
			double currentKwh = (double)currentTotal.get("CONSUMPTION");
			resultJson.put("totalMWh", ReportsUtil.toMegaNRound(currentKwh));
		}
		resultJson.put("currentVal", current);
		resultJson.put("mapping", purposeMapping);
		setReportAllData(resultJson);
		return SUCCESS;
	}
	
	//no time series..
	//needs period, building id..
	@SuppressWarnings("unchecked")
	public String getServiceConsumption() throws Exception
	{
		JSONObject resultJson = new JSONObject();
		
		HashMap<Long,String> purposeMapping= getPurposeMapping(getBuildingId(),true);
		Set<Long> keys=purposeMapping.keySet();
		String deviceList=StringUtils.join(keys, ",");
		String duration = getPeriod();
		long startTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		
		FacilioField selectFld = ReportsUtil.getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
				
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
		FacilioField meterFld = ReportsUtil.getField("Meter","Energy_Meter.ID",FieldType.NUMBER);
		FacilioField purposeField =ReportsUtil.getField("Name","Energy_Meter_Purpose.NAME",FieldType.STRING);
		
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
	@SuppressWarnings("unchecked")
	private void getRootConsumption(String deviceId) throws Exception
	{
		JSONObject consumptionData = new JSONObject();
		long startTime=-1;
		long previousStartTime=-1;
		long previousEndTime=-1;
		long endTime=DateTimeUtil.getCurrenTime();
		
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
		FacilioField periodFld = ReportsUtil.getField(displayName,colName, FieldType.NUMBER);
		periodFld.setName(displayName);
		fields.add(periodFld);
		
		
		double currentKwh=-1;
		double previousKwh=-1;
		List<Map<String, Object>> current=getData(deviceId,startTime, endTime, fields,true);
		List<Map<String, Object>> previous=getData(deviceId,previousStartTime, previousEndTime, fields, true);
		
		if(current!=null & !current.isEmpty()) {
			Map<String,Object> currentTotal=current.remove(current.size()-1);
			currentKwh = (double)currentTotal.get("CONSUMPTION");
		}
		if(previous!=null & !previous.isEmpty()) {
			Map<String,Object> previousTotal=previous.remove(previous.size()-1);
			previousKwh = (double)previousTotal.get("CONSUMPTION");
		}
		consumptionData.put("currentVal", current);
		consumptionData.put("previousVal", previous);
		consumptionData.put("currentTotal", ReportsUtil.toMegaNRound(currentKwh));
		consumptionData.put("previousTotal", ReportsUtil.toMegaNRound(previousKwh));
		consumptionData.put("units", "MWh");
		consumptionData.put("variance",ReportsUtil.getVariance(currentKwh, previousKwh));
		setReportAllData(consumptionData);
	}

	

	private List<Map<String, Object>> getGroupByData( String deviceList, long startTime, long endTime, List<FacilioField> fields) throws Exception {
		
		StringBuilder groupBuilder= new StringBuilder();		
		for(FacilioField field: fields)
		{
			groupBuilder.append(field.getName());
			groupBuilder.append(",");
		}
		String groupBy= ReportsUtil.removeLastChar(groupBuilder,",");
		
		fields.add(ReportsUtil.getEnergyField());
		fields.add(ReportsUtil.getField("TIME","MAX(TTIME)",FieldType.NUMBER));
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(getDeviceListCondition(deviceList))
				.groupBy(groupBy);
		return builder.get();
	}
	
	
	
	
	private List<Map<String, Object>> getData( String deviceList, long startTime, long endTime, List<FacilioField> fields, boolean rollUp ) throws Exception {


		StringBuilder groupBy=new StringBuilder();
		if(!fields.isEmpty())
		{
			groupBy=groupBy.append(fields.get(0).getName());
			fields.add(ReportsUtil.getField("TIME","MAX(TTIME)",FieldType.NUMBER));
		}

		FacilioField energyFld = ReportsUtil.getEnergyField();
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
		return ReportsUtil.removeLastChar(rootList, ",");
	}
	
	
	// needs building id, duration,
	// no time series data...
	@SuppressWarnings("unchecked")
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
				 Double totalKwh=(Double)rowData.get("CONSUMPTION");
				 result.put(floorName, ReportsUtil.toMegaNRound(totalKwh));
			}
		}
		if(result!=null && !result.isEmpty())
		{
			resultData.put("currentVal", ReportsUtil.valueSort(result,true));
			resultData.put("units", "MWh");
		}
		setReportAllData(resultData);
		return SUCCESS;
	}
	
	
	
	
	
	public String getBuildingDetails() throws Exception
	{
		setReportAllData(getBuildingDetails(getBuildingId()));
		return SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
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
	
	
	@SuppressWarnings("unchecked")
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
		String rootList=ReportsUtil.removeLastChar(rootBuilder, ",");
		String rootPurposeList=ReportsUtil.removeLastChar(purposeBuilder,",");

		long startTime=DateTimeUtil.getMonthStartTime(-1);
		long endTime=DateTimeUtil.getCurrenTime();
		
		FacilioField monthFld = ReportsUtil.getField("MONTH", "TTIME_MONTH", FieldType.NUMBER);
		FacilioField energyFld = ReportsUtil.getEnergyField();
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
		double lastMonthKwh=-1;double thisMonthKwh=-1;
		
		if(result!=null && !result.isEmpty())
		{
			if(result.size()==2){
				Map<String,Object> map = result.get(0);
				lastMonth =(int)map.get("MONTH");
				lastMonthKwh = (double)map.get("CONSUMPTION");
				map = result.get(1);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("CONSUMPTION");		
			}
			else{
				Map<String,Object> map = result.get(0);
				thisMonth =(int)map.get("MONTH");
				thisMonthKwh = (double)map.get("CONSUMPTION");
			}
		}
		
		long endTimestamp=DateTimeUtil.getMonthStartTime();
		int lastMonthDays= DateTimeUtil.getDaysBetween(startTime, endTimestamp-1);
		int thisMonthDays=DateTimeUtil.getDaysBetween(endTimestamp,endTime);
		
		double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
		JSONObject lastMonthData = getMonthData(lastMonthKwh,lastMonthDays,lastMonth);
		JSONObject thisMonthData = getMonthData(thisMonthKwh,thisMonthDays,thisMonth);
		
		
		buildingData.put("previousVal", lastMonthData);
		buildingData.put("currentVal", thisMonthData);
		buildingData.put("variance", variance);
		buildingData.put("purpose", DeviceAPI.getFilteredPurposes(rootPurposeList,NumberOperators.NOT_EQUALS));
		
		// need to send cost..as well..
		//need to send temperature & carbon emmission [
		return buildingData;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getMonthData(double kwh,int days,  int monthVal)
	{
		JSONObject monthData = new JSONObject();
		monthData.put("consumption",ReportsUtil.toMegaNRound(kwh));
		monthData.put("days", days);
		monthData.put("units","MWh");
		monthData.put("currency","$");
		
		monthData.put("cost", ReportsUtil.costConversionToMillion(kwh*unitCost));
		monthData.put("monthVal", monthVal);
		////double EUI= kwh/buildingArea/lastMonthDays;
		//monthData.put("eui", EUI);
		return monthData;
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
	
}