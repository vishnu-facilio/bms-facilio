package com.facilio.bmsconsole.context;

public class MLAlarmOccurenceContext extends AlarmOccurrenceContext 
{
	private static final long serialVersionUID = 1L;
	
	public static enum MLAnomalyType
	{
		Anomaly,
		RCA;
	}
	
	private MLAnomalyType type;
	public void setMLAnomalyType(MLAnomalyType type)
	{
		this.type = type;
	}
	public MLAnomalyType getMLAnomalyType()
	{
		return type;
	}
	
	private long parentID;
	public void setParentID(long parentID)
	{
		this.parentID = parentID;
	}
	public long getParentID()
	{
		return parentID;
	}
	
	private double ratio;
	public void setRatio(double ratio)
	{
		this.ratio = ratio;
	}
	public double getRatio()
	{
		return ratio;
	}
	
	private double upperAnomaly;
	public void setUpperAnomaly(double upperAnomaly)
	{
		this.upperAnomaly = upperAnomaly;
	}
	public double getUpperAnomaly()
	{
		return upperAnomaly;
	}
	
	private double lowerAnomaly;
	
	public void setLowerAnomaly(double lowerAnomaly)
	{
		this.lowerAnomaly = lowerAnomaly;
	}
	public double getLowerAnomaly()
	{
		return lowerAnomaly;
	}
	
	@Override
	public Type getTypeEnum() {
		return Type.ANOMALY;
	}

}
