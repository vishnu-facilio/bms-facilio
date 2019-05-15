package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;

public class InventoryRequestLineItemContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long inventoryRequestId = -1;
	
	public long getInventoryRequestId() {
		return inventoryRequestId;
	}
	public void setInventoryRequestId(long inventoryRequestId) {
		this.inventoryRequestId = inventoryRequestId;
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
	
	
	private long parentId;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<Long> assetIds;

	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}

	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public static enum Status {
		ISSUED(),
		YET_TO_BE_ISSUED(),
		RETURNED()
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

	private double usedQuantity;
	
	public double getUsedQuantity() {
		return usedQuantity;
	}
	public void setUsedQuantity(double usedQuantity) {
		this.usedQuantity = usedQuantity;
	}
	public WorkorderItemContext constructWorkOrderItemContext() throws Exception{
		WorkorderItemContext woItem = new WorkorderItemContext();
		ItemContext item = ItemsApi.getItemsForTypeAndStore(this.getStoreRoomId(), this.getItemType().getId());
		woItem.setItem(item);
		woItem.setParentId(this.getParentId());
		woItem.setAssetIds(this.getAssetIds());
		woItem.setQuantity(this.getQuantity());
		woItem.setRequestedLineItem(this);
		return woItem;
	}
	
	public WorkorderToolsContext constructWorkOrderToolContext() throws Exception {
		WorkorderToolsContext woTool = new WorkorderToolsContext();
		ToolContext tool = ToolsApi.getToolsForTypeAndStore(this.getStoreRoomId(), this.getItemType().getId());
		woTool.setTool(tool);
		woTool.setParentId(this.getParentId());
		woTool.setAssetIds(this.getAssetIds());
		woTool.setQuantity(this.getQuantity());
		woTool.setDuration(3600000);
		woTool.setRequestedLineItem(this);
		
		return woTool;
	}
	
	public ItemTransactionsContext contructManualItemTransactionContext(User requestedBy) throws Exception {
		ItemTransactionsContext transaction = new ItemTransactionsContext();
		ItemContext item = ItemsApi.getItemsForTypeAndStore(this.getStoreRoomId(), this.getItemType().getId());
		transaction.setItem(item);
		transaction.setIssuedTo(requestedBy);
		transaction.setParentId(requestedBy.getOuid());
		transaction.setTransactionType(3);
		transaction.setTransactionState(2);
		transaction.setQuantity(this.getQuantity());
		if(this.getAsset() != null && this.getAsset().getId() > 0) {
			transaction.setAssetIds(Collections.singletonList(this.getAsset().getId()));
		}
		return transaction;
		
	}
	public ToolTransactionContext contructManualToolTransactionContext(User requestedBy) throws Exception {
		ToolTransactionContext transaction = new ToolTransactionContext();
		ToolContext tool = ToolsApi.getToolsForTypeAndStore(this.getStoreRoomId(), this.getToolType().getId());
		transaction.setTool(tool);
		transaction.setIssuedTo(requestedBy);
		transaction.setParentId(requestedBy.getOuid());
		transaction.setTransactionType(3);
		transaction.setTransactionState(2);
		transaction.setQuantity(this.getQuantity());
		
		if(this.getAsset() != null && this.getAsset().getId() > 0) {
			transaction.setAssetIds(Collections.singletonList(this.getAsset().getId()));
		}
		return transaction;
		
	}
	
	private long storeRoomId;

	public long getStoreRoomId() {
		return storeRoomId;
	}
	public void setStoreRoomId(long storeRoomId) {
		this.storeRoomId = storeRoomId;
	}

	
	
}
