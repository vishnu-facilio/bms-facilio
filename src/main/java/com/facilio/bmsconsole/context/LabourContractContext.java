package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.context.ContractsContext.Status;

public class LabourContractContext extends ContractsContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LabourContractContext() {
		// TODO Auto-generated constructor stub
	}
	
	public LabourContractContext(LabourContractContext labourContract) {
		super(labourContract);
		this.lineItems = labourContract.lineItems;
	}
	
	private List<LabourContractLineItemContext> lineItems;
	public List<LabourContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<LabourContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	public LabourContractContext clone() {
		return new LabourContractContext(this);
	}
}
