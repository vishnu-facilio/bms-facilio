package com.facilio.bmsconsole.modules;

import com.facilio.unitconversion.Metric;

public class NumberField extends FacilioField {
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
	
	public Metric getMetricEnum() {
		if(metric != -1) {
			return Metric.valueOf(metric);
		}
		return null;
	}
 }
