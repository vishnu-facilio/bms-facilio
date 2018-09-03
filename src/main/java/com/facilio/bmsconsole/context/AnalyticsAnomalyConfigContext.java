package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;


public class AnalyticsAnomalyConfigContext extends ModuleBaseWithCustomFields {
	private long meterId;
	private String dimension1Buckets;
	private String dimension2Buckets;
	private String dimension1Value;
	private String dimension2Value;
	private String xAxisDimension;
	private String yAxisDimension;
	private Double constant1;
	private Double constant2;
	private Double maxDistance;
	
	public String getDimension1Buckets() {
		return dimension1Buckets;
	}
	public void setDimension1Buckets(String dimension1Buckets) {
		this.dimension1Buckets = dimension1Buckets;
	}
	public String getDimension2Buckets() {
		return dimension2Buckets;
	}
	public void setDimension2Buckets(String dimension2Buckets) {
		this.dimension2Buckets = dimension2Buckets;
	}
	public String getDimension1Value() {
		return dimension1Value;
	}
	public void setDimension1Value(String dimension1Value) {
		this.dimension1Value = dimension1Value;
	}
	public String getDimension2Value() {
		return dimension2Value;
	}
	public void setDimension2Value(String dimension2Value) {
		this.dimension2Value = dimension2Value;
	}
	public String getxAxisDimension() {
		return xAxisDimension;
	}
	public void setxAxisDimension(String xAxisDimension) {
		this.xAxisDimension = xAxisDimension;
	}
	public String getyAxisDimension() {
		return yAxisDimension;
	}
	public void setyAxisDimension(String yAxisDimension) {
		this.yAxisDimension = yAxisDimension;
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}

	public Double getConstant1() {
		return constant1;
	}
	public void setConstant1(Double constant1) {
		this.constant1 = constant1;
	}
	public Double getConstant2() {
		return constant2;
	}
	public void setConstant2(Double constant2) {
		this.constant2 = constant2;
	}
	public Double getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("[meterId = ");
			buf.append(meterId);
			buf.append(", dimension1Buckets = ");
			buf.append(dimension1Buckets);
			buf.append(", dimension1Value = ");
			buf.append(dimension1Value);
			buf.append(", dimension2Buckets = ");
			buf.append(dimension2Buckets);
			buf.append(", dimension2Value = ");
			buf.append(dimension2Value);
			buf.append(", xAxisDimension = ");
			buf.append(xAxisDimension);
			buf.append(", yAxisDimension = ");
			buf.append(yAxisDimension);
			buf.append(", constant1 = ");
			buf.append(constant1);
			buf.append(", constant2 = ");
			buf.append(constant2);
			buf.append(", maxDistance = ");
			buf.append(maxDistance);

			return buf.toString();
	}
}