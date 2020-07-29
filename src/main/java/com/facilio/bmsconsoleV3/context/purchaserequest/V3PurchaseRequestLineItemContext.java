package com.facilio.bmsconsoleV3.context.purchaserequest;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsoleV3.context.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.V3ToolTypesContext;
import com.facilio.v3.context.V3Context;

public class V3PurchaseRequestLineItemContext extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private V3PurchaseRequestContext prid;
	private InventoryType inventoryType;
	private V3ItemTypesContext itemType;
	private V3ToolTypesContext toolType;
	private Double quantity;
	private Double unitPrice;
	private Double cost;
	public String remarks;
	private ServiceContext service;
	
	public V3PurchaseRequestContext getPrid() {
		return prid;
	}
	public void setPrid(V3PurchaseRequestContext prid) {
		this.prid = prid;
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

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks; 
	}
	public ServiceContext getService() {
		return service;
	}
	public void setService(ServiceContext service) {
		this.service = service;
	}
}
