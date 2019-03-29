package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkContext {
	
	private String displayUnit;
	
	public String getDisplayUnit() {
		return displayUnit;
	}
	public void setDisplayUnit(String displayUnit) {
		this.displayUnit = displayUnit;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private double value = -1;
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	private Metric metric;
	public int getMetric() {
		if (metric != null) {
			return metric.getMetricId();
		}
		return -1;
	}
	public void setMetric(int metric) {
		this.metric = Metric.valueOf(metric);
	}
	public Metric getMetricEnum() {
		return metric;
	}
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	
	private FacilioFrequency duration;
	public int getDuration() {
		if (duration != null) {
			return duration.getValue();
		}
		return -1;
	}
	public void setDuration(int duration) {
		this.duration = FacilioFrequency.valueOf(duration);
	}
	public FacilioFrequency getDurationEnum() {
		return duration;
	}
	public void setDuration(FacilioFrequency duration) {
		this.duration = duration;
	}
	
	private List<Unit> units;
	public List<Unit> getUnits() {
		return units;
	}
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	public void addUnit(Unit unit) {
		if (units == null) {
			units = new ArrayList<>();
		}
		units.add(unit);
	}
	
}
