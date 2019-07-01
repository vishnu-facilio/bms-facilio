package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RCAEvent extends MLAnomalyEvent 
{

	private static final long serialVersionUID = 1L;
	private long parentId;
	
	public void setParentId(long parentId)
	{
		this.parentId = parentId;
	}
	public long getParentId()
	{
		return parentId;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) {
		if (add && baseAlarm == null) 
		{
			baseAlarm = new RCAAlarm();
		}
		
		RCAAlarm rcaAlarm = (RCAAlarm) baseAlarm;
		super.updateAlarmContext(baseAlarm, add);
		rcaAlarm.setParentId(parentId);
		
		return baseAlarm;
	}
	
	@Override
	@JsonSerialize
	public Type getAlarmType() {
		return Type.RCA_ALARM;
	}

}
