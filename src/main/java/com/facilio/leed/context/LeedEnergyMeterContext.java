package com.facilio.leed.context;

import com.facilio.bmsconsole.device.Device;

public class LeedEnergyMeterContext extends Device {

	private long fuelType;
	private long meterId;
	
	public long getFuelType()
	{
		return fuelType;
	}
	public void setDisplayName(long fuelType)
	{
		this.fuelType = fuelType;
	}
	
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	
}
