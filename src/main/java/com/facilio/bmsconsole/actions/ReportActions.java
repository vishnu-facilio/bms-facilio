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

	public String getBuildingDetails() throws Exception
	{
		setReportData(getBuildingDetails(getBuildingId()));
		return SUCCESS;
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
				result.put(floorName, ReportsUtil.roundOff(totalKwh,2));
			}
		}
		if(result!=null && !result.isEmpty())
		{
			resultData.put("currentVal", ReportsUtil.valueSort(result,true));
			resultData.put("units", "kWh");
		}
		setReportData(resultData);
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
		HashMap<Long,String> purposeMapping= DeviceAPI.getPurposeMapping(getBuildingId(),true);

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
			String[] consumptionArray=ReportsUtil.energyConverter(currentKwh);
			resultJson.put("totalConsumption", consumptionArray[0]);
			resultJson.put("units",consumptionArray[1]);
		}
		resultJson.put("currentVal", current);
		resultJson.put("mapping", purposeMapping);
		setReportData(resultJson);
		return SUCCESS;
	}

	//no time series..
	//needs period, building id..
	@SuppressWarnings("unchecked")
	public String getServiceConsumption() throws Exception
	{
		JSONObject resultJson = new JSONObject();

		HashMap<Long,String> purposeMapping= DeviceAPI.getPurposeMapping(getBuildingId(),true);
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
		setReportData(resultJson);
		return SUCCESS;
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
			List<EnergyMeterContext> rootMeterList =	DeviceAPI.getMainEnergyMeter(""+buildingId);
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
		String[] consumptionArray=ReportsUtil.energyConverter(currentKwh);
		consumptionData.put("currentTotal", consumptionArray[0]);
		consumptionData.put("currentUnits",consumptionArray[1]);
		consumptionArray=ReportsUtil.energyConverter(previousKwh);
		consumptionData.put("previousTotal", consumptionArray[0]);
		consumptionData.put("previousUnits",consumptionArray[1]);
		consumptionData.put("variance",ReportsUtil.getVariance(currentKwh, previousKwh));
		setReportData(consumptionData);
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





	


	@SuppressWarnings("unchecked")
	private JSONObject getBuildingDetails(long buildingId) throws Exception
	{
		BuildingContext building =SpaceAPI.getBuildingSpace(getBuildingId());
		JSONObject buildingData=ReportsUtil.getBuildingData(building);
		List<EnergyMeterContext> rootMeterList= DeviceAPI.getMainEnergyMeter(""+buildingId);

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

		long previousStartTime=DateTimeUtil.getMonthStartTime(-1);
		long currentStartTime=DateTimeUtil.getMonthStartTime();
		long endTime=DateTimeUtil.getCurrenTime();

		List<Map<String, Object>> previousResult = ReportsUtil.fetchMeterData(rootList,previousStartTime,currentStartTime-1,true);
		List<Map<String, Object>> currentResult = ReportsUtil.fetchMeterData(rootList,currentStartTime,endTime,true);
		
		double lastMonthKwh=-1;double thisMonthKwh=-1;

		if(previousResult!=null && !previousResult.isEmpty()){
			
				Map<String,Object> map = previousResult.get(0);
				lastMonthKwh = (double)map.get("CONSUMPTION");
		}
		if(currentResult!=null && !currentResult.isEmpty()){

			Map<String,Object> map = currentResult.get(0);
			thisMonthKwh = (double)map.get("CONSUMPTION");
		}

		int lastMonthDays= DateTimeUtil.getDaysBetween(previousStartTime, currentStartTime);
		int thisMonthDays=DateTimeUtil.getDaysBetween(currentStartTime,endTime)+1;

		double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
		JSONObject lastMonthData = ReportsUtil.getMonthData(lastMonthKwh,lastMonthDays);
		JSONObject thisMonthData = ReportsUtil.getMonthData(thisMonthKwh,thisMonthDays);


		buildingData.put("previousVal", lastMonthData);
		buildingData.put("currentVal", thisMonthData);
		buildingData.put("variance", variance);
		buildingData.put("purpose", DeviceAPI.getFilteredPurposes(rootPurposeList,NumberOperators.NOT_EQUALS));

		// need to send cost..as well..
		//need to send temperature & carbon emission [
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
	private JSONObject reportData = null;

	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}

}