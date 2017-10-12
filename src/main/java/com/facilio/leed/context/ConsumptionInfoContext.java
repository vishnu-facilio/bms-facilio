package com.facilio.leed.context;

import java.util.List;

import com.facilio.bmsconsole.context.EnergyDataContext;
import com.facilio.bmsconsole.device.Device;

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
