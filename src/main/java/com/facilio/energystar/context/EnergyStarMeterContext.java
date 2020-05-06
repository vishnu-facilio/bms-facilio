package com.facilio.energystar.context;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.energystar.context.EnergyStarCustomerContext.Data_Exchange_Mode;

public class EnergyStarMeterContext {

	long id = -1;
	long orgId = -1;
	long propertyId = -1; 
	Data_Exchange_Mode type;
	long meterId = -1;
	long meterDataModuleId = -1;
	long meterDataFieldId = -1;
	String energyStarMeterId;
	String firstBillDate;
	String meta;
	String energyStarPropertyId;
	public String getEnergyStarPropertyId() {
		return energyStarPropertyId;
	}

	@JSON(serialize=false)
	public void setEnergyStarPropertyId(String energyStarPropertyId) {
		this.energyStarPropertyId = energyStarPropertyId;
	}
	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}
	
	public String getFirstBillDate() {
		return firstBillDate;
	}
	public void setFirstBillDate(String firstBillDate) {
		this.firstBillDate = firstBillDate;
	}
	AssetContext meterContext;
	
	public AssetContext getMeterContext() {
		return meterContext;
	}
	public void setMeterContext(AssetContext meterContext) {
		this.meterContext = meterContext;
	}
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
	public void setPropertyId(long properyId) {
		this.propertyId = properyId;
	}
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public Data_Exchange_Mode getTypeEnum() {
		return type;
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
	public String getEnergyStarMeterId() {
		return energyStarMeterId;
	}
	public void setEnergyStarMeterId(String energyStarMeterId) {
		this.energyStarMeterId = energyStarMeterId;
	}
	public long getMeterDataFieldId() {
		return meterDataFieldId;
	}
	public void setMeterDataFieldId(long meterDataFieldId) {
		this.meterDataFieldId = meterDataFieldId;
	}
}
