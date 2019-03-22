package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class PurchaseRequestContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private VendorContext vendorContext;
	public VendorContext getVendorContext() {
		return vendorContext;
	}
	public void setVendorContext(VendorContext vendorContext) {
		this.vendorContext = vendorContext;
	}
	
	private StoreRoomContext storeRoomContext;
	public StoreRoomContext getStoreRoomContext() {
		return storeRoomContext;
	}
	public void setStoreRoomContext(StoreRoomContext storeRoomContext) {
		this.storeRoomContext = storeRoomContext;
	}
	
	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	private double totalCost = -1;
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getTotalCost() {
		return totalCost;
	}
	
	public static enum Status {
		REQUESTED(),
		APPROVED(),
		REJECTED();
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
