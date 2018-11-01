package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;


public class AnalyticsAnomalyConfigContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long meterId;
	private String dimension1Buckets;
	private String dimension2Buckets;
	private String dimension1Value;
	private String dimension2Value;
	private String xAxisDimension;
	private String yAxisDimension;
	private double constant1;
	private double constant2;
	private double maxDistance;
	private double outlierDistance;
	private int historyDays;
	private String startDate;
	private boolean startDateMode;
	private int meterInterval;
	private int clusterSize;
	private int bucketSize;
	
	public long getMeterId() {
		return meterId;
	}


	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}


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


	public double getConstant1() {
		return constant1;
	}


	public void setConstant1(double constant1) {
		this.constant1 = constant1;
	}


	public double getConstant2() {
		return constant2;
	}


	public void setConstant2(double constant2) {
		this.constant2 = constant2;
	}


	public double getMaxDistance() {
		return maxDistance;
	}


	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}


	public double getOutlierDistance() {
		return outlierDistance;
	}


	public void setOutlierDistance(double outlierDistance) {
		this.outlierDistance = outlierDistance;
	}


	public int getHistoryDays() {
		return historyDays;
	}


	public void setHistoryDays(int historyDays) {
		this.historyDays = historyDays;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public boolean getStartDateMode() {
		return startDateMode;
	}


	public void setStartDateMode(boolean startDateMode) {
		this.startDateMode = startDateMode;
	}

	public int getMeterInterval() {
		return meterInterval;
	}


	public void setMeterInterval(int meterInterval) {
		this.meterInterval = meterInterval;
	}
	
	public int getClusterSize() {
		return clusterSize;		
	}
	
	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}
	
	public int getBucketSize() {
		return bucketSize;
	}
	
	public void setBucketSize(int bucketSize) {
		this.bucketSize = bucketSize;
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
			buf.append(", outlierDistance = ");
			buf.append(outlierDistance);
			buf.append(", historyDays = ");
			buf.append(historyDays);
			buf.append(", startDate = ");
			buf.append(startDate);
			buf.append(", meterInterval = ");
			buf.append(meterInterval);
			buf.append(", clusterSize = ");
			buf.append(clusterSize);
			buf.append(", bucketSize = ");
			buf.append(bucketSize);
			
			return buf.toString();
	}
}