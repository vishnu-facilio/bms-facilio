package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class V3PurchaseOrderLineItemContext extends BaseLineItemContext {
	
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	String name;
	private V3PurchaseOrderContext purchaseOrder;
	private InventoryType inventoryType;
	private Double quantityReceived;
	List<PurchasedItemContext> purchasedItems;
	List<PurchasedToolContext> purchasedTools;
	private Integer noOfSerialNumbers;
	private Double quantityUsed;


	public V3PurchaseOrderContext getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(V3PurchaseOrderContext purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
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

	public static V3PurchaseOrderLineItemContext from(V3PurchaseRequestLineItemContext prItem) {
		V3PurchaseOrderLineItemContext poItem = null;
		if (prItem != null) {
			poItem = new V3PurchaseOrderLineItemContext();
			poItem.setInventoryType(prItem.getInventoryType());
			poItem.setItemType(prItem.getItemType());
			poItem.setToolType(prItem.getToolType());
			poItem.setService(prItem.getService());
			poItem.setQuantity(prItem.getQuantity());
			poItem.setUnitPrice(prItem.getUnitPrice());
			poItem.setDescription(prItem.getDescription());
			poItem.setTaxAmount(prItem.getTaxAmount());
			poItem.setTax(prItem.getTax());
			poItem.setUnitOfMeasure(prItem.getUnitOfMeasure());
		}
		return poItem;
	}


	public List<PurchasedItemContext> getPurchasedItems() {
		return purchasedItems;
	}

	public void setPurchasedItems(List<PurchasedItemContext> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}

	public List<PurchasedToolContext> getPurchasedTools() {
		return purchasedTools;
	}
	public void setPurchasedTools(List<PurchasedToolContext> purchasedTools) {
		this.purchasedTools = purchasedTools;
	}

	public Double getQuantityReceived() {
		return quantityReceived;
	}
	public void setQuantityReceived(Double quantityReceived) {
		this.quantityReceived = quantityReceived;
	}
	public Integer getNoOfSerialNumbers() {
		return noOfSerialNumbers;
	}
	public void setNoOfSerialNumbers(Integer noOfSerialNumbers) {
		this.noOfSerialNumbers = noOfSerialNumbers;
	}
	public Double getQuantityUsed() {
		return quantityUsed;
	}
	public void setQuantityUsed(Double quantityUsed) {
		this.quantityUsed = quantityUsed;
	}
}
