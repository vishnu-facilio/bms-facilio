package com.facilio.leed.context;

import com.facilio.bmsconsole.context.EnergyDataContext;

public class ConsumptionInfoContext extends EnergyDataContext {

	private long startTime;
	private long consumptionId;
	
	public long getStartTime()
	{
		return this.startTime;
	}
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	public long getConsumptionId()
	{
		return consumptionId;
	}
	public void setConsumptionId(long consumptionId)
	{
		this.consumptionId = consumptionId;
	}
	
}
