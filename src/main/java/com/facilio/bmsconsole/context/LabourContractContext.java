package com.facilio.bmsconsole.context;

import java.util.List;

public class LabourContractContext extends ContractsContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<LabourContractLineItemContext> lineItems;
	public List<LabourContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<LabourContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
}
