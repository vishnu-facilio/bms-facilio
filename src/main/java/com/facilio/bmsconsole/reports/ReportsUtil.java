package com.facilio.bmsconsole.reports;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;

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
	
	public static String getCost(double kWh)
	{
		//later we need to calculate based on slab..
		return toMillions(kWh*unitCost);
	}
	
	
	public static String toMillions(double value)
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
		return roundOff(finalValue, decimal)+units;
		
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
	
}
