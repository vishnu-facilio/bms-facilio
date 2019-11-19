package com.facilio.bmsconsole.context;

public class MLAnomalyAlarm extends BaseAlarmContext {

	private static final long serialVersionUID = 1L;
	
	private long energyDataFieldid;
	private long upperAnomalyFieldid;
	private double actualValue;
	private double adjustedUpperBoundValue;
	private long readingTime;
	protected long mlid;
	
	public void setEnergyDataFieldid(long energyDataFieldid)
	{
		this.energyDataFieldid = energyDataFieldid;
	}
	
	public long getEnergyDataFieldid()
	{
		return energyDataFieldid;
	}
	
	public void setUpperAnomalyFieldid(long upperAnomalyFieldid)
	{
		this.upperAnomalyFieldid = upperAnomalyFieldid;
	}
	
	public long getUpperAnomalyFieldid()
	{
		return upperAnomalyFieldid;
	}

	public double getActualValue() {
		return actualValue;
	}

	public void setActualValue(double actualValue) {
		this.actualValue = actualValue;
	}

	public double getAdjustedUpperBoundValue() {
		return adjustedUpperBoundValue;
	}

	public void setAdjustedUpperBoundValue(double adjustedUpperBoundValue) {
		this.adjustedUpperBoundValue = adjustedUpperBoundValue;
	}

	public long getReadingTime() {
		return readingTime;
	}

	public void setReadingTime(long readingTime) {
		this.readingTime = readingTime;
	}

	public long getmlid() {
		return mlid;
	}

	public void setmlid(long mlid) {
		this.mlid = mlid;
	}

	private double ratio;

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	
	public double getRatio()
	{
		return ratio;
	}
}
