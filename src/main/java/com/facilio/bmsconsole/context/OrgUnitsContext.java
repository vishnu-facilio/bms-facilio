package com.facilio.bmsconsole.context;

import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;

public class OrgUnitsContext {

	long id = -1;
	long orgid = -1;
	int metric = -1;
	int unit = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	public int getMetric() {
		return metric;
	}
	public void setMetric(int metric) {
		this.metric = metric;
	}
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public Metric getMetricEnum() {
		return  metric != -1 ?  Metric.valueOf(metric) : null;
	}
	public Unit getUnitEnum() {
		return unit != -1 ?  Unit.valueOf(unit) : null;
	}
}
