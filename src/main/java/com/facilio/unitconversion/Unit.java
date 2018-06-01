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