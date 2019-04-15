package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class LabourContractLineItemContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long labourContractId = -1;
	
	public long getLabourContractId() {
		return labourContractId;
	}
	public void setLabourContractId(long labourContractId) {
		this.labourContractId = labourContractId;
	}

	private LabourContext labour;
	public LabourContext getLabour() {
		return labour;
	}
	public void setLabour(LabourContext labour) {
		this.labour = labour;
	}
	
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
}
