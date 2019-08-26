package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RCAEvent extends MLAnomalyEvent 
{

	private static final long serialVersionUID = 1L;
	private long parentid;
	
	public void setparentid(long parentid)
	{
		this.parentid = parentid;
	}
	public long getparentid()
	{
		return parentid;
	}
	
	@Override
	public String constructMessageKey() {
		if (getResource() != null) {
			return "Anomaly_RCA_" + getResource().getId();	
		}
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (add && baseAlarm == null) 
		{
			baseAlarm = new RCAAlarm();
		}
		
		RCAAlarm rcaAlarm = (RCAAlarm) baseAlarm;
		super.updateAlarmContext(baseAlarm, add);
		rcaAlarm.setparentid(parentid);
		
		return baseAlarm;
	}
	
	@Override
	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.RCA_ALARM;
	}

}
