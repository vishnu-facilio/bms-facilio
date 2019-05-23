package com.facilio.bmsconsole.context;

import java.util.List;

public class ServiceContractContext extends ContractsContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ServiceContractLineItemContext> lineItems;
	public List<ServiceContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<ServiceContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
}
