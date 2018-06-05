package com.facilio.unitconversion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Metric {

	ENERGY(1,"Energy",1),
	TEMPRATURE(2,"Temprature",4),
	LENGTH(3,"Length",7),
	MASS(4,"Mass",25),
	TIME(5,"Time",18),
	;
	
	public static Map<Integer, Metric> getMetricMap() {
		return METRIC_MAP;
	}

	int metricId;
	String name;
	int siUnitId = -1;
	
	public int getSiUnitId() {
		return siUnitId;
	}

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

	
	Metric(int metricId, String name,int siUnitid) {
		this.metricId = metricId;
		this.name = name;
		this.siUnitId = siUnitid;
	}
	
	private static final Map<Integer, Metric> METRIC_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, Metric> initTypeMap() {
		Map<Integer, Metric> typeMap = new HashMap<>();
		for(Metric type : values()) {
			typeMap.put(type.getMetricId(), type);
		}
		return typeMap;
	}
	
	public static Collection<Metric> getAllMetrics() {
		return METRIC_MAP.values();
	}
	
	public static Metric valueOf(int metricId) {
		return METRIC_MAP.get(metricId);
	}
}
