package com.facilio.bmsconsoleV3.context.purchaserequest;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemsParentModuleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;

import java.util.ArrayList;
import java.util.List;

public class V3PurchaseRequestContext extends BaseLineItemsParentModuleContext {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private V3VendorContext vendor;
	private V3PurchaseOrderContext purchaseOrder;
	private V3StoreRoomContext storeRoom;
	private Status status;
	private List<V3PurchaseRequestLineItemContext> lineItems;
	private Long requestedTime;
	private Long requiredTime;
	private LocationContext shipToAddress;
	private LocationContext billToAddress;
	private User requestedBy;
	private Boolean isPoCreated;
	private List<PrAssociatedTermsContext> termsAssociated;


	public List<PrAssociatedTermsContext> getTermsAssociated() {
		return termsAssociated;
	}

	public void setTermsAssociated(List<PrAssociatedTermsContext> termsAssociated) {
		this.termsAssociated = termsAssociated;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public Status getStatusEnum() {
		return status;
	}
	public Integer getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return null;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(int status) {
		this.status = Status.valueOf(status);
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

	public LocationContext getShipToAddress() {
		return shipToAddress;
	}

	public void setShipToAddress(LocationContext location) {
		this.shipToAddress = location;
	}


	public LocationContext getBillToAddress() {
		return billToAddress;
	}

	public void setBillToAddress(LocationContext location) {
		this.billToAddress = location;
	}

	public void addLineItem(V3PurchaseRequestLineItemContext lineItem) {
		if (lineItem == null) {
			return;
		}
		if (this.lineItems == null) {
			this.lineItems = new ArrayList<V3PurchaseRequestLineItemContext>();
		}
		this.lineItems.add(lineItem);
	}

	public User getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	public V3VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(V3VendorContext vendor) {
		this.vendor = vendor;
	}
	public V3PurchaseOrderContext getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(V3PurchaseOrderContext purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	public V3StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(V3StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}

	public List<V3PurchaseRequestLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<V3PurchaseRequestLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public Long getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(Long requestedTime) {
		this.requestedTime = requestedTime;
	}
	public Long getRequiredTime() {
		return requiredTime;
	}

	public void setRequiredTime(Long requiredTime) {
		this.requiredTime = requiredTime;
	}

	public Boolean getIsPoCreated() {
		return isPoCreated;
	}

	public void setIsPoCreated(Boolean isPoCreated) {
		this.isPoCreated = isPoCreated;
	}

	public Boolean isPoCreated() {
		if (isPoCreated != null) {
			return isPoCreated.booleanValue();
		}
		return false;
	}

}
