package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MLAnomalyEvent extends BaseEventContext{

	private static final long serialVersionUID = 1L;
	
	@Override
	public String constructMessageKey() {
		if (getResource() != null) {
			return "Anomaly_" + getResource().getId();	
		}
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) {
		if (add && baseAlarm == null) {
			baseAlarm = new MLAnomalyAlarm();
		}
		super.updateAlarmContext(baseAlarm, add);
		MLAnomalyAlarm anomalyAlarm = (MLAnomalyAlarm) baseAlarm;

		if (energyDataFieldid != -1) {
			anomalyAlarm.setEnergyDataFieldid(energyDataFieldid);
		}
		if (upperAnomalyFieldid != -1) {
			anomalyAlarm.setUpperAnomalyFieldid(upperAnomalyFieldid);
		}
		if(actualValue!=-1)
		{
			anomalyAlarm.setActualValue(actualValue);
		}
		if(adjustedUpperBoundValue!=-1)
		{
			anomalyAlarm.setAdjustedUpperBoundValue(adjustedUpperBoundValue);
		}
		if(readingTime!=-1)
		{
			anomalyAlarm.setReadingTime(readingTime);
		}
		if(mlid!=-1)
		{
			anomalyAlarm.setmlid(mlid);
		}
		
		return baseAlarm;
	}
	
	private long energyDataFieldid;
	private long upperAnomalyFieldid;
	
	private double actualValue;
	private double adjustedUpperBoundValue;
	private long readingTime;
	private long mlid;
	
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
	
	
	@Override
	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.ML_ANOMALY_ALARM;
	}

	public long getmlid() {
		return mlid;
	}

	public void setmlid(long mlid) {
		this.mlid = mlid;
	}

}
