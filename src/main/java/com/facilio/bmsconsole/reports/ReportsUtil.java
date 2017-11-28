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

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ReportsUtil 
{
	
	public static double getVariance(Double currentVal, Double previousVal)
	{
		double variance =(currentVal - previousVal)/currentVal;
		variance=roundOff(variance, 2);
		return variance*100;
	}
	
	private static double unitCost=0.65;

	
	public static double toMega(double value)
	{
		//Converting Kilo into Mega
		return roundOff(value/1000,2);
	}
	
	public static double roundOff(double value, int decimalDigits)
	{
		double multiplier =Math.pow(10, decimalDigits);;
		return (double)Math.round(value*multiplier)/ multiplier ;
	}
	
	public static String[] getCost(double kWh)
	{
		//later we need to calculate based on slab..
		return toMillions(kWh*unitCost);
	}
	
	
	public static String[] toMillions(double value)
	{

		long length=(long)Math.log10(value)+1;
		int divider=1;
		int decimal=2;
		String units="";
		if(length>6)
		{
			divider=1000000;
			units=" M";
			decimal=4;
		}
		else if(length>4)
		{
			divider=1000;
			units=" K";
			decimal=4;
		}
		double finalValue=value/divider;
		String[] result= new String[2];
		result[0] =""+roundOff(finalValue, decimal);
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
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getBuildingData(BuildingContext building)
	{
		JSONObject buildingData= new JSONObject();
		int floors=building.getNoOfFloors();
		buildingData.put("floors", floors);
		long photoId=building.getPhotoId();
		buildingData.put("photoid", photoId);
		String avatarUrl="";
		try{
			avatarUrl=building.getAvatarUrl();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
		buildingData.put("id", building.getId());
		buildingData.put("displayName", displayName);
		buildingData.put("area", buildingArea);
		return buildingData;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMonthData(double kwh,int days)
	{
		JSONObject monthData = new JSONObject();
		monthData.put("consumption",ReportsUtil.toMega(kwh));
		monthData.put("days", days);
		monthData.put("units","MWh");
		monthData.put("currency","$");
		
		String [] costArray=ReportsUtil.getCost(kwh);
		monthData.put("cost", costArray[0]);
		monthData.put("costUnits", costArray[1]);
		////double EUI= kwh/buildingArea/lastMonthDays;
		//monthData.put("eui", EUI);
		return monthData;
	}
	
	
	public static List<Map<String, Object>> fetchMothlyData(String deviceList,long startTime, long endTime, boolean building)
	{
		List<Map<String, Object>> result=null;
		
		FacilioField energyFld = ReportsUtil.getEnergyField();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(energyFld);
		String groupBy="";
		if(!building)
		{
			FacilioField meterFld = ReportsUtil.getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
			fields.add(meterFld);
			groupBy= meterFld.getName();
		}

		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(DeviceAPI.getCondition("PARENT_METER_ID", deviceList, NumberOperators.EQUALS))
				.groupBy(groupBy);
		try {
			result = builder.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;	
	}
	
	public static Map<Long,Double> getMeterVsConsumption(List<Map<String, Object>> result)
	{
		Map<Long,Double> meterConsumption = new HashMap<Long,Double>();
		for(Map<String,Object> rowData: result)
		{
			long meterId=(long)	rowData.get("Meter_ID");
			double consumption=(double) rowData.get("CONSUMPTION");
			meterConsumption.put(meterId, consumption);
		}
		return meterConsumption;
	}
}
