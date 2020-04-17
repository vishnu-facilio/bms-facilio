package com.facilio.energystar.context;

import com.facilio.energystar.context.EnergyStarCustomerContext.Data_Exchange_Mode;

public class EnergyStarMeterContext {

	long id;
	long orgId;
	long propertyId;
	Data_Exchange_Mode type;
	long meterId;
	long meterDataModuleId;
	long energyStarMeterId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getPropertyId() {
		return propertyId;
	}
	public void setProperyId(long properyId) {
		this.propertyId = properyId;
	}
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = Data_Exchange_Mode.getAllAppTypes().get(type);
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public long getMeterDataModuleId() {
		return meterDataModuleId;
	}
	public void setMeterDataModuleId(long meterDataModuleId) {
		this.meterDataModuleId = meterDataModuleId;
	}
	public long getEnergyStarMeterId() {
		return energyStarMeterId;
	}
	public void setEnergyStarMeterId(long energyStarMeterId) {
		this.energyStarMeterId = energyStarMeterId;
	}
}
