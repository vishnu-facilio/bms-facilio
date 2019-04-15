package com.facilio.bmsconsole.context;

import java.util.List;

public class PurchaseContractContext extends ContractsContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PurchaseContractLineItemContext> lineItems;
	public List<PurchaseContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
}
