package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

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
	
	public static List<PurchaseOrderLineItemContext> from(List<PurchaseRequestLineItemContext> lineItems) {
		List<PurchaseOrderLineItemContext> items = new ArrayList<PurchaseOrderLineItemContext>();
		if (CollectionUtils.isNotEmpty(lineItems)) {
			for (PurchaseRequestLineItemContext prItem : lineItems) {
				items.add(PurchaseOrderLineItemContext.from(prItem));
			}
		}
		return items;
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
}
