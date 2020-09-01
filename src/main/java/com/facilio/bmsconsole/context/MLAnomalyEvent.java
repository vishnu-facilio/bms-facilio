package com.facilio.bmsconsole.context;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MLAnomalyEvent extends BaseEventContext{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MLAnomalyEvent.class.getName());
	
	@Override
	public String constructMessageKey() {
		if (getResource() != null) {
			return "Anomaly_" + getResource().getId();	
		}
		return null;
	}
	@Override
	public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
		if (add && alarmOccurrence == null) {
			alarmOccurrence = new MLAlarmOccurenceContext();
		}
		MLAlarmOccurenceContext mlAlarmOccurence = (MLAlarmOccurenceContext)alarmOccurrence;
		if(mlAnomalyType!=null)
		{
			mlAlarmOccurence.setMLAnomalyType(mlAnomalyType);
			if(mlAnomalyType.equals(MLAlarmOccurenceContext.MLAnomalyType.RCA))
			{
				mlAlarmOccurence.setParentAlarm((MLAnomalyAlarm) parentEvent.getAlarmOccurrence().getAlarm());
				mlAlarmOccurence.setParentOccurrence((MLAlarmOccurenceContext) parentEvent.getAlarmOccurrence());
				mlAlarmOccurence.setRatio(ratio);
				mlAlarmOccurence.setLowerAnomaly(lowerAnomaly);
				mlAlarmOccurence.setUpperAnomaly(upperAnomaly);
			}
		}

		return super.updateAlarmOccurrenceContext(mlAlarmOccurence, context, add);
	}
	
	@Override
    public BaseEventContext createAdditionClearEvent(AlarmOccurrenceContext alarmOccurrence) {
        if (alarmOccurrence instanceof MLAlarmOccurenceContext) {
        	MLAlarmOccurenceContext context =(MLAlarmOccurenceContext)alarmOccurrence;

        	if(mlAnomalyType!=null && context.getMLAnomalyTypeEnum() != null  && !(context.getMLAnomalyTypeEnum().equals(mlAnomalyType)))
        	{
        		String message = "Anomaly Cleared";
				MLAnomalyEvent event = new MLAnomalyEvent();
				event.setEventMessage(message);
		        event.setResource(this.getResource());
		        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		        event.setReadingTime(this.getReadingTime());
		        event.setCreatedTime(this.getReadingTime());
		        event.setmlid(mlid);
		        return event;
        	}
        }
        return null;
    }
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
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
		if(ratio!=-1)
		{
			anomalyAlarm.setRatio(ratio);
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
	
	private MLAlarmOccurenceContext.MLAnomalyType mlAnomalyType;
	public void setType(MLAlarmOccurenceContext.MLAnomalyType mlAnomalyType)
	{
		this.mlAnomalyType = mlAnomalyType;
	}
	private double ratio;
	public  void setRatio(double ratio)
	{
		this.ratio=ratio;
	}
	public double getRatio()
	{
		return ratio;
	}
	private long parentID;
	public void setParentID(long parentID)
	{
		this.parentID=parentID;
	}
	private double upperAnomaly;
	public void setUpperAnomaly(double upperAnomaly)
	{
		this.upperAnomaly=upperAnomaly;
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
	
	private MLAnomalyEvent parentEvent;
	@JsonSerialize
	public void setParentEvent(MLAnomalyEvent parentEvent)
	{
		this.parentEvent = parentEvent;
	}

}
