package com.facilio.bmsconsole.context;

import java.util.List;

public class RentalLeaseContractContext extends ContractsContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<RentalLeaseContractLineItemsContext> lineItems;
	public List<RentalLeaseContractLineItemsContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<RentalLeaseContractLineItemsContext> lineItems) {
		this.lineItems = lineItems;
	}
	public RentalLeaseContractContext() {
		// TODO Auto-generated constructor stub
	}
	
	public RentalLeaseContractContext(RentalLeaseContractContext rentalContract) {
		super(rentalContract);
		this.lineItems = rentalContract.lineItems;
	}
	private RentalLeaseContractType rentalLeaseContractType;
	public RentalLeaseContractType getRentalLeaseContractTypeEnum() {
		return rentalLeaseContractType;
	}
	public int getRentalLeaseContractType() {
		if (rentalLeaseContractType != null) {
			return rentalLeaseContractType.getValue();
		}
		return -1;
	}
	public void setRentalLeaseContractType(int rentalLeaseContractType) {
		this.rentalLeaseContractType = RentalLeaseContractType.valueOf(rentalLeaseContractType);
	}
	
	public void setRentalLeaseContractType(RentalLeaseContractType rentalLeaseContractType) {
		this.rentalLeaseContractType = rentalLeaseContractType;
	}
	public static enum RentalLeaseContractType {
		RENTAL(),
		LEASE()
		
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static RentalLeaseContractType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	
	public RentalLeaseContractContext clone() {
		RentalLeaseContractContext rental = new RentalLeaseContractContext(this);
		rental.setRentalLeaseContractType(this.getRentalLeaseContractType());
		rental.setAssociatedAssets(this.getAssociatedAssets());
		return rental;
	}
}
