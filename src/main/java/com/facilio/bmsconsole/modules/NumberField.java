package com.facilio.bmsconsole.modules;

import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;

public class NumberField extends FacilioField {
	
	private int unitId;
	
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public Unit getUnitEnum() {
		if(unitId > 0) {
			return Unit.valueOf(unitId);
		}
		return null;
	}
	private String unit;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	private int metric = -1;
	public int getMetric() {
		return metric;
	}
	public void setMetric(int metric) {
		this.metric = metric;
	}
	
	private Boolean counterField;
	public Boolean getCounterField() {
		return counterField;
	}
	public void setCounterField(Boolean counterField) {
		this.counterField = counterField;
	}
	public boolean isCounterField() {
		if (counterField != null) {
			return counterField.booleanValue();
		}
		return false;
	}
	
	public Metric getMetricEnum() {
		if(metric != -1) {
			return Metric.valueOf(metric);
		}
		return null;
	}
 }
