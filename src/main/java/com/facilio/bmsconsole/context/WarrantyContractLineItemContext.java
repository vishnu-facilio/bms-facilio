package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class WarrantyContractLineItemContext extends ModuleBaseWithCustomFields{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long warrantyContractId = -1;
	private ServiceContext service;
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public long getWarrantyContractId() {
		return warrantyContractId;
	}
	public void setWarrantyContractId(long warrantyContractId) {
		this.warrantyContractId = warrantyContractId;
	}
	public ServiceContext getService() {
		return service;
	}
	public void setService(ServiceContext service) {
		this.service = service;
	}

	private double toolCoverage;
	private double itemCoverage;
	private double labourCoverage;
	public double getToolCoverage() {
		return toolCoverage;
	}
	public void setToolCoverage(double toolCoverage) {
		this.toolCoverage = toolCoverage;
	}
	public double getItemCoverage() {
		return itemCoverage;
	}
	public void setItemCoverage(double itemCoverage) {
		this.itemCoverage = itemCoverage;
	}
	public double getLabourCoverage() {
		return labourCoverage;
	}
	public void setLabourCoverage(double labourCoverage) {
		this.labourCoverage = labourCoverage;
	}
	
	
}
