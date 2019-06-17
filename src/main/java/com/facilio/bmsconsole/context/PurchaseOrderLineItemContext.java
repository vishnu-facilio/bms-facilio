package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class PurchaseOrderLineItemContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long poId = -1;
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	
	private InventoryType inventoryType;
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
	
	private ItemTypesContext itemType;
	public ItemTypesContext getItemType() {
		return itemType;
	}
	public void setItemType(ItemTypesContext itemType) {
		this.itemType = itemType;
	}
	
	private ToolTypesContext toolType;
	public ToolTypesContext getToolType() {
		return toolType;
	}
	public void setToolType(ToolTypesContext toolType) {
		this.toolType = toolType;
	}
	
	private double quantity = -1;
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	private double unitPrice = -1;
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public static PurchaseOrderLineItemContext from(PurchaseRequestLineItemContext prItem) {
		PurchaseOrderLineItemContext poItem = null;
		if (prItem != null) {
			poItem = new PurchaseOrderLineItemContext();
			poItem.setInventoryType(prItem.getInventoryType());
			poItem.setItemType(prItem.getItemType());
			poItem.setToolType(prItem.getToolType());
			poItem.setQuantity(prItem.getQuantity());
			poItem.setUnitPrice(prItem.getUnitPrice());
		}
		return poItem;
	}
	
	private double quantityReceived = -1;

	public double getQuantityReceived() {
		return quantityReceived;
	}

	public void setQuantityReceived(double quantityReceived) {
		this.quantityReceived = quantityReceived;
	}

	List<PurchasedItemContext> purchasedItems;

	public List<PurchasedItemContext> getPurchasedItems() {
		return purchasedItems;
	}

	public void setPurchasedItems(List<PurchasedItemContext> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}

	List<PurchasedToolContext> purchasedTools;

	public List<PurchasedToolContext> getPurchasedTools() {
		return purchasedTools;
	}

	public void setPurchasedTools(List<PurchasedToolContext> purchasedTools) {
		this.purchasedTools = purchasedTools;
	}
	
	private int noOfSerialNumbers = -1;
	public int getNoOfSerialNumbers() {
		return noOfSerialNumbers;
	}
	public void setNoOfSerialNumbers(int noOfSerialNumbers) {
		this.noOfSerialNumbers = noOfSerialNumbers;
	}
	
	private ServiceContext service;
	public ServiceContext getService() {
		return service;
	}
	public void setService(ServiceContext service) {
		this.service = service;
	}

	private double quantityUsed;
	public double getQuantityUsed() {
		return quantityUsed;
	}
	public void setQuantityUsed(double quantityUsed) {
		this.quantityUsed = quantityUsed;
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
}
