package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class PurchasedItemContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;

	private ItemTypesContext itemType;

	public ItemTypesContext getItemType() {
		return itemType;
	}

	public void setItemType(ItemTypesContext itemType) {
		this.itemType = itemType;
	}
	
	private ItemContext item;
	public ItemContext getItem() {
		return item;
	}
	public void setItem(ItemContext inventory) {
		this.item = inventory;
	}
	
	private double unitcost=-1;
	public double getUnitcost() {
		return unitcost;
	}
	public void setUnitcost(double unitcost) {
		this.unitcost = unitcost;
	}
	
	private double quantity = -1;
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	private double currentQuantity = -1;
	public double getCurrentQuantity() {
		return currentQuantity;
	}
	public void setCurrentQuantity(double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}
	
	private long costDate;
	public long getCostDate() {
		return costDate;
	}
	public void setCostDate(long costDate) {
		this.costDate = costDate;
	}
	
	private long ttime;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private long modifiedTime;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	private String serialNumber;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public Boolean isUsed;
	public Boolean getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}
	public boolean isUsed() {
		if(isUsed != null) {
			return isUsed.booleanValue();
		}
		return false;
	}
}
