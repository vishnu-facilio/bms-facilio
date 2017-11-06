package com.facilio.leed.context;

import com.facilio.bmsconsole.context.ReadingContext;

public class ConsumptionInfoContext extends ReadingContext {

	private long startTime;
	public long getStartTime() {
		return this.startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long consumptionId;
	public long getConsumptionId() {
		return consumptionId;
	}
	public void setConsumptionId(long consumptionId) {
		this.consumptionId = consumptionId;
	}
	
}
