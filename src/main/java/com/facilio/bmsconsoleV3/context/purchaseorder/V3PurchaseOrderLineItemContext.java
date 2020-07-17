package com.facilio.bmsconsoleV3.context.purchaseorder;

import java.util.List;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.v3.context.V3Context;

public class V3PurchaseOrderLineItemContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	
	private Long poId;
	private InventoryType inventoryType;
	private V3ItemTypesContext itemType;
	private V3ToolTypesContext toolType;
	private Double quantity;
	private Double unitPrice;
	private Double cost;
	private Double quantityReceived;
	List<PurchasedItemContext> purchasedItems;
	List<PurchasedToolContext> purchasedTools;
	private Integer noOfSerialNumbers;
	private Double quantityUsed;
	
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
			poItem.setRemarks(prItem.getRemarks());
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
	
	private ServiceContext service;
	public ServiceContext getService() {
		return service;
	}
	public void setService(ServiceContext service) {
		this.service = service;
	}
	
	public String remarks;
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public WorkOrderServiceContext constructWorkOrderServiceContext(long parentId) throws Exception {
		WorkOrderServiceContext woService = new WorkOrderServiceContext();
		woService.setService(this.getService());
		woService.setParentId(parentId);
		woService.setQuantity(this.getQuantity());
		woService.setUnitPrice(this.getUnitPrice());
		woService.setCost(this.getQuantity() * this.getUnitPrice());
		return woService;
	}
	
	public Long getPoId() {
		return poId;
	}
	public void setPoId(Long poId) {
		this.poId = poId;
	}
	public V3ItemTypesContext getItemType() {
		return itemType;
	}
	public void setItemType(V3ItemTypesContext itemType) {
		this.itemType = itemType;
	}
	public V3ToolTypesContext getToolType() {
		return toolType;
	}
	public void setToolType(V3ToolTypesContext toolType) {
		this.toolType = toolType;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
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
