package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.List;

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
	
	private VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	private PurchaseOrderContext purchaseOrder;
	public PurchaseOrderContext getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(PurchaseOrderContext purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	private StoreRoomContext storeRoom;
	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
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
	
	private List<PurchaseRequestLineItemContext> lineItems;
	public List<PurchaseRequestLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseRequestLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	public static enum Status {
		REQUESTED(),
		APPROVED(),
		REJECTED(),
		COMPLETED()
		;
		
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
	
	private long requestedTime = -1;
	public long getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}
	
	private long requiredTime = -1;
	public long getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(long requiredTime) {
		this.requiredTime = requiredTime;
	}
	
	private LocationContext shipToAddress;

	public LocationContext getShipToAddress() {
		return shipToAddress;
	}

	public void setShipToAddress(LocationContext location) {
		this.shipToAddress = location;
	}
	
	public long getShipToAddressId() {
		// TODO Auto-generated method stub
		if (shipToAddress != null) {
			return shipToAddress.getId();
		}
		return -1;
	}

	private LocationContext billToAddress;

	public LocationContext getBillToAddress() {
		return billToAddress;
	}

	public void setBillToAddress(LocationContext location) {
		this.billToAddress = location;
	}

	public long getBillToAddressId() {
		// TODO Auto-generated method stub
		if (billToAddress != null) {
			return billToAddress.getId();
		}
		return -1;
	}

	public void addLineItem(PurchaseRequestLineItemContext lineItem) {
		if (lineItem == null) {
			return;
		}
		if (this.lineItems == null) {
			this.lineItems = new ArrayList<PurchaseRequestLineItemContext>();
		}
		this.lineItems.add(lineItem);
	}
	
	private User requestedBy;

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
}
