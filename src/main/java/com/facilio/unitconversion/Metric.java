package com.facilio.unitconversion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Metric {

	ENERGY(1,"Energy",Unit.WH),
	TEMPRATURE(2,"Temprature",Unit.CELSIUS),
	LENGTH(3,"Length",Unit.METER),
	MASS(4,"Mass",Unit.CELSIUS),
	TIME(5,"Time",Unit.HOUR),
	CURRENT(6,"Current",Unit.CELSIUS),
	;
	
	int metricId;
	String name;
	Unit siUnit;
	
	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Unit getSiUnit() {
		return siUnit;
	}

	public void setSiUnit(Unit siUnit) {
		this.siUnit = siUnit;
	}
	
	Metric(int metricId, String name,Unit siUnit) {
		this.metricId = metricId;
		this.name = name;
		this.siUnit = siUnit;
	}
	
	private static final Map<Integer, Metric> METRIC_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, Metric> initTypeMap() {
		Map<Integer, Metric> typeMap = new HashMap<>();
		for(Metric type : values()) {
			typeMap.put(type.getMetricId(), type);
		}
		return typeMap;
	}
	
	public static Metric valueOf(int metricId) {
		return METRIC_MAP.get(metricId);
	}
}
