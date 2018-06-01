package com.facilio.unitconversion;

import java.awt.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public enum Unit {
	
	WH(1,"wh",Metric.ENERGY),
	KWH(2,"kwh",Metric.ENERGY,"si/1000","this*1000"),
	MWH(3,"mwh",Metric.ENERGY,"si/1000000","this*1000000"),
	
	CELSIUS(4,"Celsius",Metric.TEMPRATURE),
	FAHRENHEIT(5,"Fahrenheit",Metric.TEMPRATURE,"(si*1.8)+32","(this-32)/1.8"),
	KELWIN(6,"Kelwin",Metric.TEMPRATURE,"si+273.15","this-273.15"),
	
	METER(7,"Meter",Metric.LENGTH),
	DECIMETER(8,"Decimetre",Metric.LENGTH,"si*10","this/10"),
	CENTIMETER(9,"Centimetre",Metric.LENGTH,"si*100","this/100"),
	MILLIMETER(10,"Millimeter",Metric.LENGTH,"si*1000","this/1000"),
	DECAMETER(11,"Decameter",Metric.LENGTH,"si/10","this*10"),
	HECTOMETER(12,"Hectometer",Metric.LENGTH,"si/100","this*100"),
	KILOMETER(13,"kilometer",Metric.LENGTH,"si/1000","this*1000"),
	MILE(14,"Mile",Metric.LENGTH,"si/1609.34","this*1609.34"),
	YARD(15,"Yard",Metric.LENGTH,"si*1.0936132983","this*0.9144"),
	FEET(16,"Feet",Metric.LENGTH,"si*3.280839895","this*0.3048"),
	INCH(17,"Inch",Metric.LENGTH,"si*39.3700787402","this*0.0254"),
	
	HOUR(18,"Hour",Metric.TIME),
	MIN(19,"Minute",Metric.TIME,"si*60","this/60"),
	SEC(20,"Second",Metric.TIME,"si*60*60","this/(60*60)"),
	DAY(21,"Day",Metric.TIME,"si/24","this*24"),
	WEEK(22,"Week",Metric.TIME,"si/(24*7)","this*(24*7)"),
	YEAR(24,"Year",Metric.TIME,"si/(24*365)","this*(24*365)"),
	;
	
	int unitId;
	String displayName;
	Metric metric;
	boolean isSiUnit;
	String fromSiUnit;
	String toSiUnit;
	
	private static final Map<Integer, Unit> UNIT_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, Unit> initTypeMap() {
		Map<Integer, Unit> typeMap = new HashMap<>();
		for(Unit type : values()) {
			typeMap.put(type.getUnitId(), type);
		}
		return typeMap;
	}
	
	private static final Multimap<Metric, Unit> METRIC_UNIT_MAP = initMap();
	private static Multimap<Metric, Unit> initMap() {
		Multimap<Metric, Unit> typeMap = ArrayListMultimap.create();
		for(Unit type : values()) {
			typeMap.put(type.getMetric(), type);
		}
		return typeMap;
	}
	
	Unit(int unitId,String displayName,Metric metric) {
		
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = true;
		this.displayName = displayName;
	}
	
	Unit(int unitId,String displayName,Metric metric,String fromSiUnit,String toSiUnit) {
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = false;
		this.fromSiUnit = fromSiUnit;
		this.toSiUnit = toSiUnit;
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public boolean isSiUnit() {
		return isSiUnit;
	}

	public void setSiUnit(boolean isSiUnit) {
		this.isSiUnit = isSiUnit;
	}

	public String getFromSiUnit() {
		return fromSiUnit;
	}

	public void setFromSiUnit(String fromSiUnit) {
		this.fromSiUnit = fromSiUnit;
	}

	public String getToSiUnit() {
		return toSiUnit;
	}

	public void setToSiUnit(String toSiUnit) {
		this.toSiUnit = toSiUnit;
	}
}