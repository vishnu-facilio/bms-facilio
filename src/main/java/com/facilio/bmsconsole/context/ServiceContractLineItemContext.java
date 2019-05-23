package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ServiceContractLineItemContext extends ModuleBaseWithCustomFields{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long serviceContractId = -1;
	private ServiceContext service;
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public long getServiceContractId() {
		return serviceContractId;
	}
	public void setServiceContractId(long serviceContractId) {
		this.serviceContractId = serviceContractId;
	}
	public ServiceContext getService() {
		return service;
	}
	public void setService(ServiceContext service) {
		this.service = service;
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
	
	public double calculateCost() {
		return unitPrice * quantity;
	}
	
}
