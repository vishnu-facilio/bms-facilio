package com.facilio.bmsconsole.context;

import java.util.List;

public class PurchaseContractContext extends ContractsContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PurchaseContractContext() {
		// TODO Auto-generated constructor stub
	}
	
	public PurchaseContractContext(PurchaseContractContext purchaseContract) {
		super(purchaseContract);
		this.lineItems = purchaseContract.lineItems;
	}
	
	private List<PurchaseContractLineItemContext> lineItems;
	public List<PurchaseContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	public PurchaseContractContext clone() {
		return new PurchaseContractContext(this);
	}
	
}
