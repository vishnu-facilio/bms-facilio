package com.facilio.bmsconsole.context;

public class EnergyDataContext {
	
	private long deviceId = -1;
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	
	private long addedTime = -1;
	public long getAddedTime() {
		return addedTime;
	}
	public void setAddedTime(long addedTime) {
		this.addedTime = addedTime;
	}

	private float totalEnergyConsumptionDelta = 0;
	public float getTotalEnergyConsumptionDelta() {
		return totalEnergyConsumptionDelta;
	}

	public void setTotalEnergyConsumptionDelta(float totalEnergyConsumptionDelta) {
		this.totalEnergyConsumptionDelta = totalEnergyConsumptionDelta;
	}
}
