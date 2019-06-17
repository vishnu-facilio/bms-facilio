package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class WorkOrderServiceContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;

	private double cost = -1;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	private long parentId = -1;
	
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private ServiceContext service;
	
	public ServiceContext getService() {
		return service;
	}

	public void setService(ServiceContext service) {
		this.service = service;
	}
	
	private long vendor;

	public long getVendor() {
		return vendor;
	}

	public void setVendor(long vendor) {
		this.vendor = vendor;
	}
	
	private double unitPrice;
	private double quantity;

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	

	
}
