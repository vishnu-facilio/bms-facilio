package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;

public class MLAlarmOccurenceContext extends AlarmOccurrenceContext 
{
	private static final long serialVersionUID = 1L;
	
	public static enum MLAnomalyType implements FacilioIntEnum
	{
		Anomaly,
		RCA;
		
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static MLAnomalyType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private MLAnomalyType type;
	public void setMLAnomalyType(int type) {
		this.type = MLAnomalyType.valueOf(type);
	}
	public int getMLAnomalyType() {
		if (type != null) {
			return type.getIndex();
		}
		return -1;
	}
	public void setMLAnomalyType(MLAnomalyType type)
	{
		this.type = type;
	}
	public MLAnomalyType getMLAnomalyTypeEnum()
	{
		return type;
	}

	private MLAnomalyAlarm parentAlarm;
	public MLAnomalyAlarm getParentAlarm() {
		return parentAlarm;
	}
	public void setParentAlarm(MLAnomalyAlarm parentAlarm) {
		this.parentAlarm = parentAlarm;
	}

	private MLAlarmOccurenceContext parentOccurrence;
	public MLAlarmOccurenceContext getParentOccurrence() {
		return parentOccurrence;
	}
	public void setParentOccurrence(MLAlarmOccurenceContext parentOccurrence) {
		this.parentOccurrence = parentOccurrence;
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
