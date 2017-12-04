package com.facilio.bmsconsole.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ReportsUtil 
{
	
	public static double getVariance(Double currentVal, Double previousVal)
	{
		if(currentVal==null || currentVal==0 || previousVal==null || previousVal==0)
		{
			return 0;
		}
		double variance =(currentVal - previousVal)/currentVal;
		variance=variance*100;
		return roundOff(variance, 2);
	}
	
	private static double unitCost=0.65;

	
	public static String[] energyUnitConverter(Double value)
	{
		long length=(long)Math.log10(value)+1;
		String units="kWh";
		if(length>4)
		{
			//converting kilo to mega
			value=value/1000;
			units="MWh";
		}
		String[] result= new String[2];
		result[0] =""+roundOff(value, 2);
		result[1] =units;
		return result;
	}
	
	public static double roundOff(double value, int decimalDigits)
	{
		double multiplier =Math.pow(10, decimalDigits);;
		return (double)Math.round(value*multiplier)/ multiplier ;
	}
	
	public static String[] getCost(Double kWh)
	{
		//later we need to calculate based on slab..
		return costConverter(kWh*unitCost);
	}
	
	
	
	public static Long[] getTimeInterval(String duration)
	{
		//defaulting to "today"
		long startTime=DateTimeUtil.getDayStartTime();
		long endTime=DateTimeUtil.getCurrenTime();

		if (duration.equals("yesterday"))
		{
			startTime=DateTimeUtil.getDayStartTime(-1);
			endTime=DateTimeUtil.getDayStartTime() -1;
		}
		else if (duration.equals("week"))
		{
			startTime=DateTimeUtil.getWeekStartTime();	
		}
		else if (duration.equals("lastWeek"))
		{
			startTime=DateTimeUtil.getWeekStartTime(-1);	
			endTime=DateTimeUtil.getWeekStartTime() -1;	
		}
		else if (duration.equals("month"))
		{
			startTime=DateTimeUtil.getMonthStartTime();
		}
		else if (duration.equals("lastMonth"))
		{
			startTime=DateTimeUtil.getMonthStartTime(-1);
			endTime=DateTimeUtil.getMonthStartTime()-1;
		}
		else if (duration.equals("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		else if (duration.equals("lastYear"))
		{
			startTime=DateTimeUtil.getYearStartTime(-1);
			endTime=DateTimeUtil.getYearStartTime()-1;
		}
		Long[] timeIntervals= new Long[2];
		timeIntervals[0]=startTime;
		timeIntervals[1]=endTime;
		return timeIntervals;
	}
	
	public static String[] costConverter(double value)
	{

		long length=(long)Math.log10(value)+1;
		int divider=1;
		String units="";
		if(length>6)
		{
			divider=1000000;
			units=" M";
		}
		else if(length>4)
		{
			divider=1000;
			units=" K";
		}
		double finalValue=value/divider;
		String[] result= new String[2];
		result[0] =""+roundOff(finalValue, 2);
		result[1] =units;
		return result;
	}
	
	
	public static FacilioField getEnergyField() {
		FacilioField energyFld = new FacilioField();
		energyFld.setName("CONSUMPTION");
		energyFld.setColumnName("ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2)");
		energyFld.setDataType(FieldType.DECIMAL);
		return energyFld;
	}

	public static FacilioField getField(String name, String colName, FieldType type) {
		FacilioField energyFld = new FacilioField();
		energyFld.setName(name);
		energyFld.setColumnName(colName);
		energyFld.setDataType(type);
		return energyFld;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> valueSort(Map<K, V> map, boolean descending) {
		   
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
	
	public static String removeLastChar(StringBuilder builder, String deleteChar)
	{
		int index=builder.lastIndexOf(deleteChar);
		if(index==-1)
		{
			return builder.toString();
		}
		return builder.deleteCharAt(index).toString();
	}
	
	
	public static JSONObject getBuildingData(BuildingContext building)
	{
		return getBuildingData(building,true);
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getBuildingData(BuildingContext building, boolean fetchLocation)
	{
		JSONObject buildingData= new JSONObject();
		buildingData.put("name", building.getName());
		buildingData.put("id", building.getId());
		buildingData.put("displayName", building.getDisplayName());
		buildingData.put("area", building.getGrossFloorArea());
		buildingData.put("floors", building.getNoOfFloors());
		buildingData.put("photoid", building.getPhotoId());
		if(!fetchLocation)
		{
			return buildingData;
		}
		
		try{
			String avatarUrl=building.getAvatarUrl();
			buildingData.put("avatar",avatarUrl);
			LocationContext location=building.getLocation();
			if(location!=null)
			{
				location=SpaceAPI.getLocationSpace(building.getLocation().getId());
				buildingData.put("city", location.getCity());
				buildingData.put("street",location.getStreet());
				buildingData.put("latitude",location.getLat());
				buildingData.put("longitude",location.getLng());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return buildingData;
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getEnergyData(Double kwh,int days)
	{
		JSONObject data = new JSONObject();
		if(kwh==null || kwh==0)
		{
			return data;
		}
		String[] consumptionArray=ReportsUtil.energyUnitConverter(kwh);
		data.put("consumption",consumptionArray[0]);
		data.put("units",consumptionArray[1]);
		data.put("currency","$");
		data.put("days", days);
		
		String [] costArray=ReportsUtil.getCost(kwh);
		data.put("cost", costArray[0]);
		data.put("costUnits", costArray[1]);
		return data;
	}
	
	
	public static Map<Long, Long> getBuildingVsMeter(List<EnergyMeterContext> energyMeters)
	{
	
		Map <Long, Long> buildingVsMeter= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			//under the assumption only one main meter for a building.. 
			//when there are more than 1 main meter, there should be one virtual meter which will be the main meter..
			buildingVsMeter.put(buildingId,meterId);
		}
		return buildingVsMeter;
	}
	
	
	public static double getEUI(Double currentKwh, Double buildingArea) {

		if(currentKwh==null || currentKwh==0 || buildingArea==null || buildingArea==0)
		{
			return 0;
		}
		double conversionMultiplier=3.412;//this for electrical energy..
		double convertedVal=currentKwh*conversionMultiplier;
		double eui= convertedVal/buildingArea;
		return roundOff(eui, 2);
	}
	
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime)
	{
		return fetchMeterData(deviceList,startTime,endTime,false);
	}
	
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime, boolean org)
	{
		return fetchMeterData(deviceList,startTime,endTime, org,false);
	}
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime, boolean org,boolean rollUp)
	{
		List<Map<String, Object>> result=null;
		
		FacilioField energyFld = ReportsUtil.getEnergyField();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(energyFld);
		StringBuilder groupBy=new StringBuilder();
		if(org)
		{
			FacilioField meterFld = ReportsUtil.getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
			fields.add(meterFld);
			groupBy.append(meterFld.getName());
			if(rollUp)
			{
				groupBy.append(" WITH ROLLUP");
			}
		}
		
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
                .andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(DeviceAPI.getCondition("PARENT_METER_ID", deviceList, NumberOperators.EQUALS))
				.groupBy(groupBy.toString());
		try {
			result = builder.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;	
	}
	
	public static Map<Long,Double> getMeterVsConsumption(List<Map<String, Object>> result)
	{
		return getMapping(result,"Meter_ID","CONSUMPTION");
	}
	
	public static Map<Long,Double> getMapping(List<Map<String, Object>> result,String key,String value)
	{
		Map<Long,Double> keyVsValue = new HashMap<Long,Double>();
		for(Map<String,Object> rowData: result)
		{
			Long meterId=(Long)	rowData.get(key);
			Double consumption=(Double) rowData.get(value);
			keyVsValue.put(meterId, consumption);
		}
		return keyVsValue;
	}
	
	public static Map<Long,Double> getMeterVsConsumptionPercentage(List<Map<String, Object>> result, double totalKwh)
	{
		Map<Long,Double> meterConsumption = new HashMap<Long,Double>();
		for(Map<String,Object> rowData: result)
		{
			Long meterId=(Long)	rowData.get("Meter_ID");
			Double consumption=(Double) rowData.get("CONSUMPTION");
			Double percentage=getPercentage(consumption,totalKwh);
			meterConsumption.put(meterId, percentage);
		}
		return meterConsumption;
	}
	
	public static double getPercentage(double numerator, double denominator)
	{
		double returnVal=numerator/denominator;
		return returnVal*100;
	}
}
