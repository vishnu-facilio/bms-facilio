package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class InventoryContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;
	private String name, description, serialNumber,qrVal;
	public String getQrVal() {
		return qrVal;
	}
	public void setQrVal(String qrVal) {
		this.qrVal = qrVal;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	private InventoryCategoryContext category;
	
	public InventoryCategoryContext getCategory() {
		return category;
	}
	public void setCategory(InventoryCategoryContext category) {
		this.category = category;
	}
	private double unitcost=0;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public double getUnitcost() {
		return unitcost;
	}
	public void setUnitcost(double unitcost) {
		this.unitcost = unitcost;
	}
	private int quantity,minimumQuantity;
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getMinimumQuantity() {
		return minimumQuantity;
	}
	public void setMinimumQuantity(int minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}
	private InventoryVendorContext vendor;
	public InventoryVendorContext getVendor() {
		return vendor;
	}
	public void setVendor(InventoryVendorContext vendor) {
		this.vendor = vendor;
	}
	public long getSpaceId() {
		// TODO Auto-generated method stub
		if (space != null) {
			return space.getId();
		}
		return -1;
	}
	private long modifiedTime=-1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public long getLocalId() {
		return super.getLocalId();
	}
	public void setLocalId(long localId) {
		super.setLocalId(localId);
	}
}
