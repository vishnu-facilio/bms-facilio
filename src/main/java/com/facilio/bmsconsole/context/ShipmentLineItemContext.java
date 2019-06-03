package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.modules.ModuleBaseWithCustomFields;

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
	
	private double unitPrice;
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	private double rate;
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private List<Long> assetIds;
	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}
	
	  public ItemTransactionsContext contructManualItemTransactionContext(ShipmentContext shipment) throws Exception { 
	        ItemTransactionsContext transaction = new ItemTransactionsContext(); 
	        if(shipment.getFromStore() == null) { 
	            throw new IllegalArgumentException("No appropriate Item found"); 
	        } 
	        ItemContext item = ItemsApi.getItemsForTypeAndStore(shipment.getFromStore().getId(), this.getItemType().getId()); 
	        transaction.setItem(item); 
	        transaction.setIssuedTo(shipment.getReceivedBy()); 
		    transaction.setParentId(shipment.getReceivedBy().getId()); 
	        transaction.setShipment(shipment.getId());
	        transaction.setTransactionType(TransactionType.SHIPMENT_STOCK); 
	        transaction.setTransactionState(2); 
	        transaction.setQuantity(this.getQuantity()); 
	        if(this.getAsset() != null && this.getAsset().getId() > 0) { 
	            transaction.setAssetIds(Collections.singletonList(this.getAsset().getId())); 
	        } 
	        return transaction; 
	         
	    } 
	    public ToolTransactionContext contructManualToolTransactionContext(ShipmentContext shipment) throws Exception { 
	    	ToolTransactionContext transaction = new ToolTransactionContext(); 
	        if(shipment.getFromStore() == null) { 
	            throw new IllegalArgumentException("No appropriate Tool found"); 
	        } 
	        ToolContext tool = ToolsApi.getToolsForTypeAndStore(shipment.getFromStore().getId(), this.getToolType().getId()); 
	        transaction.setTool(tool);
	        transaction.setIssuedTo(shipment.getReceivedBy()); 
		    transaction.setParentId(shipment.getReceivedBy().getId()); 
	        transaction.setShipment(shipment.getId());
	        transaction.setTransactionType(TransactionType.SHIPMENT_STOCK); 
	        transaction.setTransactionState(2); 
	        transaction.setQuantity(this.getQuantity()); 
	         
	        if(this.getAsset() != null && this.getAsset().getId() > 0) { 
	            transaction.setAssetIds(Collections.singletonList(this.getAsset().getId())); 
	        } 
	        return transaction; 
	         
	    } 
	
}
