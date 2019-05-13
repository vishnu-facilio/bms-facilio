package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ShipmentLineItemContext extends ModuleBaseWithCustomFields{
	
	private long shipment;
	public long getShipment() {
		return shipment;
	}
	public void setShipment(long shipment) {
		this.shipment = shipment;
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
}
