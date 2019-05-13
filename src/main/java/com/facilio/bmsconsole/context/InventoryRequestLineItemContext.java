package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

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
	
	private ItemContext item;
	
	public ItemContext getItem() {
		return item;
	}
	public void setItem(ItemContext item) {
		this.item = item;
	}

	private ToolContext tool;
	
	public ToolContext getTool() {
		return tool;
	}
	public void setTool(ToolContext tool) {
		this.tool = tool;
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
	public WorkorderItemContext constructWorkOrderItemContext() {
		WorkorderItemContext woItem = new WorkorderItemContext();
		woItem.setItem(this.getItem());
		woItem.setParentId(this.getParentId());
		woItem.setAssetIds(this.getAssetIds());
		woItem.setQuantity(this.getQuantity());
		woItem.setRequestedLineItem(this);
		return woItem;
	}
	
	public WorkorderToolsContext constructWorkOrderToolContext() {
		WorkorderToolsContext woTool = new WorkorderToolsContext();
		woTool.setTool(this.getTool());
		woTool.setParentId(this.getParentId());
		woTool.setAssetIds(this.getAssetIds());
		woTool.setQuantity(this.getQuantity());
		woTool.setDuration(3600000);
		woTool.setRequestedLineItem(this);
		
		return woTool;
	}
}
