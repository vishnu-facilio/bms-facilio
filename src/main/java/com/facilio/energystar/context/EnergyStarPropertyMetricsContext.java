package com.facilio.energystar.context;

public class EnergyStarPropertyMetricsContext {

	long id = -1;
	long orgId = -1;
	long propertyId = -1;
	String target;
	String nationalMedian;
	Property_Metrics metric;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getNationalMedian() {
		return nationalMedian;
	}
	public void setNationalMedian(String nationalMedian) {
		this.nationalMedian = nationalMedian;
	}
	public int getMetric() {
		if(metric != null) {
			return metric.getIntVal();
		}
		return -1;
	}
	public void setMetric(int metric) {
		this.metric = Property_Metrics.getAllMetrics().get(metric);
	}
	
}
