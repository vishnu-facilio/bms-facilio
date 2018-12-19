package com.facilio.unitconversion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Metric {

	ENERGY(1,"Energy",1, 2),
	TEMPERATURE(2,"Temperature",4, 1),
	LENGTH(3,"Length",7),
	MASS(4,"Mass",25),
	DURATION(5,"Duration",18),
	CURRENT(6,"Current",41, 2),
	VOLTAGE(7,"Voltage",36, 0),
	POWER(8,"Power",46, 2),
	FREQUENCY(9,"Frequency",51),
	REACTIVEPOWER(10,"Reactive Power",58),
	PRESSURE(11,"Pressure",61),
	DEGREE(12,"Degree",62),
	AREA(12,"Area",63),
	TORQUE(13,"Torque",69, 2),
	VOLUME(14,"Liquid",70),
	CURRENCY(15,"Currency"),
	PERCENTAGE(16,"Percentage",87),
	PRECIPITATION_INTENSITY(17,"precipitation intensity",89),
	;
	
	public static Map<Integer, Metric> getMetricMap() {
		return METRIC_MAP;
	}

	int metricId;
	String name;
	int siUnitId = -1;
	int decimalPoints;
	
	public int getDecimalPoints() {
		return decimalPoints;
	}

	public void setDecimalPoints(int decimalPoints) {
		this.decimalPoints = decimalPoints;
	}

	public int getSiUnitId() {
		return siUnitId;
	}

	public int getMetricId() {
		return metricId;
	}
	
	public String getName() {
		return name;
	}
	
	Metric(int metricId, String name,int siUnitid, int decimalPoints) {
		this.metricId = metricId;
		this.name = name;
		this.siUnitId = siUnitid;
		this.decimalPoints = decimalPoints;
	}
	Metric(int metricId, String name,int siUnitid) {
		this.metricId = metricId;
		this.name = name;
		this.siUnitId = siUnitid;
	}
	Metric(int metricId, String name) {
		this.metricId = metricId;
		this.name = name;
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
