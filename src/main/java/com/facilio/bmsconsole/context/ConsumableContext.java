package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ConsumableContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;
	
	private InventryContext inventory;
	public InventryContext getInventory() {
		return inventory;
	}
	public void setInventory(InventryContext inventory) {
		this.inventory = inventory;
	}
	
	private InventoryCostContext inventoryCost;
	public InventoryCostContext getInventoryCost() {
		return inventoryCost;
	}
	public void setInventoryCost(InventoryCostContext inventoryCost) {
		this.inventoryCost = inventoryCost;
	}
	
	private double quantityConsumed;
	public double getQuantityConsumed() {
		return quantityConsumed;
	}
	public void setQuantityConsumed(double quantityConsumed) {
		this.quantityConsumed = quantityConsumed;
	}
}
