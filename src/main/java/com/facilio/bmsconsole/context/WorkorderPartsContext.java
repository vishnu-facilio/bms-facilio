package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class WorkorderPartsContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;
	long parentId, modifiedTime, ttime;
	int quantity;
	double cost;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	InventoryContext part;
	
	public InventoryContext getPart() {
		return part;
	}
	public void setPart(InventoryContext part) {
		this.part = part;
	}
	public long getPartId() {
		// TODO Auto-generated method stub
		if (part != null) {
			return part.getId();
		}
		return -1;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
