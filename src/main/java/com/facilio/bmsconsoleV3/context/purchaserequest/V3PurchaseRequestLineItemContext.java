package com.facilio.bmsconsoleV3.context.purchaserequest;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import lombok.Getter;
import lombok.Setter;

public class V3PurchaseRequestLineItemContext extends BaseLineItemContext {
	
	private static final long serialVersionUID = 1L;

	private V3PurchaseRequestContext purchaseRequest;
	private InventoryType inventoryType;
	@Getter @Setter
	private String name;

	public V3PurchaseRequestContext getPurchaseRequest() {
		return purchaseRequest;
	}

	public void setPurchaseRequest(V3PurchaseRequestContext purchaseRequest) {
		this.purchaseRequest = purchaseRequest;
	}

	public InventoryType getInventoryTypeEnum() {
		return inventoryType;
	}
	public int getInventoryType() {
		if (inventoryType != null) {
			return inventoryType.getValue();
		}
		return -1;
	}
	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = InventoryType.valueOf(inventoryType);
	}
}
