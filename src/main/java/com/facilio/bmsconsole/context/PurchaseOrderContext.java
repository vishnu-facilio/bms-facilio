package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class PurchaseOrderContext extends ModuleBaseWithCustomFields {

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
	
	private VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendorContext) {
		this.vendor = vendorContext;
	}
	
	private StoreRoomContext storeRoom;
	
	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(StoreRoomContext storeroom) {
		this.storeRoom = storeroom;
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
	
	private List<PurchaseOrderLineItemContext> lineItems;
	public List<PurchaseOrderLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseOrderLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	private ReceivableContext receivableContext;
	public ReceivableContext getReceivableContext() {
		return receivableContext;
	}
	public void setReceivableContext(ReceivableContext receivableContext) {
		this.receivableContext = receivableContext;
	}
	
	public static PurchaseOrderContext fromPurchaseRequest(List<PurchaseRequestContext> list) {
		PurchaseOrderContext purchaseOrderContext = new PurchaseOrderContext();
		purchaseOrderContext.setName("Sample");
		purchaseOrderContext.setStatus(Status.REQUESTED);
		purchaseOrderContext.setDescription("sample");
		long vendorId = -1;
		long storeRoomId = -1;
		
		for(PurchaseRequestContext pr : list) {
			if(vendorId == -1 && pr.getVendor() != null) {
				purchaseOrderContext.setVendor(pr.getVendor());
				vendorId = 	pr.getVendor().getId();
			}
			if(storeRoomId == -1 && pr.getStoreRoom() != null) {
				purchaseOrderContext.setStoreRoom(pr.getStoreRoom());
				storeRoomId = pr.getStoreRoom().getId();
			}
		 	
			if(pr.getVendor() != null && pr.getVendor().getId() != vendorId) {
				
				throw new IllegalArgumentException("Cannot create single PO for multiple vendors");
			}
			if(pr.getStoreRoom() != null && pr.getStoreRoom().getId() != storeRoomId) {
				
				throw new IllegalArgumentException("Cannot create single PO for multiple storeroom items");
			}
		}
		
		return purchaseOrderContext;
	}
	
	public static enum Status {
		REQUESTED(),
		APPROVED(),
		REJECTED(),
		ORDERED(),
		PARTIALLY_RECEIVED(),
		RECEIVED_COMPLETED();
		
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
