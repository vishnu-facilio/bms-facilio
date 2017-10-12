package com.facilio.leed.context;

import java.util.List;

import com.facilio.bmsconsole.context.EnergyDataContext;
import com.facilio.bmsconsole.device.Device;

public class ConsumptionInfoContext extends EnergyDataContext {

	private long startDate;
	private long consumptionId;
	
	public long getstartDate()
	{
		return this.startDate;
	}
	public void setstartDate(long startDate)
	{
		this.startDate = startDate;
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
