package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class InventoryRequestContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	private List<InventoryRequestLineItemContext> lineItems;
	public List<InventoryRequestLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<InventoryRequestLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	public static enum Status {
		REQUESTED(),
		APPROVED(),
		REJECTED(),
		PARTIALLY_APPROVED(),
		PARTIALLY_REJECTED(),
		ISSUED()
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
	
	private long requestedTime = -1;
	public long getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}
	
	private long requiredTime = -1;
	public long getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(long requiredTime) {
		this.requiredTime = requiredTime;
	}
	
	public void addLineItem(InventoryRequestLineItemContext lineItem) {
		if (lineItem == null) {
			return;
		}
		if (this.lineItems == null) {
			this.lineItems = new ArrayList<InventoryRequestLineItemContext>();
		}
		this.lineItems.add(lineItem);
	}
	
	private User requestedBy;

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	
	private long parentId = -1;
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
	
	private StoreRoomContext storeRoom;
	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}
	
	
}
